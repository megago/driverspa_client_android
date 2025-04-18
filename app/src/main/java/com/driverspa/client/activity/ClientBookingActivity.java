package com.driverspa.client.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.splunk.mint.Mint;

import com.driverspa.R;
import com.driverspa.activity.BaseActivity;
import com.driverspa.client.fragment.ClientBookingFragment;
import com.driverspa.client.fragment.ClientBookingFragment.ActivityActions;
import com.driverspa.model.Washer;
import com.driverspa.model.api.request.BookingRequest;
import com.driverspa.util.ActivityForResult;
import com.driverspa.util.JsonUtil;
import com.driverspa.util.UserPreferences;

import static com.driverspa.util.Constants.REQUEST_CODE;
import static com.driverspa.util.Constants.WASHER_BOOKING_REQUEST_DATA;
import static com.driverspa.util.Constants.WASHER_BOOK_FROM_MAP;
import static com.driverspa.util.Constants.WASHER_DATA_TO_BOOK;
import static com.driverspa.util.Constants.WASHER_ID;

public class ClientBookingActivity extends BaseActivity implements ActivityActions {

	public final static int SUCCESS_BOOK = 10001;
	  @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		  Mint.initAndStartSession(this.getApplication(), "b054ddc0");
		  Mint.setUserIdentifier(UserPreferences.getUserPhone(this));
		  setContentView(R.layout.activity_client_common);

		  Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
		  setSupportActionBar(mToolbar);
		  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		  getSupportActionBar().setDisplayShowTitleEnabled(false);

        if( savedInstanceState == null ) {
			String washerStr = getIntent().getStringExtra(WASHER_DATA_TO_BOOK);
			String requestStr = getIntent().getStringExtra(WASHER_BOOKING_REQUEST_DATA);
			String washerId = getIntent().getStringExtra(WASHER_ID);
			boolean fromMap = getIntent().getBooleanExtra(WASHER_BOOK_FROM_MAP,false);

			if(!fromMap) {
				Washer washer = JsonUtil.deserializeToWasher(washerStr);
				BookingRequest request = JsonUtil.deserializeToBookingRequest(requestStr);
				getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ClientBookingFragment.newInstance(washer, request)).commit();
			}
			else{
				getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ClientBookingFragment.newInstance(washerId)).commit();
			}
		}        
	  }
	  
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	            case android.R.id.home:
					setResult(RESULT_CANCELED, new Intent());
	                finish();
	                overridePendingTransitionWithCommonCloseTransition();
	                break;          
	        }
	        return true;
	    }
	    
		@Override
		public void showProgressBar(boolean set) {
		}

		@Override
		public void showTimeGrid(Washer washer, BookingRequest request) {
			Intent intent = new Intent(this,ClientBookingTimeTableActivity.class);
			intent.putExtra(WASHER_DATA_TO_BOOK, JsonUtil.serialize(washer) );
			intent.putExtra(WASHER_BOOKING_REQUEST_DATA, JsonUtil.serialize(request) );
			intent.putExtra(REQUEST_CODE, ActivityForResult.ACTIVITY_TIMETABLE_BOOK);
			startActivityForResult(intent, ActivityForResult.ACTIVITY_TIMETABLE_BOOK);
		}
        
	    @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {	
		super.onActivityResult(requestCode, resultCode, arg2);
		switch(requestCode){
		case ActivityForResult.ACTIVITY_TIMETABLE_BOOK:
	    	if(resultCode == RESULT_OK){
			  setResult(RESULT_OK, new Intent());
	 		  finish();
		  }
		}
	}	
}
