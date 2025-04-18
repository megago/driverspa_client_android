package com.driverspa.client.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
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
import java.util.HashMap;
import butterknife.ButterKnife;
import com.squareup.otto.Subscribe;
import butterknife.InjectView;
import butterknife.OnClick;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.adapter.BooksAdapter;
import com.driverspa.assist.BaseAssist;
import com.driverspa.model.PushData;
import com.driverspa.model.SearchFilter;
import com.driverspa.model.BookInfo;
import com.driverspa.util.Functions;
import com.driverspa.util.ToastUtil;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.NewBookingPushRequestEvent;
import com.driverspa.util.otto.NewBookingPushResponseEvent;
import com.driverspa.util.otto.RemoveBulkBookingPushRequestEvent;
import com.driverspa.util.otto.ws.AuthClientLogoutRequestEvent;
import com.driverspa.util.otto.ws.BooksRequestEvent;
import com.driverspa.util.otto.ws.BooksResponseEvent;
import com.driverspa.util.otto.ws.UserGetSelfResponseEvent;

import static com.driverspa.util.Constants.APPROVED;
import static com.driverspa.util.Constants.REJECTED;

public class ClientBooksFragment extends ClientBaseHomeFragment {
	
        public static final String PENDING = "pending";
        public static final String FINISHED = "finished";
        public static final String STATUS = "STATUS";
	
        public interface ActivityActions {
    	 public void openBookInfo(String bookId);
    	 public void openActiveBookInfo(String bookId);
		 public void hideShowFilterButton(int tab);
		 public void login();
        } 

	    public static ClientBooksFragment newInstance(String status){
			ClientBooksFragment fragment = new ClientBooksFragment();
			Bundle bundle = new Bundle();
			bundle.putString(STATUS, status);
			fragment.setArguments(bundle);
			return fragment;
		}

        private ActivityActions activityActions;
	    private int numberPage = 0;
	    private BooksAdapter listAdapter;
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
	    @InjectView(R.id.title)
	    TextView title;
		@InjectView(R.id.login)
		TextView loginText;

	    boolean fragmentVisible;
	    HashMap<String,PushData> bookingPushData;
	    protected SearchFilter filter = new SearchFilter(false);
		Toolbar toolbar;
		TextView titleView;
	    boolean loggedIn = UserPreferences.isUserLoggedIn(BA.getContext());

		@InjectView(R.id.noLoggedInLayout)
		View noLoggedInLayout;

