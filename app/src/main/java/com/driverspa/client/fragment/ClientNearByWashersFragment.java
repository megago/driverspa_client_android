package com.driverspa.client.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import androidx.appcompat.widget.Toolbar;
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
import java.util.Locale;
import butterknife.ButterKnife;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.Reference.City;
import com.driverspa.activity.CityChooseActivity;
import com.driverspa.adapter.WasherListNearByAdapter;
import com.driverspa.assist.BaseAssist;
import com.driverspa.dialog.SingleSelectDialog;
import com.driverspa.model.SearchFilter;
import com.driverspa.model.WasherPublic;
import com.driverspa.util.Functions;
import com.driverspa.util.GPSTracker;
import com.driverspa.util.ToastUtil;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.LocationResponseEvent;
import com.driverspa.util.otto.NearSearchEvent;
import com.driverspa.util.otto.ws.FilterGetResponseEvent;
import com.driverspa.util.otto.ws.WashersNearRequestEvent;
import com.driverspa.util.otto.ws.WashersNearResponseEvent;
import com.squareup.otto.Subscribe;
import butterknife.InjectView;
import butterknife.OnClick;


public class ClientNearByWashersFragment extends ClientBaseHomeFragment {

    public interface ActivityActions {
    	public void openProfileWasher(String washerId);
		public void hideShowFilterButton(int tab);
		public void showChooseCity();
    } 

	private ActivityActions activityActions; // activity methods this fragment can run	
	
    private int numberPage = 0;
    private WasherListNearByAdapter listAdapter;
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
	protected SearchFilter filter;
	boolean fragmentVisible;
	boolean useNoGPSOption = false;
	ListView listView;
	boolean isCityFoundByGPS;
	String localCity;
	Toolbar toolbar;
	TextView titleView;
	GPSTracker gps;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_client_nearby_washers, container,false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);					
	}
	
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {	       
        ButterKnife.bind(this, view);

		if(gps == null){
			gps = new GPSTracker(getActivity());
			currentLoc = gps.getLocation();
		}
		toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_actionbar);
		titleView = (TextView) toolbar.findViewById(R.id.action_bar_title);
//		titleView.setVisibility(View.GONE);
		localCity = UserPreferences.getCity(BA.getContext());
