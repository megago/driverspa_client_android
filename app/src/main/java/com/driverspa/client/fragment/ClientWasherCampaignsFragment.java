package com.driverspa.client.fragment;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toolbar;
import android.text.TextUtils;
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
import java.util.ArrayList;
import java.util.List;
import butterknife.ButterKnife;
import com.squareup.otto.Subscribe;
import butterknife.InjectView;
import butterknife.OnClick;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.Reference.City;
import com.driverspa.adapter.WasherCampaignsListAdapter;
import com.driverspa.assist.BaseAssist;
import com.driverspa.dialog.SingleSelectDialog;
import com.driverspa.model.SearchFilter;
import com.driverspa.model.WasherPublic;
import com.driverspa.util.Functions;
import com.driverspa.util.ToastUtil;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.ws.FilterGetResponseEvent;
import com.driverspa.util.otto.ws.WasherCampaignsRequestEvent;
import com.driverspa.util.otto.ws.WasherCampaignsResponseEvent;


public class ClientWasherCampaignsFragment extends ClientBaseHomeFragment {

    public interface ActivityActions {
    	public void openProfileWasher(String washerId);
    	public void openCampaignInfo(String washerId, String washer);
		public void hideShowFilterButton(int tab);
		public void showChooseCity();
    } 

	private ActivityActions activityActions; // activity methods this fragment can run	
	
    private int numberPage = 0;
    private WasherCampaignsListAdapter listAdapter;
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
    @InjectView(R.id.noRecords)
    TextView noRecords;

    Location currentLoc;
    Location selectedCityLoc;
    SingleSelectDialog cityDialog;
    String selectedCity;
    ArrayList<City> allCities = BA.getReference().getCities();
    boolean cityDialogShown = false;
    boolean isLoading = false;
	protected SearchFilter filter = new SearchFilter();
	boolean fragmentVisible;
	boolean useNoGPSOption = false;
	ListView listView;
	boolean isCityFoundByGPS;
	String localCity;
	Toolbar toolbar;
	TextView titleView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_client_washer_campaigns, container,false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);					
	}
	
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {	       
        ButterKnife.inject(this, view);
		toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_actionbar);
		titleView = (TextView) toolbar.findViewById(R.id.action_bar_title);

		localCity = UserPreferences.getCity(BA.getContext());
		titleView.setText("Акции "+(localCity!=null?"("+Functions.getCityDescription(localCity)+")":""));
//		filter.setMobile(UserPreferences.getUserPhone(BA.getContext()));

		listView = pullToRefreshView.getRefreshableView();
        listView.setHeaderDividersEnabled(false);

		listView.setEmptyView(null);
        listView.setFooterDividersEnabled(false);	        	        
