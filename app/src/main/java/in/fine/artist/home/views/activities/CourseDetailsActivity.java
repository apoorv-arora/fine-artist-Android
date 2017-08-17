package in.fine.artist.home.views.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import in.fine.artist.home.R;
import in.fine.artist.home.ZApplication;
import in.fine.artist.home.utils.CommonLib;
import in.fine.artist.home.utils.ImageLoader;
import in.fine.artist.home.utils.VPrefsReader;

/**
 * Created by apoorvarora on 10/07/17.
 */
public class CourseDetailsActivity extends BaseActivity {

    private boolean destroyed = false;
    private Activity mActivity;
    private ZApplication vapp;
    private VPrefsReader prefs;
    private int width;
    private int height;
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        destroyed = false;
        mActivity = this;
        vapp = (ZApplication) getApplication();
        prefs = VPrefsReader.getInstance();
        height = CommonLib.getWindowHeightWidth(mActivity)[0];
        width = CommonLib.getWindowHeightWidth(mActivity)[1];
        imageLoader = new ImageLoader(this, vapp);

        setListeners();
    }

    private void setListeners() {

    }

    @Override
    public void onDestroy() {
        destroyed = true;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        CommonLib.hideKeyboard(mActivity);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
