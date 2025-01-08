package com.driverspa.client.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.splunk.mint.Mint;

import com.driverspa.R;
import com.driverspa.activity.BaseActivity;
import com.driverspa.client.fragment.ClientBookingInfoFragment;
import com.driverspa.client.fragment.ClientBookingInfoFragment.ActivityActions;
import com.driverspa.client.fragment.ClientWasherInfoFragment;
import com.driverspa.model.Washer;
import com.driverspa.model.api.request.BookingRequest;
import com.driverspa.util.ActivityForResult;
import com.driverspa.util.JsonUtil;
import com.driverspa.util.UserPreferences;

import static com.driverspa.util.Constants.BOOKING_INFO_TYPE;
import static com.driverspa.util.Constants.EXTRA_BOOKING_ID;
import static com.driverspa.util.Constants.WASHER_BOOKING_REQUEST_DATA;
import static com.driverspa.util.Constants.WASHER_DATA_TO_BOOK;
import static com.driverspa.util.Constants.WASHER_ID;
import static com.driverspa.util.Constants.WASHER_NAME;

public class ClientBookingInfoActivity extends BaseActivity implements ActivityActions {

	ClientBookingInfoFragment fragment;
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
			String bookId = getIntent().getStringExtra(EXTRA_BOOKING_ID);
			String bookInfoType = getIntent().getStringExtra(BOOKING_INFO_TYPE);
			fragment = ClientBookingInfoFragment.newInstance(bookId, bookInfoType);
        	getFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
		}        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu_options, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				if(fragment != null && fragment.canBackPressed())
				finish();
				overridePendingTransitionWithCommonCloseTransition();
				break;
			case R.id.menu_options:
				if(fragment != null) fragment.showOptions();
				break;
		}
		return true;
	}

	@Override
	public void showProgressBar(boolean set) {
	}

		@Override
	public void goToHome() {
			setResult(RESULT_OK, new Intent());
			finish();
			overridePendingTransitionWithCommonCloseTransition();
	}

	@Override
	public void openWasher(Washer washer) {
		Intent intent = new Intent(this,ClientWasherInfoActivity.class);
		intent.putExtra(ClientWasherInfoFragment.EXTRA_WASHER_ID, washer.getId());
		startActivity(intent);
	}

	@Override
   public void editBooking(Washer washer, BookingRequest request) {
			finish();
			Intent intent = new Intent(this,ClientBookingActivity.class);
			intent.putExtra(WASHER_DATA_TO_BOOK, JsonUtil.serialize(washer) );
			intent.putExtra(WASHER_BOOKING_REQUEST_DATA, JsonUtil.serialize(request) );
			startActivity(intent);
	}

	@Override
	public void addReview(Washer washer) {
		Intent intent = new Intent(this,ClientAddWasherReviewActivity.class);
		intent.putExtra(WASHER_ID, washer.getId());
		intent.putExtra(WASHER_NAME, washer.getName());
		startActivityForResult(intent, ActivityForResult.ACTIVITY_REVIEW);
	}

	@Override
	public void writeToWashme(String email, String title) {
		Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, title);
		emailIntent.putExtra(Intent.EXTRA_TEXT, "");
		startActivity(Intent.createChooser(emailIntent, "Chooser Title"));
	}
}