//        pullToRefreshView.setVisibility(View.GONE);
        pullToRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				loadData(0,false);
            }
        });

		pullToRefreshView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				WasherPublic washer = (WasherPublic)((pullToRefreshView.getRefreshableView().getAdapter()).getItem(arg2));
				activityActions.openCampaignInfo(washer.getId(),washer.serialize());
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
					if (pullToRefreshView.getRefreshableView().getLastVisiblePosition() == pullToRefreshView.getRefreshableView().getAdapter().getCount() - 1
						&& pullToRefreshView.getChildAt(pullToRefreshView.getChildCount() - 1).getBottom() <= pullToRefreshView.getHeight()) {
						if ((pullToRefreshView.getRefreshableView().getCount()-2) % 20 == 0 && pullToRefreshView.getRefreshableView().getCount() > 2) {
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
		listAdapter = new WasherCampaignsListAdapter(getActivity(), currentLoc, true);
		pullToRefreshView.setAdapter(listAdapter);
		load();
    }
					 
   @OnClick(R.id.repeatConnect)
   public void onRepeatConnect(){
			if (Functions.checkOnline(getActivity())) {
				loadData(numberPage, false);
			} else {					
				pullToRefreshView.setVisibility(View.GONE);
				progressBar.setVisibility(View.GONE);
				noInternetLayout.setVisibility(View.VISIBLE);
		}
   } 
   
   private void loadData(int numberPage, boolean isGrazyLoad) {
	    this.numberPage = numberPage;
 	    isCityFoundByGPS = UserPreferences.isCityFoundByGPS(BA.getContext());
	    localCity = UserPreferences.getCity(BA.getContext());
	    String foundCity = "";
		filter.setOffset(Integer.toString(numberPage * 20));
	    filter.setHasCampaign(true);
	    if(!TextUtils.isEmpty(localCity)) {
			isLoading = true;
			if (!isGrazyLoad) {
				noRecords.setVisibility(View.GONE);
				noInternetLayout.setVisibility(View.GONE);
				if (pullToRefreshView != null && pullToRefreshView.isRefreshing()) {
					pullToRefreshView.setVisibility(View.VISIBLE);
					progressBar.setVisibility(View.GONE);
				} else {
//					pullToRefreshView.setVisibility(View.GONE);
					progressBar.setVisibility(View.VISIBLE);
				}
					List<WasherPublic> tempWashers = null;
					if(listAdapter != null) tempWashers = listAdapter.getList();
					listAdapter = new WasherCampaignsListAdapter(getActivity(), currentLoc, true);
					if(tempWashers != null && tempWashers.size() > 0)
						listAdapter.set(tempWashers);
					pullToRefreshView.setAdapter(listAdapter);
					isLoading = true;
					filter.setLonLat(null);
					filter.setCity(localCity);
			}

			BA.getEventBus().post(new WasherCampaignsRequestEvent(filter));
		 }
		else{
		  pullToRefreshView.onRefreshComplete();
		  progressBar.setVisibility(View.GONE);
		  pullToRefreshView.setVisibility(View.VISIBLE);
//		  ToastUtil.display(getActivity(), "Выберите город");
		}
	}

	@Subscribe
	public void onFilterReceived(FilterGetResponseEvent event){
//        filter = event.getSearchFilter().clone();
		localCity = UserPreferences.getCity(BA.getContext());
		loadData(0,false);
	}

	/**
	 * Received location and request
	 * @param event
	 */
	@Subscribe
	public void onWashersCampaignsReceived(WasherCampaignsResponseEvent event) {
		isLoading = false;
		pullToRefreshView.onRefreshComplete();
		progressBar.setVisibility(View.GONE);
		if(event.getData() != null && event.getData().getResponse() != null && event.getData().getResponse().getResult() != null) {
			if(BaseAssist.isSuccess(event.getData())) {
				if (pullToRefreshView != null
						&& event.getData().getResponse().getResult() != null
						&& (pullToRefreshView.getRefreshableView().getCount() <= 0 // if first loading
						|| (numberPage == 0))
						|| numberPage == 0) { // if
					noInternetLayout.setVisibility(View.GONE);
					progressBar.setVisibility(View.GONE);
					pullToRefreshView.setVisibility(View.VISIBLE);
					listAdapter.set(event.getData().getResponse().getResult());
					pullToRefreshView.setAdapter(listAdapter);
					if(event.getData().getResponse().getResult().size() == 0){
						setEmptyView();
					}
				} else {// load next page
					listAdapter.add(event.getData().getResponse().getResult());
					listAdapter.notifyDataSetChanged();
					listAdapter.setPreloadStatus(false);
					listAdapter.notifyDataSetChanged();
				}
			}
			else{
				listAdapter = new WasherCampaignsListAdapter(getActivity(),currentLoc,useNoGPSOption);
				if(numberPage == 0){
					if(event.getData().getResponse().getResult().size() == 0){
						setEmptyView();
					}
				}
				listAdapter.set(new ArrayList<WasherPublic>());
				pullToRefreshView.setAdapter(listAdapter);
				pullToRefreshView.setVisibility(View.VISIBLE);
				noInternetLayout.setVisibility(View.GONE);
				progressBar.setVisibility(View.GONE);
				ToastUtil.display(getActivity(),event.getData().getMessage());
			}
		}
		else{
			listAdapter = new WasherCampaignsListAdapter(getActivity(),currentLoc,useNoGPSOption);
			if(numberPage == 0){
				if(event.getData() != null && event.getData().getResponse() != null && event.getData().getResponse().getResult() != null &&
						event.getData().getResponse().getResult().size() == 0){
					setEmptyView();
				}
			}
			listAdapter.set(new ArrayList<WasherPublic>());
			pullToRefreshView.setAdapter(listAdapter);
			pullToRefreshView.setVisibility(View.VISIBLE);
			noInternetLayout.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			ToastUtil.display(getActivity(),"Ошибка при получении данных с сервера");
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onResume() {
		super.onResume();	
		BA.getEventBus().register(this);
		if(TextUtils.isEmpty(localCity)){
			localCity = UserPreferences.getCity(BA.getContext());
			load();
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
	    if(isLoading){
		   pullToRefreshView.onRefreshComplete();
		   progressBar.setVisibility(View.GONE);
		   pullToRefreshView.setVisibility(View.VISIBLE);
	    }
		BA.getEventBus().unregister(this);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);			
		activityActions = (ActivityActions) activity;
		if(fragmentVisible)
		   activityActions.hideShowFilterButton(2);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		activityActions = null;
	}

	@Override
	public int getTitleResourceId() {		
		return 0;
	}

	@Override
	public void load() {
		loadData(0,false);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		localCity = UserPreferences.getCity(BA.getContext());
		fragmentVisible = isVisibleToUser;
		if(activityActions != null){
			activityActions.hideShowFilterButton(4);
		}
		if(titleView != null)
			titleView.setText("Акции "+(localCity!=null?"("+Functions.getCityDescription(localCity)+")":""));
	}

	private void setEmptyView(){
		localCity = UserPreferences.getCity(BA.getContext());
		noRecords.setText("Акцию еще не объявили в городе '"+Functions.getCityDescription(localCity)+"'");
		noRecords.setVisibility(View.VISIBLE);
	}

}

