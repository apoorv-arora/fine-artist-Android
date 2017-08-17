package in.fine.artist.home.utils.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import in.fine.artist.home.utils.CommonLib;

/**
 * Created by apoorvarora on 03/10/16.
 */
public class EditTextBold extends EditText {
    public EditTextBold(Context context) {
        super(context);
        setTypeface(CommonLib.getTypeface(context, CommonLib.FONT_BOLD));
    }

    public EditTextBold(Context context, AttributeSet attr) {
        super(context,attr);
        setTypeface(CommonLib.getTypeface(context, CommonLib.FONT_BOLD));
    }

    public EditTextBold(Context context, AttributeSet attr, int i) {
        super(context,attr,i);
        setTypeface(CommonLib.getTypeface(context, CommonLib.FONT_BOLD));
    }
}