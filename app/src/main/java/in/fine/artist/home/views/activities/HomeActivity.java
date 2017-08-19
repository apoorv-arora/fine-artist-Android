package in.fine.artist.home.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.ref.SoftReference;

import in.fine.artist.home.R;
import in.fine.artist.home.ZApplication;
import in.fine.artist.home.utils.CommonLib;
import in.fine.artist.home.utils.VPrefsReader;
import in.fine.artist.home.utils.views.TextViewMedium;
import in.fine.artist.home.views.fragments.MyCoursesFragment;
import in.fine.artist.home.views.fragments.RecommendedFragment;
import in.fine.artist.home.views.fragments.UpdatesFragment;

/**
 * Created by apoorvarora on 21/04/17.
 */
public class HomeActivity extends BaseActivity {

    // layout elements
    private boolean destroyed;
    private VPrefsReader prefs;
    private LayoutInflater inflater;

    // tablayout and viewpager
    private TabLayout tabLayout;
    private ViewPager viewPager;

    // fragment indices
    public static final int FRAGMENT_INDEX_FIND_LOAD = 0;
    public static final int FRAGMENT_INDEX_RECOMMEDED = 1;
    public static final int FRAGMENT_INDEX_ACTIVE = 2;
    private int initialFragmentIndex = FRAGMENT_INDEX_RECOMMEDED;
    private SparseArray<SoftReference<Fragment>> fragments = new SparseArray<SoftReference<Fragment>>();
    private HomePagerAdapter mPagerAdapter;
    private ZApplication vapp;
    private int width,height;
    private TextView title1, title2, title3;
    private RelativeLayout recommendedTab, activeLoadTab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        inflater = LayoutInflater.from(this);
        destroyed = false;
        vapp = (ZApplication) getApplication();
        prefs = VPrefsReader.getInstance();
        width = getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();

        // init viewpager and adapter
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        mPagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setOffscreenPageLimit(2);

