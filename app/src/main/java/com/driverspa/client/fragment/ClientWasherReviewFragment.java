package com.driverspa.client.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.squareup.otto.Subscribe;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.adapter.WasherReviewAdapter;
import com.driverspa.model.Washer;
import com.driverspa.util.Functions;
import com.driverspa.util.JsonUtil;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.ws.WasherInfoRequestEvent;
import com.driverspa.util.otto.ws.WasherInfoResponseEvent;

public class ClientWasherReviewFragment extends ClientBaseFragment {
		
	    public static final String WASHER_DATA = "WASHER_DATA";

	public interface ActivityActions {
		public void addReview();
		public void login();
	}

	public static ClientWasherReviewFragment newInstance(Washer washer) {
    		ClientWasherReviewFragment fragment = new ClientWasherReviewFragment();
    		Bundle bundle = new Bundle();
    		bundle.putString(WASHER_DATA, JsonUtil.serialize(washer) );
    		fragment.setArguments(bundle);
    		return fragment;
    	}
    	
	    private WasherReviewAdapter listAdapter;
	    @InjectView(R.id.listView)
		ListView listView;	   
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
	    Location currentLoc;
	    Washer washer;
	    private ActivityActions activityActions;
	   
		@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	        return inflater.inflate(R.layout.fragment_client_washer_review, container, false);
	    }
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setHasOptionsMenu(true);					
			String washerStr = getArguments().getString(WASHER_DATA);
			washer = JsonUtil.deserializeToWasher(washerStr);
		}

	    @Override
	    public void onViewCreated(View view, Bundle savedInstanceState) {	       
	        ButterKnife.inject(this, view);
	        listAdapter = new WasherReviewAdapter(getActivity());
			Toolbar mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_actionbar);
			TextView titleView = (TextView) mToolbar.findViewById(R.id.action_bar_title);
			titleView.setText(washer.getName());
			titleView.setVisibility(View.VISIBLE);
			title.setVisibility(View.GONE);
			title.setText(washer.getName());
	        listView.setHeaderDividersEnabled(false);
	        listView.setFooterDividersEnabled(false);	   
	        View footerView = new View(this.getActivity());
	        footerView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, (int) Functions.dipToPixels(getActivity(), 1)));
	        footerView.setBackgroundColor(getActivity().getResources().getColor(R.color.list_divider));
	        listView.addFooterView(footerView);
	        progressBar.setVisibility(View.GONE);
	        noInternetLayout.setVisibility(View.GONE);
			requestReviews();
	    }
						 
        @OnClick(R.id.repeatConnect)
	    public void onRepeatConnect(){
				if (Functions.checkOnline(getActivity())) {
				} else {					
					progressBar.setVisibility(View.GONE);
					noInternetLayout.setVisibility(View.VISIBLE);
					listView.setVisibility(View.GONE);
			}
        }      
        
		@Subscribe
		public void onWasherInfoReceived(WasherInfoResponseEvent event) {
			setWaitScreen(false);
			if(event.getWasher() !=null && event.getWasher().getReviews() !=null && event.getWasher().getReviews().size() > 0){
				List<Washer.Review> reviewAdapterData = event.getWasher().getReviews();
				Collections.sort(reviewAdapterData, new Comparator<Washer.Review>() {
					@Override
					public int compare(Washer.Review lhs, Washer.Review rhs) {
						return rhs.getId().compareTo(lhs.getId());
					}
				});

			 listAdapter.set(reviewAdapterData);
	         listView.setAdapter(listAdapter);
	         listAdapter.notifyDataSetChanged();
	        }
			else{				
				addEmptyTextView(listView, "Нет отзывов");
		    }
		}
		
		public void requestReviews(){
	        View emptyView = listView.getEmptyView();
			if(emptyView != null)
				emptyView.setVisibility(View.GONE);			
			addProgressBar(listView);
			BA.getEventBus().post(new WasherInfoRequestEvent(washer.getId()));
		}
				
		/**
		 * Received location and request people nearby
		 * @param
		 */

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
				listView.setVisibility(View.GONE);
				if (listAdapter != null) {
					listAdapter.setPreloadStatus(false);
					listAdapter.notifyDataSetChanged();
			        listView.setAdapter(listAdapter);
				}
			}
		}
		
		@Override
		public void onPause() {
			super.onPause();
			BA.getEventBus().unregister(this);
			if(progressBar.getVisibility() == View.VISIBLE) {
				progressBar.setVisibility(View.GONE);
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

	    @OnClick(R.id.btnAddReview)
	public void onAddReviewClicked(){
			boolean loggedIn = UserPreferences.isUserLoggedIn(BA.getContext());
			if(loggedIn) {
				activityActions.addReview();
			}
			else
				showLoginWarning();
	}

	private void showLoginWarning(){
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
		dialog.setTitle("Необходимо войти");
		dialog.setPositiveButton("Войти", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				activityActions.login();
			}
		});
		dialog.setNegativeButton("Отмена",null);
		dialog.show();
	}

}