    	@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	        return inflater.inflate(R.layout.fragment_client_books, container, false);
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
			toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_actionbar);
			titleView = (TextView) toolbar.findViewById(R.id.action_bar_title);

			listAdapter = new BooksAdapter(getActivity(),bookingPushData);
			ListView listView = pullToRefreshView.getRefreshableView();
	        listView.setHeaderDividersEnabled(false);
	        listView.setFooterDividersEnabled(false);	        	        
	        TextView emptyView = new TextView(this.getActivity());
	        emptyView.setText("Нет брони");
			emptyView.setTextColor(Color.BLACK);
	        emptyView.setGravity(Gravity.CENTER);
	        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
	        listView.setEmptyView(emptyView);
	        pullToRefreshView.setVisibility(View.GONE);
	        noInternetLayout.setVisibility(View.GONE);
			pullToRefreshView.getLoadingLayoutProxy().setPullLabel("Тяни еще смелее");
			pullToRefreshView.getLoadingLayoutProxy().setRefreshingLabel("Обновление...");
			pullToRefreshView.getLoadingLayoutProxy().setReleaseLabel("Теперь можно отпустить");

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
					BookInfo book = (BookInfo)((pullToRefreshView.getRefreshableView().getAdapter()).getItem(arg2));
					activityActions.openBookInfo(book.getId());
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

	 private void isUserLoggedIn(boolean withoutLoader){
		loggedIn = UserPreferences.isUserLoggedIn(BA.getContext());
		if(loggedIn){
			if (progressBar != null && pullToRefreshView != null && noInternetLayout != null) {
				noInternetLayout.setVisibility(View.GONE);
				noLoggedInLayout.setVisibility(View.GONE);
			}
			if(!withoutLoader)
		  	 load();
			else
			 loadDataWithoutLoader();
		}else {
			if (progressBar != null && pullToRefreshView != null && noInternetLayout != null){
				progressBar.setVisibility(View.GONE);
				pullToRefreshView.setVisibility(View.GONE);
				noInternetLayout.setVisibility(View.GONE);
				noLoggedInLayout.setVisibility(View.VISIBLE);
		  }
		}
	 }
      
	 private void loadData(int numberPage, boolean isGrazyLoad) {
		    this.numberPage = numberPage;
			if (!isGrazyLoad) {
				noInternetLayout.setVisibility(View.GONE);
				if(pullToRefreshView != null && pullToRefreshView.isRefreshing()){
					 pullToRefreshView.setVisibility(View.VISIBLE);
					 progressBar.setVisibility(View.GONE);				
				 }
				else{
				 progressBar.setVisibility(View.VISIBLE);
				 pullToRefreshView.setVisibility(View.GONE);
			   }
			}
						
			filter.setOffset(Integer.toString(numberPage*20));
			BA.getEventBus().post(new BooksRequestEvent(filter));
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

	private void loadDataWithoutLoader() {
		    this.numberPage = 0;
		     if(progressBar != null)
		        progressBar.setVisibility(View.GONE);
			filter.setOffset(Integer.toString(numberPage*20));
			BA.getEventBus().post(new BooksRequestEvent(filter));
		}
				
		/**
		 * Received location and request people nearby
		 * @param event
		 */
		@Subscribe
		public void onBooksReceived(BooksResponseEvent event) {
		   //First remove all badges then re-insert according to list
          if(bookingPushData != null && bookingPushData.size() > 0)
			  BA.getEventBus().post(new RemoveBulkBookingPushRequestEvent());

		  if(event.getData() != null){
				if(BaseAssist.isSuccess(event.getData())) {
					if (pullToRefreshView != null
							&& event.getData().getResponse().getBookInfoList() != null
							&& (pullToRefreshView.getRefreshableView().getCount() <= 0 // if first loading
							|| ( numberPage == 0) )
							|| numberPage == 0) { // if
						noInternetLayout.setVisibility(View.GONE);
						pullToRefreshView.onRefreshComplete();
						progressBar.setVisibility(View.GONE);
						pullToRefreshView.setVisibility(View.VISIBLE);
						listAdapter = new BooksAdapter(getActivity(),bookingPushData);
						listAdapter.set(event.getData().getResponse().getBookInfoList());
						pullToRefreshView.setAdapter(listAdapter);
                        for(BookInfo item : event.getData().getResponse().getBookInfoList()){
							if(item.getStatus().equals(PENDING) || item.getStatus().equals(APPROVED)) {
//								activityActions.openActiveBookInfo(item.getId());
								PushData data = new PushData(item.getId());
								data.setType("booking_"+item.getStatus());
								if(item.getStatus().equals(APPROVED))
								  data.setText("Ваша бронь подтверждена!");
								else if(item.getStatus().equals(FINISHED)){
								  data.setText("Ваша машина помыта!");
								}
								else if(item.getStatus().equals(REJECTED)){
								  data.setText("Ваша бронь отклонена.");
								}
								else
									data.setText("");
								data.setObjectId(item.getId());
								BA.getEventBus().post(new NewBookingPushRequestEvent(data));
							}
						}
					} else {// load next page
						listAdapter.add(event.getData().getResponse().getBookInfoList());
						for(BookInfo item : event.getData().getResponse().getBookInfoList()){
							if(item.getStatus().equals(PENDING) || item.getStatus().equals(APPROVED)) {
//								activityActions.openActiveBookInfo(item.getId());
								PushData data = new PushData(item.getId());
								data.setType("booking_"+item.getStatus());
								if(item.getStatus().equals(APPROVED))
									data.setText("Ваша бронь подтверждена!");
								else if(item.getStatus().equals(FINISHED)){
									data.setText("Ваша машина помыта!");
								}
								else if(item.getStatus().equals(REJECTED)){
									data.setText("Ваша бронь отклонена.");
								}
								else
									data.setText("");
								data.setObjectId(item.getId());
								BA.getEventBus().post(new NewBookingPushRequestEvent(data));
							}
						}
						listAdapter.notifyDataSetChanged();
						listAdapter.setPreloadStatus(false);
						listAdapter.notifyDataSetChanged();
					}
				}
				else{
					listAdapter = new BooksAdapter(getActivity(),bookingPushData);
					pullToRefreshView.setAdapter(listAdapter);
					pullToRefreshView.setVisibility(View.VISIBLE);
					noInternetLayout.setVisibility(View.GONE);
					progressBar.setVisibility(View.GONE);
					if(fragmentVisible) {
						ToastUtil.display(getActivity(), event.getData().getMessage());
					}
				}
			}else{
				listAdapter = new BooksAdapter(getActivity(),bookingPushData);
				pullToRefreshView.setAdapter(listAdapter);
				pullToRefreshView.setVisibility(View.VISIBLE);
				noInternetLayout.setVisibility(View.GONE);
				progressBar.setVisibility(View.GONE);
				ToastUtil.display(getActivity(),"Ошибка при получении данных с сервера");
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
//			if(fragmentVisible)
//				activityActions.hideShowFilterButton(1);
		}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		fragmentVisible = isVisibleToUser;
		if(fragmentVisible){
			isUserLoggedIn(false);
		}
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
	public void onBookingPushReceived(NewBookingPushResponseEvent event){
		if(loggedIn && event != null && event.getPush() != null){
			bookingPushData = event.getPush().getData();
//			listAdapter = new BooksAdapter(getActivity(),bookingPushData);
//			numberPage = 0;
//			loadData(numberPage, false);
		}
	}

	@OnClick(R.id.login)
	public void onLoginButtonClicked(){
		activityActions.login();
	}

	@Subscribe
	public void onLogoutRequested(AuthClientLogoutRequestEvent event){
		isUserLoggedIn(false);
		if(bookingPushData != null && bookingPushData.size() > 0)
			BA.getEventBus().post(new RemoveBulkBookingPushRequestEvent());
	}
}