        tabLayout.setupWithViewPager(viewPager);
        setTabLayout();
        viewPager.setCurrentItem(initialFragmentIndex);
        changeSelectedTabcolor(initialFragmentIndex);
        setListeners();
    }

    private void setTabLayout() {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setCustomView(getTabView(i));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // logging out user
        if (prefs.getPref(CommonLib.PROPERTY_USER_ID, 0) == 0) {
            Intent intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public View getTabView(int position) {
        // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
        View v = inflater.inflate(R.layout.layout_tab, null);
        switch (position) {
            case 0:
                title1 = (TextViewMedium) v.findViewById(R.id.title);
                title1.setText(getString(R.string.find_load_tab_title));
                break;
            case 1:
                recommendedTab = (RelativeLayout) v.findViewById(R.id.tabView);
                title2 = (TextViewMedium) v.findViewById(R.id.title);
                title2.setText(getString(R.string.recommended_load_tab_title));

                break;
            case 2:
                activeLoadTab = (RelativeLayout) v.findViewById(R.id.tabView);
                title3 = (TextViewMedium) v.findViewById(R.id.title);
                title3.setText(getString(R.string.active_load_tab_title));
                break;
        }
        return v;
    }

    private void changeSelectedTabcolor(int selectedTabPosition) {
        switch (selectedTabPosition) {
            case 0:
                title1.setTextColor(ContextCompat.getColor(this, R.color.white));
                title2.setTextColor(ContextCompat.getColor(this, R.color.locality_text));
                title3.setTextColor(ContextCompat.getColor(this, R.color.locality_text));

                break;
            case 1:
                title1.setTextColor(ContextCompat.getColor(this, R.color.locality_text));
                title2.setTextColor(ContextCompat.getColor(this, R.color.white));
                title3.setTextColor(ContextCompat.getColor(this, R.color.locality_text));

                break;
            case 2:
                title1.setTextColor(ContextCompat.getColor(this, R.color.locality_text));
                title2.setTextColor(ContextCompat.getColor(this, R.color.locality_text));
                title3.setTextColor(ContextCompat.getColor(this, R.color.white));

                break;
        }
    }

    private void setListeners() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeSelectedTabcolor(tabLayout.getSelectedTabPosition());
                tabLayout.getTabAt(position).select();

//                Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_home);
                AppBarLayout mAppBarLayout = (AppBarLayout) findViewById(R.id.app_barlayout);
                // Show toolbar when layout  in scroll mode
//                AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();
                CoordinatorLayout.LayoutParams appBarLayoutParams = (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
                if (position == 0) {
//                    params.setScrollFlags(0);
                    appBarLayoutParams.setBehavior(null);
                    mAppBarLayout.setLayoutParams(appBarLayoutParams);
                } else {
//                    params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                    appBarLayoutParams.setBehavior(new AppBarLayout.Behavior());
                    mAppBarLayout.setLayoutParams(appBarLayoutParams);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        activeLoadTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdatesFragment activeLoadsFragment = null;

                if (fragments.get(FRAGMENT_INDEX_ACTIVE) != null
                        && fragments.get(FRAGMENT_INDEX_ACTIVE).get() != null) {
                    activeLoadsFragment = (UpdatesFragment) fragments.get(FRAGMENT_INDEX_ACTIVE).get();
                } else {
                    HomePagerAdapter hAdapter = (HomePagerAdapter) viewPager.getAdapter();
                    if (hAdapter != null) {
                        try {
                            activeLoadsFragment = (UpdatesFragment) hAdapter.instantiateItem(viewPager,
                                    FRAGMENT_INDEX_ACTIVE);
                        } catch (Exception e) {
                        }
                    }
                }

                if (activeLoadsFragment != null) {
                    viewPager.setCurrentItem(2);
                    changeSelectedTabcolor(2);
//                    activeLoadsFragment.scrollToTop();
                }
            }
        });

        recommendedTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecommendedFragment recommendedLoadsFragment = null;
                if (fragments.get(FRAGMENT_INDEX_RECOMMEDED) != null
                        && fragments.get(FRAGMENT_INDEX_RECOMMEDED).get() != null) {
                    recommendedLoadsFragment = (RecommendedFragment) fragments.get(FRAGMENT_INDEX_RECOMMEDED).get();
                } else {
                    HomePagerAdapter hAdapter = (HomePagerAdapter) viewPager.getAdapter();
                    if (hAdapter != null) {
                        try {
                            recommendedLoadsFragment = (RecommendedFragment) hAdapter.instantiateItem(viewPager,
                                    FRAGMENT_INDEX_RECOMMEDED);
                        } catch (Exception e) {
                        }
                    }
                }

                if (recommendedLoadsFragment != null) {
                    viewPager.setCurrentItem(1);
                    changeSelectedTabcolor(1);
//                    recommendedLoadsFragment.scrollToTop();
                }
            }
        });

        findViewById(R.id.search_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.settings_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, UserProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    private class HomePagerAdapter extends FragmentStatePagerAdapter {

        public HomePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case FRAGMENT_INDEX_FIND_LOAD: {
                    MyCoursesFragment findLoadFragment = new MyCoursesFragment();
                    fragments.put(FRAGMENT_INDEX_FIND_LOAD, new SoftReference<Fragment>(findLoadFragment));
                    return findLoadFragment;
                }
                case FRAGMENT_INDEX_RECOMMEDED: {
                    RecommendedFragment recommendedLoadsFragment = new RecommendedFragment();
                    fragments.put(FRAGMENT_INDEX_RECOMMEDED, new SoftReference<Fragment>(recommendedLoadsFragment));
                    return recommendedLoadsFragment;
                }
                case FRAGMENT_INDEX_ACTIVE: {
                    UpdatesFragment activeLoadsFragment = new UpdatesFragment();
                    fragments.put(FRAGMENT_INDEX_ACTIVE, new SoftReference<Fragment>(activeLoadsFragment));
                    return activeLoadsFragment;
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
}
