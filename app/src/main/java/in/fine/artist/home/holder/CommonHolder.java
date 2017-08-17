package in.fine.artist.home.holder;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import in.fine.artist.home.R;
import in.fine.artist.home.data.course.CourseBrief;
import in.fine.artist.home.utils.ImageLoader;

/**
 * Created by apoorvarora on 06/07/17.
 */
public class CommonHolder extends RecyclerView.ViewHolder {

    public ImageView backgroundImage;
    public TextView title;
    public TextView description;
    public View snippet_recommended_parent;

    public CommonHolder(View view) {
        super(view);
        snippet_recommended_parent = view.findViewById(R.id.snippet_recommended_parent);
        backgroundImage = (ImageView) view.findViewById(R.id.background_image);
        title = (TextView) view.findViewById(R.id.title);
        description = (TextView) view.findViewById(R.id.description);
    }

    public void setValues(ImageLoader loader, Activity mActivity, CourseBrief course) {
        int width = mActivity.getWindowManager().getDefaultDisplay().getWidth();

        loader.setImageFromUrlOrDisk(course.getCoverImageUrl(), backgroundImage, "", width, width, false);
        title.setText(course.getTitle());
        description.setText(course.getShortDescription());
    }

}