//		titleView.setText(Functions.getCityDescription(localCity));
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
				activityActions.openProfileWasher(washer.getId());
			}
		});
		pullToRefreshView.getLoadingLayoutProxy().setPullLabel("Тяни еще смелее");
		pullToRefreshView.getLoadingLayoutProxy().setRefreshingLabel("Обновление...");
		pullToRefreshView.getLoadingLayoutProxy().setReleaseLabel("Теперь можно отпустить");

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
		listAdapter = new WasherListNearByAdapter(getActivity(), currentLoc, true);
		pullToRefreshView.setAdapter(listAdapter);

		if(TextUtils.isEmpty(UserPreferences.getSearchFilter(BA.getContext()))) filter = new SearchFilter();
		else filter = SearchFilter.deserialize(UserPreferences.getSearchFilter(BA.getContext()));
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
	   	filter.setMobile(null);
	    this.numberPage = numberPage;
 	    isCityFoundByGPS = UserPreferences.isCityFoundByGPS(BA.getContext());
	    localCity = UserPreferences.getCity(BA.getContext());
	    String foundCity = "";
		filter.setOffset(Integer.toString(numberPage * 20));
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

				if (currentLoc != null) {
					foundCity = CityChooseActivity.findNearestCity(currentLoc);
					if (!TextUtils.isEmpty(foundCity) && localCity.equals(foundCity) && filter.isDefaultValues()) {
						List<WasherPublic> tempWashers = null;
						if(listAdapter != null) tempWashers = listAdapter.getList();
						listAdapter = new WasherListNearByAdapter(getActivity(), currentLoc, false);
						if(tempWashers != null && tempWashers.size() > 0) listAdapter.set(tempWashers);
						String location = String.format(Locale.US, "%f,%f", currentLoc.getLongitude(), currentLoc.getLatitude());
						filter.setLonLat(location);
						filter.setCity(null);
//						filter.setOrderBy(null);
//						if(titleView != null && fragmentVisible)
//							titleView.setText(Functions.getCityDescription(localCity));

					} else {
						filter.setLonLat(null);
//						filter.setOrderBy("name");
						filter.setCity(localCity);
					}
				} else {
					List<WasherPublic> tempWashers = null;
					if(listAdapter != null) tempWashers = listAdapter.getList();
					listAdapter = new WasherListNearByAdapter(getActivity(), currentLoc, true);
					if(tempWashers != null && tempWashers.size() > 0)
						listAdapter.set(tempWashers);
					pullToRefreshView.setAdapter(listAdapter);
					isLoading = true;
					filter.setLonLat(null);
//					filter.setOrderBy("name");
					filter.setCity(localCity);
				}
			}

			BA.getEventBus().post(new WashersNearRequestEvent(filter));
		 }
		else{
		  pullToRefreshView.onRefreshComplete();
		  progressBar.setVisibility(View.GONE);
		  pullToRefreshView.setVisibility(View.VISIBLE);
		  ToastUtil.display(getActivity(), "Выберите город");
		}
	}

	@Subscribe
	public void onFilterReceived(FilterGetResponseEvent event){
        filter = event.getSearchFilter().clone();
		localCity = UserPreferences.getCity(BA.getContext());
		loadData(0,false);
	}

	/**
	 * Received location and request people nearby
	 * @param event
	 */
	@Subscribe
	public void onNearWashersReceived(WashersNearResponseEvent event) {
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
				listAdapter = new WasherListNearByAdapter(getActivity(),currentLoc,useNoGPSOption);
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
			listAdapter = new WasherListNearByAdapter(getActivity(),currentLoc,useNoGPSOption);
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

		if(!(listAdapter != null && listAdapter.getList() != null && listAdapter.getList().size() > 0) && currentLoc != null){
			load();
		}
		if(TextUtils.isEmpty(localCity)){
			localCity = UserPreferences.getCity(BA.getContext());
			load();
		}

	}

	@Subscribe
	public void onLocationResponseEvent(LocationResponseEvent event) {
		if(currentLoc == null && event.getLocation() != null) {
			currentLoc = event.getLocation();
			listAdapter = new WasherListNearByAdapter(getActivity(), currentLoc, true);
//			listView.setAdapter(listAdapter);
			load();
		}
		currentLoc = event.getLocation();
	}

	@Override
	public void onPause() {
		super.onPause();
	    if(gps!=null)
	    	gps.stopUsingGPS();
	    if(isLoading){
		   pullToRefreshView.onRefreshComplete();
		   progressBar.setVisibility(View.GONE);
		   pullToRefreshView.setVisibility(View.VISIBLE);
		   isLoading = false;
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

	@Subscribe
	public void onFilterSubmitted(NearSearchEvent event){
	  this.filter.setSearchText(event.getQuery());
	  loadData(0, false);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		localCity = UserPreferences.getCity(BA.getContext());
		fragmentVisible = isVisibleToUser;
		if(activityActions != null){
			activityActions.hideShowFilterButton(2);
		}
//		if(fragmentVisible && !TextUtils.isEmpty(localCity)){
//			if(titleView != null)
//				titleView.setText(Functions.getCityDescription(localCity));
//		}
	}

	public void showSettingsAlert(){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT);
		// Setting Dialog Title
		alertDialog.setTitle("Настройки GPS");
		// Setting Dialog Message
		alertDialog.setMessage("GPS отключен. Хотите включить?");
		// On pressing the Settings button.
		alertDialog.setPositiveButton("Настройки", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog,int which) {
//            	mContext.startActivity(new Intent(mContext, ClientSettingsActivity.class).putExtra(ClientBaseActivity.OPENING_ANIMATION, false));
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				getActivity().startActivity(intent);
				gpsSettingsShown = true;
				useNoGPSOption = false;
			}
		});

		// On pressing the cancel button
		alertDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				useNoGPSOption = true;
				gpsSettingsShown = false;
				loadData(0,false);
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}

	private void setEmptyView(){
		localCity = UserPreferences.getCity(BA.getContext());
		if(filter.isDefaultValues()){
			noRecords.setText("Нет зарегестрированных моек в '"+Functions.getCityDescription(localCity)+"'");
			noRecords.setVisibility(View.VISIBLE);
		}
		else{
			noRecords.setText("Не найдено не одной мойки,\nсоответсвующего параметрам поиска\nВведите другие параметры поиска");
			noRecords.setVisibility(View.VISIBLE);
		}
		noRecords.setVisibility(View.VISIBLE);
	}

}

