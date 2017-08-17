package in.fine.artist.home.views.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.fine.artist.home.R;
import in.fine.artist.home.adapters.MyCourseAdapter;
import in.fine.artist.home.adapters.RecommendedAdapter;
import in.fine.artist.home.data.course.CourseBrief;
import in.fine.artist.home.utils.RandomCallback;
import in.fine.artist.home.utils.networking.UploadManager;
import in.fine.artist.home.utils.networking.UploadManagerCallback;

/**
 * Created by apoorvarora on 07/05/17.
 */
public class MyCoursesFragment extends BaseFragment implements UploadManagerCallback, RandomCallback {
    private LayoutInflater inflater;
    private Activity mActivity;
    private View getView;
    private boolean destroyed;

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private MyCourseAdapter mAdapter;
    private ArrayList<CourseBrief> loadList;

    public MyCoursesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_courses, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        destroyed = false;
        getView = getView();
        mActivity = getActivity();
        inflater = LayoutInflater.from(mActivity);
        UploadManager.getInstance().addCallback(this
                , UploadManager.COURSES_USER);

        swipeRefresh = (SwipeRefreshLayout) getView.findViewById(R.id.swipeRefresh);
        recyclerView = (RecyclerView) getView.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        recyclerView.setLayoutManager(linearLayoutManager);

        loadList = new ArrayList<>();
        mAdapter = new MyCourseAdapter(mActivity, loadList, this);
        recyclerView.setAdapter(mAdapter);

        setListeners();

        getView.findViewById(R.id.progress_view).setVisibility(View.VISIBLE);
        getView.findViewById(R.id.empty_view).setVisibility(View.GONE);
        getView.findViewById(R.id.recyclerView).setVisibility(View.GONE);
        refreshView();
    }

    private void setListeners() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshView();
            }
        });
    }

    @Override
    public void onDestroyView() {
        destroyed = true;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        destroyed = true;
        UploadManager.getInstance().removeCallback(this
                , UploadManager.COURSES_USER);
        super.onDestroy();
    }

    private void refreshView() {
        UploadManager.getInstance().apiCall(new HashMap<String, String>(), UploadManager.COURSES_USER, null, null);
    }

    @Override
    public void randomCallback(Object object) {
        if (object != null) {
            if (object instanceof CourseBrief) {
            }
        }
    }

    @Override
    public void uploadStarted(int requestType, Object data, Object requestData) {

    }

    @Override
    public void uploadFinished(int requestType, Object data, boolean status, String errorMessage, Object requestData) {
        if (requestType == UploadManager.COURSES_USER) {
            if (!destroyed) {
                getView.findViewById(R.id.progress_view).setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                if (data != null && data instanceof List<?> && ((List<?>)data).size() > 0) {
                    loadList = (ArrayList<CourseBrief>) data;
                    mAdapter = new MyCourseAdapter(mActivity, loadList, this);
                    recyclerView.setAdapter(mAdapter);

                    getView.findViewById(R.id.empty_view).setVisibility(View.GONE);
                    getView.findViewById(R.id.recyclerView).setVisibility(View.VISIBLE);
                } else {
                    getView.findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                    getView.findViewById(R.id.recyclerView).setVisibility(View.GONE);
                }
            }
        }
    }
}