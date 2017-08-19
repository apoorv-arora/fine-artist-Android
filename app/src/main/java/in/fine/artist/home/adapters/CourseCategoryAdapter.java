package in.fine.artist.home.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import in.fine.artist.home.R;
import in.fine.artist.home.data.course.CourseBrief;
import in.fine.artist.home.data.course.CourseCategory;

/**
 * Created by apoorvarora on 15/07/17.
 */
public class CourseCategoryAdapter extends BaseAdapter {
    private Context mContext;
    private List<CourseCategory> courseBriefs;
    private LayoutInflater inflater;

    public CourseCategoryAdapter(Context c, List<CourseCategory> courseBriefs) {
        mContext = c;
        this.courseBriefs = courseBriefs;
        inflater = LayoutInflater.from(mContext);
    }

    public int getCount() {
        return courseBriefs == null ? 0 : courseBriefs.size();
    }

    public Object getItem(int position) {
        return courseBriefs == null ? null : courseBriefs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        ImageView imageView;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_category_snippet, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.description = (TextView) convertView.findViewById(R.id.description);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CourseBrief courseBrief = (CourseBrief) getItem(position);
        holder.title.setText(courseBrief.getTitle());
        holder.description.setText(courseBrief.getShortDescription());

        return convertView;
    }

    public class ViewHolder {
        private TextView title;
        private TextView description;
        private ImageView image;
    }

}
