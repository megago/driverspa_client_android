package com.driverspa.client.fragment;

import android.app.Activity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.assist.BaseAssist;
import com.driverspa.model.api.request.AddReviewRequest;
import com.driverspa.util.ToastUtil;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.ws.AddReviewRequestEvent;
import com.driverspa.util.otto.ws.AddReviewResponseEvent;

import static com.driverspa.util.Constants.URL_PREFIX;
import static com.driverspa.util.Constants.WASHER_ID;
import static com.driverspa.util.Constants.WASHER_NAME;

public class ClientAddWasherReviewFragment extends ClientBaseFragment {
    String phone = UserPreferences.getUserPhone(BA.getContext());
            
	public interface ActivityActions{
		public void showProgressBar(boolean set);
		public void setReviewAndClose();
	}		
	
    int mark = 0;	
	@BindView(R.id.txtTitle)
	TextView title;
	@BindView(R.id.feedback_text)
	EditText feedBackText;
	@BindView(R.id.star1)
	ImageView star1;
	@BindView(R.id.star2)
	ImageView star2;
	@BindView(R.id.star3)
	ImageView star3;
	@BindView(R.id.star4)
	ImageView star4;
	@BindView(R.id.star5)
	ImageView star5;
    
	private ActivityActions activityActions;
	private String washerId;
	private String washerName;
	
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
			return inflater.inflate(R.layout.fragment_client_add_washer_review, container,false);
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setHasOptionsMenu(true);
			washerId = getActivity().getIntent().getStringExtra(WASHER_ID);
			washerName = getActivity().getIntent().getStringExtra(WASHER_NAME);
		}
		
	    @Override
	    public void onViewCreated(View view, Bundle savedInstanceState) {	       
	        ButterKnife.bind(this, view);
			Toolbar mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_actionbar);
			TextView titleView = (TextView) mToolbar.findViewById(R.id.action_bar_title);
			titleView.setText(washerName);
			titleView.setVisibility(View.VISIBLE);
			title.setVisibility(View.GONE);
			title.setText(washerName);
	    }
	   
		@Override
		public void onDestroy() {
			super.onDestroy();		
		}

		@Override
		public void onResume() {
			super.onResume();			
			BA.getEventBus().register(this);
		}
		
		@Override
		public void onPause() {
			super.onPause();
			BA.getEventBus().unregister(this);
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
		
		
		@OnClick(R.id.star1)
		public void onClickStar1(){
				star1.setSelected(true);
				star2.setSelected(false);			
				star3.setSelected(false);						
				star4.setSelected(false);									
				star5.setSelected(false);				
				mark = 1;
		}
		
		@OnClick(R.id.star2)
		public void onClickStar2(){
				star1.setSelected(true);
				star2.setSelected(true);
				star3.setSelected(false);						
				star4.setSelected(false);									
				star5.setSelected(false);									
				mark = 2;
		}	
		
		@OnClick(R.id.star3)
		public void onClickStar3(){
				star1.setSelected(true);
				star2.setSelected(true);			
				star3.setSelected(true);
				star4.setSelected(false);									
				star5.setSelected(false);
				mark = 3;
		}
		
		@OnClick(R.id.star4)
		public void onClickStar4(){		
				star1.setSelected(true);
				star2.setSelected(true);			
				star3.setSelected(true);						
				star4.setSelected(true);
				star5.setSelected(false);
				mark = 4;
		}
		
		@OnClick(R.id.star5)
		public void onClickStar5(){
				star1.setSelected(true);
				star2.setSelected(true);			
				star3.setSelected(true);						
				star4.setSelected(true);						
				star5.setSelected(true);
				mark = 5;
		}	
	
		@OnClick(R.id.btnAddReview)
		public void onAddReviewPressed(){			
			if(mark == 0){
				ToastUtil.display(getActivity(), "Пожалуйста, дайте оценку!");
				return;
			}
			if(TextUtils.isEmpty(feedBackText.getText().toString())){
				ToastUtil.display(getActivity(), "Пожалуйста, напишите что думаете о мойке");
				return;
			}
			setWaitScreen(true);
			AddReviewRequest request = new AddReviewRequest();
			request.setCarwash(URL_PREFIX+washerId);
			request.setMark(mark);
			request.setText(feedBackText.getText().toString());			
			BA.getEventBus().post(new AddReviewRequestEvent(request));
		}

		@Subscribe
		public void onAddReviewResponseEvent(AddReviewResponseEvent event){
			setWaitScreen(false);
			if(event.getResponse() != null){
			   if(BaseAssist.isSuccess(event.getResponse())){
				  activityActions.setReviewAndClose();   
			   }
			   else{
				  ToastUtil.display(getActivity(), event.getResponse().getMessage());
			   }
		    }
	   }
}
