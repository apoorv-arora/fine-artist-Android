package in.fine.artist.home.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.crashlytics.android.Crashlytics;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Hashtable;

import in.fine.artist.home.BuildConfig;
import in.fine.artist.home.data.PlaceExtended;

/**
 * Created by apoorvarora on 21/04/17.
 */
public class CommonLib {
    public final static boolean VYOMLOG = false;

    // Font file def follows
    public static String FONT_MEDIUM = "fonts/home_medium.ttf";
    public static String FONT_LIGHT = "fonts/home_light.ttf";
    public static String FONT_REGULAR = "fonts/home_regular.ttf";
    public static String FONT_BOLD = "fonts/home_bold.ttf";
    public static String Icons = "fonts/home_icons.ttf";

    public static final String APP_CONFIG = "bundle/appConfig.json";

    public static final int DIALOG_VANISH = 135;

    /** Preferences Files
     */
    public final static String APP_SETTINGS = "application_settings";
    public final static String ONE_LAUNCH_SETTINGS = "application_settings_once";

    /** Preferences
     */
    public static final String PROPERTY_REG_ID = "regId";
    public static final String PROPERTY_ACCESS_TOKEN = "accessToken";
    public static final String PROPERTY_FIREBASE_TOKEN = "firebaseToken";
    public static final String PROPERTY_USER_ID = "userId";
    public static final String PROPERTY_USER_ADDRESS = "userAddress";
    public static final String PROPERTY_USER_CITY = "userCity";
    public static final String PROPERTY_USER_DISTRICT = "userDistrict";
    public static final String PROPERTY_PLAY_SERVICES_CHECK = "play_service_check";
    public static final String PROPERTY_USER_NAME = "name";
    public static final String PROPERTY_USER_PHONE_NUMBER = "phoneNumber";
    public static final String PROPERTY_USER_PROFILE_PIC = "profilePic";
    public static final String PLACE_LAT = "place_lat";
    public static final String PLACE_LON = "place_lon";
    public static final String PROPERTY_APP_CONFIG = "appConfig";

    /** header keys
     * */
    public static final String HEADER_KEY_TOKEN = "at";
    public static final String HEADER_KEY_USERID = "uid";
    public static final String HEADER_KEY_VERSION = "v";
    public static final String HEADER_KEY_ACCEPT_FORMAT = "Accept";
    public static final String HEADER_KEY_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_KEY_SOURCE = "s";
    public static final String HEADER_KEY_LANGUAGE = "ln";
    public static final String HEADER_KEYBOARD_LANGUAGE = "frln";
    public static final String HEADER_KEY_ENCODING ="Accept-Encoding";
    public static final String HEADER_KEY_AP ="ap";

    public static final long THREE_HOUR_TIMER = 3 * 60 * 60 * 1000;

    /**
     * Intent data variables
     */
    public static final String INTENT_INDEX = "index";
    public static final String INTENT_TITLE = "title";
    public static final String INTENT_URL = "url";
    public static final String INTENT_PRICE = "price";
    public static final String INTENT_STATUS = "status";
    public static final String INTENT_DESCRIPTION = "description";
    public static final String INTENT_ICON = "icon";
    public static final String INTENT_ICON_STRING = "icon_string";
    public static final String INTENT_ICON_COLOR = "icon_color";


    public static final int PLACE_ACT = 1;
    public static final String CLICK_ON_SRC = "src_click";
    public static final String FROM_PLACE_PICKER = "place_picker";
    public static final String SRC_PLACE_ID = "src_place_id";
    public static final String SRC_PLACE_ADDRESS =  "src_place_addr";
    public static final String SRC_PLACE_NAME =  "src_place_name";
    public static final String SRC_PLACE_TYPE =  "src_place_type";
    public static final String DEST_PLACE_TYPE =  "dest_place_type";
    public static final String DEST_PLACE_ID = "dest_place_id";
    public static final String DEST_PLACE_ADDRESS = "dest_place_addr";
    public static final String DEST_PLACE_NAME = "dest_place_name";
    public static final String FROM_ADD_LOAD = "fromAddLoad";

    public static final int PLACE_EXTENDED_DEFAULT =   -1;
    public static final int PLACE_EXTENDED_RECENT = 0;
    public static final int PLACE_EXTENDED_FREQUENT = 1;
    public static final int PLACE_EXTENDED_RESULT = 2;

