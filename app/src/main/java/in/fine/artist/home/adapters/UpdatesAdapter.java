package in.fine.artist.home.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import in.fine.artist.home.R;
import in.fine.artist.home.ZApplication;
import in.fine.artist.home.data.BlogUpdate;
import in.fine.artist.home.data.course.CourseBrief;
import in.fine.artist.home.holder.BlogViewHolder;
import in.fine.artist.home.holder.CommonHolder;
import in.fine.artist.home.holder.GifViewHolder;
import in.fine.artist.home.utils.ImageLoader;
import in.fine.artist.home.utils.RandomCallback;

/**
 * Created by apoorvarora on 06/07/17.
 */
public class UpdatesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private Activity mActivity;
    private ImageLoader loader;
    private RandomCallback callback;
    private ArrayList<BlogUpdate> loadList;

    private final int TYPE_LOADING = 0;
    private final int TYPE_NORMAL = 1;

    public UpdatesAdapter(Activity mActivity, ArrayList<BlogUpdate> loadList, RandomCallback callback) {
        this.mActivity = mActivity;
        this.loadList = loadList;
        this.callback = callback;
        loader = new ImageLoader(mActivity, (ZApplication) mActivity.getApplication());
    }

    @Override
    public int getItemViewType(int position){
        if(loadList.get(position) == null){
            return TYPE_LOADING;
        } else {
            return TYPE_NORMAL;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType){
            case TYPE_NORMAL:
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.snippet_recommended, viewGroup, false);
                BlogViewHolder viewHolder = new BlogViewHolder(view);
                viewHolder.snippet_recommended_parent.setOnClickListener(this);
                return new CommonHolder(view);

            case TYPE_LOADING:
                View view_null = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gif, viewGroup, false);
                return new GifViewHolder(view_null);
            default: return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (!(viewHolder instanceof BlogViewHolder))
            return;

        if (loadList.get(position) == null)
            return;

        BlogViewHolder vh = (BlogViewHolder) viewHolder;
        BlogUpdate course = loadList.get(position);
        vh.setValues(loader, mActivity, course);
        vh.snippet_recommended_parent.setTag(position);
    }

    @Override
    public int getItemCount() {
        return loadList == null ? 0 : loadList.size();
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag != null && tag instanceof Integer) {
            int position = (int)tag;
            BlogUpdate course = loadList.get(position);
            callback.randomCallback(course);
        }
    }
}
