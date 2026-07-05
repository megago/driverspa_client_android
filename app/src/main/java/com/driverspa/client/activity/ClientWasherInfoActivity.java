package com.driverspa.client.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.splunk.mint.Mint;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.driverspa.R;
import com.driverspa.activity.BaseActivity;
import com.driverspa.activity.GalleryPhotosActivity;
import com.driverspa.activity.LoginActivity;
import com.driverspa.client.fragment.ClientServiceDialogFragment;
import com.driverspa.client.fragment.ClientTimetableDialogFragment;
import com.driverspa.client.fragment.ClientWasherInfoFragment;
import com.driverspa.client.fragment.ClientWasherInfoFragment.ActivityActions;
import com.driverspa.client.fragment.ClientWasherReviewFragment;
import com.driverspa.model.BookInfo;
import com.driverspa.model.PhotoParcelable;
import com.driverspa.model.Washer;
import com.driverspa.model.WasherPublic;
import com.driverspa.model.api.request.BookingRequest;
import com.driverspa.util.ActivityForResult;
import com.driverspa.util.Constants;
import com.driverspa.util.JsonUtil;
import com.driverspa.util.UserPreferences;

import static com.driverspa.util.Constants.REQUEST_CODE;
import static com.driverspa.util.Constants.WASHER_BOOKING_REQUEST_DATA;
import static com.driverspa.util.Constants.WASHER_DATA;
import static com.driverspa.util.Constants.WASHER_DATA_TO_BOOK;
import static com.driverspa.util.Constants.WASHER_ID;
import static com.driverspa.util.Constants.WASHER_NAME;

public class ClientWasherInfoActivity extends BaseActivity implements ActivityActions, ClientServiceDialogFragment.ActivityActions,
		ClientTimetableDialogFragment.ActivityActions {
	public static FragmentManager fragmentManager;

	private Toolbar mToolbar;

	Menu menu;
	ClientWasherInfoFragment fragment;

	  @Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		  Mint.initAndStartSession(this.getApplication(), "b054ddc0");
		  Mint.setUserIdentifier(UserPreferences.getUserPhone(this));
          setContentView(R.layout.activity_client_common);

		  mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
		  setSupportActionBar(mToolbar);
		  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		  getSupportActionBar().setDisplayShowTitleEnabled(false);
		  tintToolbarIconsWhite();
		  fragmentManager = getSupportFragmentManager();
         if (arg0 == null) {
			fragment = new ClientWasherInfoFragment();
        	fragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commitAllowingStateLoss();
         }
	  }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu_add, menu);
		getMenuInflater().inflate(R.menu.menu_options, menu);
		this.menu = menu;
		//Initially hide add button
		MenuItem item = menu.findItem(R.id.menu_add);
		item.setVisible(false);
		tintToolbarIconsWhite();
		return true;
	}

	private void tintToolbarIconsWhite() {
		if (mToolbar == null) {
			return;
		}
		Drawable nav = mToolbar.getNavigationIcon();
		if (nav != null) {
			nav = nav.mutate();
			nav.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
			mToolbar.setNavigationIcon(nav);
		}
		Drawable overflow = mToolbar.getOverflowIcon();
		if (overflow != null) {
			overflow = overflow.mutate();
			overflow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
			mToolbar.setOverflowIcon(overflow);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				setResult(RESULT_OK, new Intent());
				finish();
				overridePendingTransitionWithCommonCloseTransition();
				break;
			case R.id.menu_options:
				if(fragment != null)
					fragment.showOptions();
				break;
		}
		return true;
	}

	@Override
	public void home() {
		finish();
		overridePendingTransitionWithCommonCloseTransition();
	}

	@Override
	public void showProgressBar(boolean set) {
	}

	@Override
	public void startBooking(Washer washer) {
		Intent intent = new Intent(this,ClientBookingActivity.class);
		intent.putExtra(WASHER_DATA_TO_BOOK, JsonUtil.serialize(washer) );
		intent.putExtra(REQUEST_CODE, ActivityForResult.ACTIVITY_WASHER_INFO);
		startActivityForResult(intent, ActivityForResult.ACTIVITY_WASHER_INFO);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
		super.onActivityResult(requestCode, resultCode, arg2);
		switch(requestCode){
			case ActivityForResult.ACTIVITY_WASHER_INFO:
				if(resultCode == RESULT_OK){
					finish();
				}
		}
	}

		@Override
		public void showReview(Washer washer) {
			Intent intent = new Intent(this,ClientWasherReviewActivity.class);
			intent.putExtra(ClientWasherReviewFragment.WASHER_DATA, JsonUtil.serialize(washer));
			startActivity(intent);
		}

		@Override
		public void showTimeTable(Washer washer) {
			Intent intent = new Intent(this,ClientBookingTimeTableActivity.class);
			intent.putExtra(WASHER_DATA_TO_BOOK, JsonUtil.serialize(washer) );
			intent.putExtra(WASHER_BOOKING_REQUEST_DATA, JsonUtil.serialize(new BookingRequest()));
			intent.putExtra(REQUEST_CODE, ActivityForResult.ACTIVITY_TIMETABLE_INFO);
			startActivityForResult(intent, ActivityForResult.ACTIVITY_TIMETABLE_INFO);
		}

		@Override
		public void showWasherImages(ArrayList<PhotoParcelable> washerPhotos) {
			startActivityForResult(GalleryPhotosActivity.newIntent(this, washerPhotos, 0), ActivityForResult.ACTIVITY_GALLERY_PHOTOS);
		}

	@Override
	public void hideShowMenuBookButton(boolean show) {
	}

	@Override
	public void login() {
		startActivity(new Intent(this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).putExtra(OPENING_ANIMATION,false));
	}

	@Override
		public void showPrices(Washer washer) {
			Intent intent = new Intent(this,ClientPriceDetailActivity.class);
			intent.putExtra(WASHER_DATA, JsonUtil.serialize(washer));
			startActivity(intent);
		}

		@Override
		public void showMapMarker(Washer washer) {
			if(washer != null && washer.getLonLat() != null) {
				Intent intent = new Intent(this, ClientAddressMapActivity.class);
				intent.putExtra(ClientAddressMapActivity.LOC_LAT, washer.getLonLat().get(1));
				intent.putExtra(ClientAddressMapActivity.LOC_LON, washer.getLonLat().get(0));
				intent.putExtra(Constants.WASHER_DATA, washer.serialize());
				startActivity(intent);
			}
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

	@Override
	public void openCampaignInfo(String washerId, String washer) {
		Intent intent = new Intent(this,ClientCampaignInfoActivity.class);
		intent.putExtra(Constants.EXTRA_WASHER_ID, washerId);
		intent.putExtra(Constants.WASHER_DATA, washer);
		startActivity(intent);
	}

	@Override
	public void setSelectedServices(WasherPublic washer, List<BookInfo.PriceDetail> priceDetails, List<Integer> services, List<String> groupServices, Double price, int minutes) {
//		fragment.setSelectedServices(washer, priceDetails,services,groupServices,price,minutes);
	}

	@Override
	public void setBookingTime(WasherPublic washer, Date bookTime) {
//		fragment.setBookingTime(washer,bookTime);
	}
}
