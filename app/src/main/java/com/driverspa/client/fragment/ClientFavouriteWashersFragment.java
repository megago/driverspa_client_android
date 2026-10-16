package com.driverspa.client.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
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
import butterknife.ButterKnife;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.adapter.WasherListNearByAdapter;
import com.driverspa.model.SearchFilter;
import com.driverspa.model.WasherPublic;
import com.driverspa.util.Functions;
import com.driverspa.util.GPSTracker;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.ws.AuthClientLogoutRequestEvent;
import com.driverspa.util.otto.ws.UserGetSelfResponseEvent;
import com.driverspa.util.otto.ws.WasherFavouriteRequestEvent;
import com.driverspa.util.otto.ws.WasherFavouriteResponseEvent;
import com.squareup.otto.Subscribe;
import butterknife.BindView;
import butterknife.OnClick;


public class ClientFavouriteWashersFragment extends ClientBaseHomeFragment {

		public interface ActivityActions {
			public void openProfileWasher(String washerId);
			public void hideShowFilterButton(int tab);
			public void login();
		}

	    private ActivityActions activityActions; // activity methods this fragment can run
	    private int numberPage = 0;
	    private WasherListNearByAdapter listAdapter;
	    @BindView(R.id.pull_to_refresh_listview)
		PullToRefreshListView pullToRefreshView;
	    @BindView(R.id.noInternetLayout)
		LinearLayout noInternetLayout;	    
	    @BindView(R.id.noLoggedInLayout)
		View noLoggedInLayout;
	    @BindView(R.id.progressBar)
		ProgressBar progressBar;
	    @BindView(R.id.repeatConnect)
		Button repeatConnect;
	    @BindView(R.id.noInternetMessage)
	    TextView noIternetMsg;
	    @BindView(R.id.login)
		TextView loginText;

