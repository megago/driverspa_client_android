package com.driverspa.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toolbar;
import android.view.MenuItem;

import com.splunk.mint.Mint;

import com.driverspa.R;
import com.driverspa.activity.BaseActivity;
import com.driverspa.client.fragment.ClientBookingTimeTableFragment;
import com.driverspa.client.fragment.ClientBookingTimeTableFragment.ActivityActions;
import com.driverspa.model.Washer;
import com.driverspa.model.api.request.BookingRequest;
import com.driverspa.util.ActivityForResult;
import com.driverspa.util.JsonUtil;
import com.driverspa.util.UserPreferences;

import static com.driverspa.util.Constants.BOOKING_INFO_TYPE;
import static com.driverspa.util.Constants.EXTRA_BOOKING_ID;
import static com.driverspa.util.Constants.FIRST_BOOK;
import static com.driverspa.util.Constants.REQUEST_CODE;
import static com.driverspa.util.Constants.WASHER_BOOKING_REQUEST_DATA;
import static com.driverspa.util.Constants.WASHER_DATA_TO_BOOK;

public class ClientBookingTimeTableActivity extends BaseActivity implements ActivityActions {

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
			int requestCode =  getIntent().getIntExtra(REQUEST_CODE,0);
			Washer washer = JsonUtil.deserializeToWasher(washerStr);
			BookingRequest request = JsonUtil.deserializeToBookingRequest(requestStr);
        	getFragmentManager().beginTransaction().add(R.id.fragment_container, ClientBookingTimeTableFragment.newInstance(washer, request, requestCode)).commit();
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
		public void showBookingInfo(String bookingId, Washer washer,BookingRequest request) {
			setResult(RESULT_OK, new Intent());
			finish();
			Intent intent = new Intent(this,ClientBookingInfoActivity.class);
			intent.putExtra(EXTRA_BOOKING_ID, bookingId );
			intent.putExtra(BOOKING_INFO_TYPE, FIRST_BOOK );
			startActivityForResult(intent, ActivityForResult.ACTIVITY_TIMETABLE_BOOK);
		}
}
