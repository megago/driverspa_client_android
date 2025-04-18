package com.driverspa.client.fragment;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.squareup.otto.Subscribe;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.adapter.WasherListAdapter;
import com.driverspa.model.SearchFilter;
import com.driverspa.model.WasherPublic;
import com.driverspa.util.Functions;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.AvailableSearchEvent;
import com.driverspa.util.otto.ws.FilterGetResponseEvent;
import com.driverspa.util.otto.ws.WasherAvailableRequestEvent;
import com.driverspa.util.otto.ws.WasherAvailableResponseEvent;


public class ClientAvailableWashersFragment extends ClientBaseHomeFragment {

    public interface ActivityActions {
        public void openProfileWasher(String washerId);
    }

    private ActivityActions activityActions; // activity methods this fragment can run
    private int numberPage = 0;
    private WasherListAdapter listAdapter;
    @InjectView(R.id.pull_to_refresh_listview)
    PullToRefreshListView pullToRefreshView;
    @InjectView(R.id.noInternetLayout)
    LinearLayout noInternetLayout;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;
    @InjectView(R.id.repeatConnect)
    Button repeatConnect;
    @InjectView(R.id.noInternetMessage)
    TextView noIternetMsg;
    Location currentLoc;
    protected SearchFilter filter = new SearchFilter();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_client_available_washers, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        listAdapter = new WasherListAdapter(getActivity());
        ListView listView = pullToRefreshView.getRefreshableView();
        listView.setHeaderDividersEnabled(false);
        listView.setFooterDividersEnabled(false);
        TextView emptyView = new TextView(this.getActivity());
        emptyView.setText(R.string.list_empty);
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        listView.setEmptyView(emptyView);
        pullToRefreshView.setVisibility(View.GONE);
        noInternetLayout.setVisibility(View.GONE);
        pullToRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (Functions.checkOnline(getActivity())) {
                    numberPage = 0;
                    loadData(numberPage, false);
                } else {
                    progressBar.setVisibility(View.GONE);
                    noInternetLayout.setVisibility(View.VISIBLE);
                    pullToRefreshView.setVisibility(View.GONE);
                }
            }
        });

        pullToRefreshView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                WasherPublic washer = (WasherPublic) ((pullToRefreshView.getRefreshableView().getAdapter()).getItem(arg2));
                activityActions.openProfileWasher(washer.getId());
            }
        });

        pullToRefreshView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {
            }

            @Override
            public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
                if (!pullToRefreshView.isRefreshing() && pullToRefreshView.getRefreshableView().getAdapter() != null &&
                        pullToRefreshView.getRefreshableView().getAdapter().getCount() > 0)
                    if (pullToRefreshView.getRefreshableView().getLastVisiblePosition() == pullToRefreshView.getRefreshableView()
                            .getAdapter().getCount() - 1
                            && pullToRefreshView
                            .getChildAt(pullToRefreshView.getChildCount() - 1)
                            .getBottom() <= pullToRefreshView.getHeight()) {
                        if ((pullToRefreshView.getRefreshableView()
                                .getCount() - 2) % 20 == 0) {
                            if (listAdapter != null && !listAdapter.getPreloadStatus()) {
                                listAdapter.setPreloadStatus(true);
                                listAdapter.notifyDataSetChanged();
                                numberPage++;
                                loadData(numberPage, true);
                            }
                        }
                    }
               }
        });

        load();
    }

    @Subscribe
    public void onFilterReceived(FilterGetResponseEvent event){
        numberPage = 0;
        filter = event.getSearchFilter().clone();
        loadData(0, false);
    }

    @OnClick(R.id.repeatConnect)
    public void onRepeatConnect() {
        if (Functions.checkOnline(getActivity())) {
            loadData(numberPage, false);
        } else {
            progressBar.setVisibility(View.GONE);
            noInternetLayout.setVisibility(View.VISIBLE);
            pullToRefreshView.setVisibility(View.GONE);
        }
    }

    private void loadData(int numberPage, boolean isGrazyLoad) {
        this.numberPage = numberPage;
        if (!isGrazyLoad) {
            noInternetLayout.setVisibility(View.GONE);
            if (pullToRefreshView != null && pullToRefreshView.isRefreshing()) {
                pullToRefreshView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            } else {
                pullToRefreshView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }
        }

        if(!TextUtils.isEmpty(UserPreferences.getCity(getActivity())))
            this.filter.setCity(UserPreferences.getCity(getActivity()));
        filter.setFree("true");
        filter.setOffset(Integer.toString(numberPage * 20));
        BA.getEventBus().post(new WasherAvailableRequestEvent(filter));
    }

    /**
     * Received location and request washer nearby
     *
     * @param event
     */
    @Subscribe
    public void onAvailableWashersReceived(WasherAvailableResponseEvent event) {
        if (event.getWashers() != null) {
            if (pullToRefreshView != null
                    && event.getWashers() != null
                    && (pullToRefreshView.getRefreshableView().getCount() <= 0 // if first loading
                    || (numberPage == 0))
                    || numberPage == 0) { // if
                noInternetLayout.setVisibility(View.GONE);
                pullToRefreshView.onRefreshComplete();
                progressBar.setVisibility(View.GONE);
                pullToRefreshView.setVisibility(View.VISIBLE);
                listAdapter.set(event.getWashers());
                pullToRefreshView.setAdapter(listAdapter);
            } else {// load next page
                listAdapter.add(event.getWashers());
                listAdapter.notifyDataSetChanged();
                listAdapter.setPreloadStatus(false);
                listAdapter.notifyDataSetChanged();
            }
        } else {
            noInternetLayout.setVisibility(View.GONE);
            pullToRefreshView.onRefreshComplete();
            progressBar.setVisibility(View.GONE);
            pullToRefreshView.setVisibility(View.VISIBLE);
            listAdapter.set(null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        BA.getEventBus().register(this);
        if (!Functions.checkOnline(getActivity())) {
            progressBar.setVisibility(View.GONE);
            noInternetLayout.setVisibility(View.VISIBLE);
            pullToRefreshView.setVisibility(View.GONE);
            if (listAdapter != null) {
                listAdapter.setPreloadStatus(false);
                listAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        BA.getEventBus().unregister(this);
        if(progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
            pullToRefreshView.onRefreshComplete();
            pullToRefreshView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activityActions = (ActivityActions) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activityActions = null;
    }

    @Override
    public int getTitleResourceId() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void load() {
        loadData(0, false);
    }

    @Subscribe
    public void onFilterSubmitted(AvailableSearchEvent event) {
        this.filter.setSearchText(event.getQuery());
        loadData(0, false);
    }
}

