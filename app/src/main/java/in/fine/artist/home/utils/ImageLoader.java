package in.fine.artist.home.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.crashlytics.android.Crashlytics;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.RejectedExecutionException;

import in.fine.artist.home.ZApplication;

/**
 * Created by apoorvarora on 10/10/16.
 */
public class ImageLoader {

    private Context mContext;
    private ZApplication vapp;
    private boolean destroyed = false;

    public ImageLoader(Context mContext, ZApplication vapp){
        this.mContext = mContext;
        this.vapp = vapp;
    }

    public void setDestroyed(boolean destroyed){
        this.destroyed = destroyed;
    }

    public void setImageFromUrlOrDisk(final String url, final ImageView imageView, final String type, int width,
                                       int height, boolean useDiskCache) {

        if (TextUtils.isEmpty(url) || url.equalsIgnoreCase("null")) {
            return;
        }

        if (cancelPotentialWork(url, imageView)) {

            GetImage task = new GetImage(url, imageView, width, height, useDiskCache, type);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(mContext.getResources(), vapp.cache.get(url + type), task);
            if (imageView.getParent() != null && imageView.getParent() instanceof ViewGroup
                    && ((ViewGroup) imageView.getParent()).getChildAt(2) != null
                    && ((ViewGroup) imageView.getParent()).getChildAt(2) instanceof ProgressBar) {
                ((ViewGroup) imageView.getParent()).getChildAt(2).setVisibility(View.GONE);
            }
            if (type != null && type.equals("home"));
            else
                imageView.setImageDrawable(asyncDrawable);

            if (vapp.cache.get(url + type) == null) {
                try {
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 1L);
                } catch (RejectedExecutionException e) {
                    if (!CommonLib.VYOMLOG) Crashlytics.logException(e);
                    e.printStackTrace();
                }
            } else if (imageView != null && imageView.getDrawable() != null
                    && ((BitmapDrawable) imageView.getDrawable()).getBitmap() != null) {
                imageView.setBackgroundResource(0);
                if (imageView.getParent() != null && imageView.getParent() instanceof ViewGroup
                        && ((ViewGroup) imageView.getParent()).getChildAt(2) != null
                        && ((ViewGroup) imageView.getParent()).getChildAt(2) instanceof ProgressBar) {
                    ((ViewGroup) imageView.getParent()).getChildAt(2).setVisibility(View.GONE);
                }
            }
        }
    }

    private class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<GetImage> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, GetImage bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference<GetImage>(bitmapWorkerTask);
        }

        public GetImage getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    public boolean cancelPotentialWork(String data, ImageView imageView) {
        final GetImage bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final String bitmapData = bitmapWorkerTask.url;
            if (!bitmapData.equals(data)) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was
        // cancelled
        return true;
    }

    private GetImage getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    private class GetImage extends AsyncTask<Object, Void, Bitmap> {

        String url = "";
        private WeakReference<ImageView> imageViewReference;
        private int width;
        private int height;
        boolean useDiskCache;
        String type;
        Bitmap blurBitmap;

        public GetImage(String url, ImageView imageView, int width, int height, boolean useDiskCache, String type) {
            this.url = url;
            imageViewReference = new WeakReference<ImageView>(imageView);
            this.width = width;
            this.height = height;
            this.useDiskCache = true;// useDiskCache;
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                if (imageView != null && imageView.getParent() != null && imageView.getParent() instanceof ViewGroup
                        && ((ViewGroup) imageView.getParent()).getChildAt(2) != null
                        && ((ViewGroup) imageView.getParent()).getChildAt(2) instanceof ProgressBar)
                    ((ViewGroup) imageView.getParent()).getChildAt(2).setVisibility(View.VISIBLE);
            }
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(Object... params) {
            Bitmap bitmap = null;
            try {

                String url2 = url + type;

                if (destroyed) {
                    return null;
                }

                if (useDiskCache) {
                    bitmap = CommonLib.getBitmapFromDisk(url2, mContext.getApplicationContext());
                }

                if (bitmap == null) {
                    try {
                        BitmapFactory.Options opts = new BitmapFactory.Options();
                        opts.inJustDecodeBounds = true;

                        opts.inSampleSize = CommonLib.calculateInSampleSize(opts, width, height);
                        opts.inJustDecodeBounds = false;

                        if (!TextUtils.isEmpty(url) && !"null".equalsIgnoreCase(url)){
                            bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent(), null, opts);
                        }

                        if (CommonLib.shouldScaleDownBitmap(mContext, bitmap)) {
                            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
                        }

                        if (useDiskCache) {
                            CommonLib.writeBitmapToDisk(url2, bitmap, mContext.getApplicationContext(),
                                    Bitmap.CompressFormat.JPEG);
                        }
                    } catch (MalformedURLException e) {
                        if (!CommonLib.VYOMLOG) Crashlytics.logException(e);
                        e.printStackTrace();
                    } catch (Exception e) {
                        if (!CommonLib.VYOMLOG) Crashlytics.logException(e);
                        e.printStackTrace();
                    } catch (Error e) {
                        if (!CommonLib.VYOMLOG) Crashlytics.logException(e);
                        e.printStackTrace();
                    }
                }

                if (bitmap != null) {
                    synchronized (vapp.cache) {
                        vapp.cache.put(url2, bitmap);
                    }
                }

            } catch (Exception e) {
                if (!CommonLib.VYOMLOG) Crashlytics.logException(e);
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if (!destroyed) {
                if (isCancelled()) {
                    bitmap = null;
                }
                if (imageViewReference != null && bitmap != null) {
                    final ImageView imageView = imageViewReference.get();
                    if (imageView != null) {
                        imageView.setBackgroundResource(0);
                        imageView.setImageBitmap(bitmap);
                        if (imageView.getParent() != null && imageView.getParent() instanceof ViewGroup
                                && ((ViewGroup) imageView.getParent()).getChildAt(2) != null
                                && ((ViewGroup) imageView.getParent()).getChildAt(2) instanceof ProgressBar) {
                            ((ViewGroup) imageView.getParent()).getChildAt(2).setVisibility(View.GONE);
                        }
                    }
                }
            }
        }
    }
}
