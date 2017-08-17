package in.fine.artist.home.utils.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

import in.fine.artist.home.utils.CommonLib;

/**
 * Created by nik on 10/1/17.
 */

public class OTPEditText extends EditText {
    public OTPEditText(Context context) {
        super(context);
        setTypeface(CommonLib.getTypeface(context, CommonLib.FONT_REGULAR));
    }

    public OTPEditText(Context context, AttributeSet attr) {
        super(context, attr);
        setTypeface(CommonLib.getTypeface(context, CommonLib.FONT_REGULAR));
    }

    public OTPEditText(Context context, AttributeSet attr, int i) {
        super(context, attr, i);
        setTypeface(CommonLib.getTypeface(context, CommonLib.FONT_REGULAR));
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        // do your stuff
        return !((keyCode == KeyEvent.KEYCODE_BACK) &&
                event.getAction() == KeyEvent.ACTION_UP) && super.dispatchKeyEvent(event);
    }
}