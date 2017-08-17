package in.fine.artist.home.utils.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import in.fine.artist.home.utils.CommonLib;

/**
 * Created by apoorvarora on 18/10/16.
 */
public class IconView extends TextView {
    public IconView(Context context) {
        super(context);
        setTypeface(CommonLib.getTypeface(context, CommonLib.Icons));
    }

    public IconView(Context context, AttributeSet attr) {
        super(context,attr);
        setTypeface(CommonLib.getTypeface(context, CommonLib.Icons));
    }

    public IconView(Context context, AttributeSet attr, int i) {
        super(context,attr,i);
        setTypeface(CommonLib.getTypeface(context, CommonLib.Icons));
    }
}