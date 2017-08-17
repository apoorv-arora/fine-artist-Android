package in.fine.artist.home.views.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import in.fine.artist.home.R;
import in.fine.artist.home.ZApplication;
import in.fine.artist.home.adapters.CourseCategoryAdapter;
import in.fine.artist.home.data.course.CourseBrief;
import in.fine.artist.home.utils.CommonLib;
import in.fine.artist.home.utils.ImageLoader;
import in.fine.artist.home.utils.VPrefsReader;

/**
 * Created by apoorvarora on 06/07/17.
 */
public class SearchActivity extends BaseActivity {

    private boolean destroyed = false;
    private Activity mActivity;
    private ZApplication vapp;
    private VPrefsReader prefs;
    private int width;
    private int height;
    private ImageLoader imageLoader;
    private CourseCategoryAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        destroyed = false;
        mActivity = this;
        vapp = (ZApplication) getApplication();
        prefs = VPrefsReader.getInstance();
        height = CommonLib.getWindowHeightWidth(mActivity)[0];
        width = CommonLib.getWindowHeightWidth(mActivity)[1];
        imageLoader = new ImageLoader(this, vapp);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        List<CourseBrief> briefs = new ArrayList<>();
        CourseBrief brief1 = new CourseBrief();
        brief1.setShortDescription("hey how are you");
        brief1.setTitle("hi");
        briefs.add(brief1);

        CourseBrief brief2 = new CourseBrief();
        brief2.setShortDescription("hey2 how are you");
        brief2.setTitle("hi2");
        briefs.add(brief2);

        CourseBrief brief3 = new CourseBrief();
        brief3.setShortDescription("hey2 how are you");
        brief3.setTitle("brief3");
        briefs.add(brief3);

        CourseBrief brief4 = new CourseBrief();
        brief4.setShortDescription("hey2 how are you");
        brief4.setTitle("brief4");
        briefs.add(brief4);

        CourseBrief brief5 = new CourseBrief();
        brief5.setShortDescription("hey2 how are you");
        brief5.setTitle("brief5");
        briefs.add(brief5);

        mAdapter = new CourseCategoryAdapter(this, briefs);
        gridview.setAdapter(mAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
            }
        });

        setListeners();
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