	    Location currentLoc;
	    boolean isLoading = false;
	    protected SearchFilter filter = new SearchFilter(true);
	    boolean fragmentVisible;
		Toolbar toolbar;
		TextView titleView;
        boolean loggedIn = UserPreferences.isUserLoggedIn(BA.getContext());
		GPSTracker gps;

	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	        return inflater.inflate(R.layout.fragment_client_favourite_washers, container, false);
	    }
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setHasOptionsMenu(true);					
		}
		
	    @Override
	    public void onViewCreated(View view, Bundle savedInstanceState) {	       
	        ButterKnife.bind(this, view);
			loginText.setPaintFlags(loginText.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
			gps = new GPSTracker(getActivity());
			toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_actionbar);
			titleView = (TextView) toolbar.findViewById(R.id.action_bar_title);
			currentLoc = gps.getLocation();
			listAdapter = new WasherListNearByAdapter(getActivity(),currentLoc, true);
	        ListView listView = pullToRefreshView.getRefreshableView();	       	        
	        listView.setHeaderDividersEnabled(false);
	        listView.setFooterDividersEnabled(false);	        	        
	        TextView emptyView = new TextView(this.getActivity());
	        emptyView.setText(BA.str(R.string.no_favorite_washes));
	        emptyView.setGravity(Gravity.CENTER);
			emptyView.setTextColor(Color.BLACK);
	        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
	        listView.setEmptyView(emptyView);
	        pullToRefreshView.setVisibility(View.GONE);
			pullToRefreshView.getLoadingLayoutProxy().setPullLabel(BA.str(R.string.pull_more));
			pullToRefreshView.getLoadingLayoutProxy().setRefreshingLabel(BA.str(R.string.refreshing));
			pullToRefreshView.getLoadingLayoutProxy().setReleaseLabel(BA.str(R.string.release_now));

	        noInternetLayout.setVisibility(View.GONE);
	        pullToRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
	            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
	            	if(Functions.checkOnline(getActivity())){
	            	   numberPage = 0;	            		
	          		   loadData(numberPage, false);
	            	}
	          		else{
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
					WasherPublic washer = (WasherPublic)((pullToRefreshView.getRefreshableView().getAdapter()).getItem(arg2));
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
									.getCount()-2) % 20 == 0) {
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
	    }
						 
       @OnClick(R.id.repeatConnect)
	   public void onRepeatConnect(){
				if (Functions.checkOnline(getActivity())) {
					loadData(numberPage, false);
				} else {					
					progressBar.setVisibility(View.GONE);
					noInternetLayout.setVisibility(View.VISIBLE);
					pullToRefreshView.setVisibility(View.GONE);
			}
       }
      
	   private void loadData(int numberPage, boolean isGrazyLoad) {
		   	   currentLoc = gps.getLocation();
			   this.numberPage = numberPage;
			   if (!isGrazyLoad) {
				   noInternetLayout.setVisibility(View.GONE);
				   if (pullToRefreshView != null && pullToRefreshView.isRefreshing()) {
					   progressBar.setVisibility(View.GONE);
				   } else {
					   pullToRefreshView.setVisibility(View.GONE);
					   progressBar.setVisibility(View.VISIBLE);
				   }
			   }

			   filter.setOffset(Integer.toString(numberPage * 20));
			   BA.getEventBus().post(new WasherFavouriteRequestEvent(filter));
			   isLoading = true;
		}

	   private void loadDataWithoutLoader() {
			   this.numberPage = 0;
		       if(pullToRefreshView!=null) {
				  pullToRefreshView.setVisibility(View.VISIBLE);
				  noInternetLayout.setVisibility(View.GONE);
			    }
			   filter.setOffset(Integer.toString(numberPage * 20));
			   BA.getEventBus().post(new WasherFavouriteRequestEvent(filter));
			   isLoading = true;
		}

		/**
		 * Received location and request washer nearby
		 * @param event
		 */
		@Subscribe
		public void onAvailableWashersReceived(WasherFavouriteResponseEvent event) {
			isLoading = false;
			progressBar.setVisibility(View.GONE);
			if(event.getWashers() != null){
				if (pullToRefreshView != null
						&& event.getWashers() != null
						&& (pullToRefreshView.getRefreshableView().getCount() <= 0 // if first loading
								|| ( numberPage == 0) )
								|| numberPage == 0) { // if																																													
					noInternetLayout.setVisibility(View.GONE);
					pullToRefreshView.onRefreshComplete();
					pullToRefreshView.setVisibility(View.VISIBLE);
					listAdapter.set(event.getWashers());
					pullToRefreshView.setAdapter(listAdapter);
				} else {// load next page					
					listAdapter.add(event.getWashers());
					listAdapter.notifyDataSetChanged();
					listAdapter.setPreloadStatus(false);
					listAdapter.notifyDataSetChanged();
				}
			}
			else{
				noInternetLayout.setVisibility(View.GONE);
				pullToRefreshView.onRefreshComplete();
				pullToRefreshView.setVisibility(View.VISIBLE);
				listAdapter.set(null);		
			}
		}

	    private void isUserLoggedIn(boolean withoutLoader){
			loggedIn = UserPreferences.isUserLoggedIn(BA.getContext());
			if(loggedIn){
				if(progressBar != null && pullToRefreshView != null && noInternetLayout != null) {
					pullToRefreshView.setVisibility(View.VISIBLE);
					noInternetLayout.setVisibility(View.GONE);
					noLoggedInLayout.setVisibility(View.GONE);
				}
				if(!withoutLoader)
				  load();
				else
				 loadDataWithoutLoader();

			}else{
			   if(progressBar != null && pullToRefreshView != null && noInternetLayout != null) {
				   progressBar.setVisibility(View.GONE);
				   pullToRefreshView.setVisibility(View.GONE);
				   noInternetLayout.setVisibility(View.GONE);
				   noLoggedInLayout.setVisibility(View.VISIBLE);
			   }
			}
		}


	@Subscribe
	public void onUserReceived(UserGetSelfResponseEvent event) {
		if(!loggedIn) {
			loggedIn = UserPreferences.isUserLoggedIn(BA.getContext());
			if (event != null && loggedIn && event.getUser() != null) {
				isUserLoggedIn(false);
			}
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
				if (listAdapter != null) {
					listAdapter.setPreloadStatus(false);
					listAdapter.notifyDataSetChanged();
				}
			}
		}
		
		@Override
		public void onPause() {
			super.onPause();
			if(gps!=null)
				gps.stopUsingGPS();
			BA.getEventBus().unregister(this);
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);			
			activityActions = (ActivityActions) activity;
			if(fragmentVisible)
				activityActions.hideShowFilterButton(0);
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

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		fragmentVisible = isVisibleToUser;
		if(activityActions != null){
			activityActions.hideShowFilterButton(0);
		}

		if(fragmentVisible){
			isUserLoggedIn(false);
		}
	}

	@OnClick(R.id.login)
	public void onLoginButtonClicked(){
		activityActions.login();
	}

	@Subscribe
	public void onLogoutRequested(AuthClientLogoutRequestEvent event){
		isUserLoggedIn(false);
	}
}