    public static final int LOGIN_TYPE_GOOGLE = 101;
    public static final int LOGIN_TYPE_FACEBOOK = 102;
    public static final int LOGIN_TYPE_SELF = 103;

    /**
     * API call and stuffs
     */
    public static final int CONNECTION_TIMEOUT = 15; // connection time out in seconds
    public static final String SERVER_URL = BuildConfig.HOST;
    public static final int MULTIPART_TIMEOUT = 140;

    // Hashtable for different type faces
    public static final Hashtable<String, Typeface> typefaces = new Hashtable<String, Typeface>();


    /**
     * Returns the bitmap associated with sampling
     */
    public static Bitmap getBitmap(Context mContext, int resId, int width, int height) throws OutOfMemoryError {
        if (mContext == null)
            return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(mContext.getResources(), resId, options);
        options.inSampleSize = CommonLib.calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        if (!CommonLib.isAndroidL())
            options.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), resId, options);

        return bitmap;
    }

    // Calculate the sample size of bitmaps
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        int inSampleSize = 1;
        double ratioH = (double) options.outHeight / reqHeight;
        double ratioW = (double) options.outWidth / reqWidth;

        int h = (int) Math.round(ratioH);
        int w = (int) Math.round(ratioW);

        if (h > 1 || w > 1) {
            if (h > w) {
                inSampleSize = h >= 2 ? h : 2;

            } else {
                inSampleSize = w >= 2 ? w : 2;
            }
        }
        return inSampleSize;
    }

    // check done before storing the bitmap in the memory
    public static boolean shouldScaleDownBitmap(Context context, Bitmap bitmap) {
        if (context != null && bitmap != null && bitmap.getWidth() > 0 && bitmap.getHeight() > 0) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;
            return ((width != 0 && width / bitmap.getWidth() < 1) || (height != 0 && height / bitmap.getHeight() < 1));
        }
        return false;
    }

    /**
     * Turn drawable resource into byte array.
     *
     * @param context parent context
     * @param id      drawable resource id
     * @return byte array
     */
    public static byte[] getFileDataFromDrawable(Context context, int id) {
        Drawable drawable = ContextCompat.getDrawable(context, id);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Turn drawable into byte array.
     *
     * @param drawable data
     * @return byte array
     */
    public static byte[] getFileDataFromDrawable(Context context, Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        }catch (OutOfMemoryError outOfMemoryError){
            CommonLib.VLog("OutOfMemoryError", " while writing bitmap to bytearrayoutput stream");
        }
        return byteArrayOutputStream.toByteArray();
    }

    // Fetches the typeface to set for the text views
    public static Typeface getTypeface(Context c, String name) {
        synchronized (typefaces) {
            if (!typefaces.containsKey(name)) {
                try {
                    InputStream inputStream = c.getAssets().open(name);
                    File file = createFileFromInputStream(inputStream, name);
                    if (file == null) {
                        return Typeface.DEFAULT;
                    }
                    Typeface t = Typeface.createFromFile(file);
                    typefaces.put(name, t);
                } catch (Exception e) {
                    if (!CommonLib.VYOMLOG) Crashlytics.logException(e);
                    e.printStackTrace();
                    return Typeface.DEFAULT;
                }
            }
            return typefaces.get(name);
        }
    }

    // Creates the file from input stream
    public static File createFileFromInputStream(InputStream inputStream, String name) {

        try {
            File f = File.createTempFile("font", null);
            OutputStream outputStream = new FileOutputStream(f);
            byte buffer[] = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();
            return f;
        } catch (Exception e) {
            if (!CommonLib.VYOMLOG) Crashlytics.logException(e);
            // Logging exception
            e.printStackTrace();
        }

        return null;
    }

    public static void showSoftKeyboard(Context context, View v) {
        v.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * Remove the keyboard explicitly.
     */
    public static void hideKeyBoard(Activity mActivity, View mGetView) {
        try {
            ((InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(mGetView.getRootView().getWindowToken(), 0);
        } catch (Exception e) {
            if (!CommonLib.VYOMLOG) Crashlytics.logException(e);
            e.printStackTrace();
        }
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            try {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isAndroidL() {
        return Build.VERSION.SDK_INT >= 21;
    }

    // RIVIGO Logging end points
    public static void VLog(String Tag, String Message) {
        if (VYOMLOG && Message != null)
            Log.i(Tag, Message);
    }

    public static void VLog(String Tag, float Message) {
        if (VYOMLOG)
            Log.i(Tag, Message + "");
    }

    public static void VLog(String Tag, boolean Message) {
        if (VYOMLOG)
            Log.i(Tag, Message + "");
    }

    public static void VLog(String Tag, int Message) {
        if (VYOMLOG)
            Log.i(Tag, Message + "");
    }

    // Checks if network is available
    public static boolean isNetworkAvailable(Context c) {
        ConnectivityManager connectivityManager = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String formatInRs(int n) {
        return "₹ " + formatNumber(n);
    }

    public static String formatNumber(int n) {
        String formattedString = "";
        try {
            NumberFormat NF = NumberFormat.getInstance();
            formattedString =  NF.format(n);
        }catch (Exception exception){
            formattedString = String.valueOf(n);
        }
        return formattedString;
    }

    public static Bitmap getBitmapFromDisk(String url, Context ctx) {

        Bitmap defautBitmap = null;
        try {
            String filename = constructFileName(url);
            File filePath = new File(ctx.getCacheDir(), filename);

            if (filePath.exists() && filePath.isFile() && !filePath.isDirectory()) {
                FileInputStream fi;
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inPreferredConfig = Bitmap.Config.RGB_565;
                fi = new FileInputStream(filePath);
                defautBitmap = BitmapFactory.decodeStream(fi, null, opts);
            }

        } catch (FileNotFoundException e) {
            if (!CommonLib.VYOMLOG) Crashlytics.logException(e);
            e.printStackTrace();

        } catch (Exception e) {
            if (!CommonLib.VYOMLOG) Crashlytics.logException(e);
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            if (!CommonLib.VYOMLOG) Crashlytics.logException(e);
            e.printStackTrace();
        }

        return defautBitmap;
    }

    public static String constructFileName(String url) {
        return url.replaceAll("/", "_");
    }


    public static void addBitmapToDisk(String url, Bitmap bmp, Context ctx) {
        writeBitmapToDisk(url, bmp, ctx, Bitmap.CompressFormat.PNG);
    }

    public static void writeBitmapToDisk(String url, Bitmap bmp, Context ctx, Bitmap.CompressFormat format) {
        FileOutputStream fos;
        String fileName = constructFileName(url);
        try {
            if (bmp != null) {
                fos = new FileOutputStream(new File(ctx.getCacheDir(), fileName));
                bmp.compress(format, 75, fos);
                fos.close();
            }
        } catch (FileNotFoundException e) {
            if (!CommonLib.VYOMLOG) Crashlytics.logException(e);
            e.printStackTrace();
        } catch (Exception e) {
            if (!CommonLib.VYOMLOG) Crashlytics.logException(e);
            e.printStackTrace();
        }
    }

    public static void requestMultiplePermissions(Context mContext, ArrayList<String> permissionsList, final int permission_code) {
        ActivityCompat.requestPermissions((Activity) mContext, permissionsList.toArray(new String[permissionsList.size()]), permission_code);
    }

    public static void requestMultiplePermissions(Context mContext, String[] permissionArr, final int permission_code) {
        ActivityCompat.requestPermissions((Activity) mContext, permissionArr, permission_code);
    }

    public static float distFrom(double lat1, double lng1, double lat2, double lng2) {
        float[] result = new float[3];
        Location.distanceBetween(lat1, lng1, lat2, lng2, result);
        //conversion to m
        result[0] = result[0] / 1000;

        return result[0];
    }

    public static int [] getWindowHeightWidth(Activity activity) {
        WindowManager wm = (WindowManager)activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return  new int[]{metrics.heightPixels,metrics.widthPixels};
    }

    public static int getColor (Resources resources, int resourceId){
        if (Build.VERSION.SDK_INT >= 23){
            return resources.getColor(resourceId, null);
        } else {
            return resources.getColor(resourceId);
        }
    }

    public static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= 24) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }

    public static boolean isValidPlace(PlaceExtended placeExtended){
        return !(placeExtended == null || TextUtils.isEmpty(placeExtended.getPlaceId()) || TextUtils.isEmpty(placeExtended.getAddress()));
    }

    public static String encodeURL(String url) {
        try {
            return URLEncoder.encode(url, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

}
