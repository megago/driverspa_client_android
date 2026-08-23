package com.driverspa.client.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.splunk.mint.Mint;

import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.activity.BaseActivity;
import com.driverspa.activity.LoginActivity;
import com.driverspa.client.fragment.CampaignInfoFragment;
import com.driverspa.model.Washer;
import com.driverspa.util.ActivityForResult;
import com.driverspa.util.JsonUtil;
import com.driverspa.util.ToastUtil;
import com.driverspa.util.UserPreferences;

import static com.driverspa.util.Constants.EXTRA_WASHER_ID;
import static com.driverspa.util.Constants.REQUEST_CODE;
import static com.driverspa.util.Constants.WASHER_DATA;
import static com.driverspa.util.Constants.WASHER_DATA_TO_BOOK;

public class ClientCampaignInfoActivity extends BaseActivity implements CampaignInfoFragment.ActivityActions {

	CampaignInfoFragment fragment;
	  @SuppressLint("SuspiciousIndentation")
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
			String washerId = getIntent().getStringExtra(EXTRA_WASHER_ID);
			String washer = getIntent().getStringExtra(WASHER_DATA);
			fragment = CampaignInfoFragment.newInstance(washerId, washer);
        	getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
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
	protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
		super.onActivityResult(requestCode, resultCode, arg2);
		switch(requestCode){
			case ActivityForResult.ACTIVITY_CAMPAIGN_INFO:
				if(resultCode == RESULT_OK){
					finish();
				}
		}
	}

	@Override
	public void noCampaign() {
		ToastUtil.display(BA.getContext(),BA.str(R.string.no_active_promos));
		finish();
		overridePendingTransitionWithCommonCloseTransition();
	}

	@Override
	public void login() {
		startActivity(new Intent(this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).putExtra(OPENING_ANIMATION,false));
	}

	@Override
	public void startBooking(Washer washer) {
		Intent intent = new Intent(this,ClientBookingActivity.class);
		intent.putExtra(WASHER_DATA_TO_BOOK, JsonUtil.serialize(washer) );
		intent.putExtra(REQUEST_CODE, ActivityForResult.ACTIVITY_CAMPAIGN_INFO);
		startActivityForResult(intent, ActivityForResult.ACTIVITY_CAMPAIGN_INFO);
	}
}
