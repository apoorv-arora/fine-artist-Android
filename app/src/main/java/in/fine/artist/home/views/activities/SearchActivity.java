package in.fine.artist.home.views.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.fine.artist.home.R;
import in.fine.artist.home.ZApplication;
import in.fine.artist.home.adapters.CourseCategoryAdapter;
import in.fine.artist.home.adapters.RecommendedAdapter;
import in.fine.artist.home.data.course.CourseBrief;
import in.fine.artist.home.data.course.CourseCategory;
import in.fine.artist.home.utils.CommonLib;
import in.fine.artist.home.utils.ImageLoader;
import in.fine.artist.home.utils.VPrefsReader;
import in.fine.artist.home.utils.networking.UploadManager;
import in.fine.artist.home.utils.networking.UploadManagerCallback;

/**
 * Created by apoorvarora on 06/07/17.
 */
public class SearchActivity extends BaseActivity implements UploadManagerCallback {

    private boolean destroyed = false;
    private Activity mActivity;
    private ZApplication vapp;
    private VPrefsReader prefs;
    private int width;
    private int height;
    private ImageLoader imageLoader;
    private CourseCategoryAdapter mAdapter;
    private GridView gridview;
    private List<CourseCategory> categories;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        UploadManager.getInstance().addCallback(this
                , UploadManager.COURSES_CATEGORIES);

        destroyed = false;
        mActivity = this;
        vapp = (ZApplication) getApplication();
        prefs = VPrefsReader.getInstance();
        height = CommonLib.getWindowHeightWidth(mActivity)[0];
        width = CommonLib.getWindowHeightWidth(mActivity)[1];
        imageLoader = new ImageLoader(this, vapp);

        categories = new ArrayList<>();
        gridview = (GridView) findViewById(R.id.gridview);
        mAdapter = new CourseCategoryAdapter(this, categories);
        gridview.setAdapter(mAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
            }
        });

        setListeners();
        refreshView();
    }

    private void refreshView() {
        UploadManager.getInstance().apiCall(new HashMap<String, String>(), UploadManager.COURSES_CATEGORIES, null, null);
    }

    private void setListeners() {

        findViewById(R.id.back_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.search_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View searchView = findViewById(R.id.search_field);
                searchView.setVisibility(View.VISIBLE);
                searchView.requestFocus();
                CommonLib.showSoftKeyboard(SearchActivity.this, searchView);
            }
        });
    }

    @Override
    public void onDestroy() {
        destroyed = true;
        UploadManager.getInstance().removeCallback(this
                , UploadManager.COURSES_CATEGORIES);
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

    @Override
    public void uploadStarted(int requestType, Object data, Object requestData) {

    }

    @Override
    public void uploadFinished(int requestType, Object data, boolean status, String errorMessage, Object requestData) {
        if (requestType == UploadManager.COURSES_RECOMMENDED) {
            if (!destroyed) {
                findViewById(R.id.progress_view).setVisibility(View.GONE);
                if (data != null && data instanceof List<?> && ((List<?>)data).size() > 0) {
                    categories = (ArrayList<CourseCategory>) data;
                    mAdapter = new CourseCategoryAdapter(mActivity, categories);
                    gridview.setAdapter(mAdapter);
                } else {
                }
            }
        }
    }
}
