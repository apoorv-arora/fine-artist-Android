package in.fine.artist.home.views.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.TextView;

import in.fine.artist.home.R;
import in.fine.artist.home.utils.CommonLib;

/**
 * Created by apoorvarora on 23/05/17.
 */
public class ResponseDialogActivity extends BaseActivity {

    private boolean destroyed = false;
    private boolean isCancellable = true;

    private static final int DIALOG_TYPE_FINANCIAL = 101;
    private static final int DIALOG_TYPE_REFER_EARN = 102;

    private int dialogType, width;

    private int DIALOG_VISIBILITY_TIMEOUT = 3000;    // TIME IN MILLI-SECONDS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response_dialog);
        width = CommonLib.getWindowHeightWidth(this)[1];
        switch (dialogType) {
            case DIALOG_TYPE_FINANCIAL:
            case DIALOG_TYPE_REFER_EARN:
                isCancellable = true;
                break;

            default:
                break;
        }
        setFinishOnTouchOutside(isCancellable);

        ((TextView)findViewById(R.id.title)).setText(getIntent().getStringExtra(CommonLib.INTENT_STATUS));
        String title = getIntent().getStringExtra(CommonLib.INTENT_STATUS);
        ((TextView)findViewById(R.id.title)).setText(title);

        if (getIntent().hasExtra(CommonLib.INTENT_DESCRIPTION) ) {
            ((TextView)findViewById(R.id.description)).setText(getIntent().getStringExtra(CommonLib.INTENT_DESCRIPTION));
        }

        if (getIntent().hasExtra(CommonLib.INTENT_ICON) ) {
            ((TextView)findViewById(R.id.icon)).setText(getIntent().getStringExtra(CommonLib.INTENT_ICON));
        }

        if(getIntent().hasExtra(CommonLib.INTENT_ICON_STRING)){
            ((TextView)findViewById(R.id.icon)).setText(CommonLib.fromHtml(getIntent().getStringExtra(CommonLib.INTENT_ICON_STRING)));
        }

        if (getIntent().hasExtra(CommonLib.INTENT_ICON_COLOR) ) {
            ((TextView)findViewById(R.id.icon)).setTextColor(
                    ContextCompat.getColor(this, getIntent().getExtras().getInt(CommonLib.INTENT_ICON_COLOR)));

        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                setResult(200);
            }
        }, DIALOG_VISIBILITY_TIMEOUT);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // If we've received a touch notification that the user has touched
        // outside the app, finish the activity.
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            return true;
        }

        // Delegate everything else to Activity.
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        if (isCancellable) {
            super.onBackPressed();
        }
    }


    @Override
    public void onDestroy() {
        destroyed = true;
        super.onDestroy();
    }

}
