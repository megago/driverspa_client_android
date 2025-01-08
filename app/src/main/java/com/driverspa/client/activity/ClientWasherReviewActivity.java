package com.driverspa.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.splunk.mint.Mint;

import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.activity.BaseActivity;
import com.driverspa.activity.LoginActivity;
import com.driverspa.client.fragment.ClientWasherReviewFragment;
import com.driverspa.model.Washer;
import com.driverspa.util.ActivityForResult;
import com.driverspa.util.JsonUtil;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.ws.WasherInfoRequestEvent;

import static com.driverspa.util.Constants.WASHER_ID;
import static com.driverspa.util.Constants.WASHER_NAME;

public class ClientWasherReviewActivity extends BaseActivity  implements ClientWasherReviewFragment.ActivityActions{
    	
	Washer washer;
	ClientWasherReviewFragment fragment;
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
			String washerStr = getIntent().getStringExtra(ClientWasherReviewFragment.WASHER_DATA);
			washer = JsonUtil.deserializeToWasher(washerStr);
        	getFragmentManager().beginTransaction().add(R.id.fragment_container, ClientWasherReviewFragment.newInstance(washer)).commit();
		 }        
	   }

	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	    	super.onCreateOptionsMenu(menu);	    	
//	    	getMenuInflater().inflate(R.menu.menu_add, menu);
	    	return true;
	    }
	    
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) { 
	            case android.R.id.home:
	                finish();
	                overridePendingTransitionWithCommonCloseTransition();
	                break;          
	            case R.id.menu_add:
	            	addReview();
	                break;             	                
	        }
	        return true;
	    }

	    @Override
		public void addReview() {
			if(washer != null) {
				Intent intent = new Intent(this, ClientAddWasherReviewActivity.class);
				intent.putExtra(WASHER_ID, washer.getId());
				intent.putExtra(WASHER_NAME, washer.getName());
				startActivityForResult(intent, ActivityForResult.ACTIVITY_REVIEW);
			}
		}	    
		
	    @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {	
		super.onActivityResult(requestCode, resultCode, arg2);
		switch(requestCode){
		case ActivityForResult.ACTIVITY_REVIEW:
	    	if(resultCode == RESULT_OK){
				if(washer!= null)
				   BA.getEventBus().post(new WasherInfoRequestEvent(washer.getId()));
//	    		fragment.requestReviews();
		  }
		}
	}

	@Override
	public void login() {
		startActivity(new Intent(this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).putExtra(OPENING_ANIMATION,false));
	}
}
