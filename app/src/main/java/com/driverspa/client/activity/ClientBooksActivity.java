package com.driverspa.client.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;

import com.splunk.mint.Mint;

import com.driverspa.R;
import com.driverspa.activity.BaseActivity;
import com.driverspa.client.fragment.ClientBooksFragment;
import com.driverspa.util.UserPreferences;

import static com.driverspa.util.Constants.EXTRA_BOOKING_ID;

public class ClientBooksActivity extends BaseActivity implements ClientBooksFragment.ActivityActions{
	
	  @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		  Mint.initAndStartSession(this.getApplication(), "b054ddc0");
		  Mint.setUserIdentifier(UserPreferences.getUserPhone(this));
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_common);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        setTitle("");        

        if( savedInstanceState == null ) {
        	getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new ClientBooksFragment()).commitAllowingStateLoss();
		}        
	  }
	  
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) { 
	            case android.R.id.home:
	                finish();
	                overridePendingTransitionWithCommonCloseTransition();
	                break;          
	        }
	        return true;
	    }

		@Override
		public void openBookInfo(String bookId) {		
			Intent intent = new Intent(this,ClientBookingInfoActivity.class);
			intent.putExtra(EXTRA_BOOKING_ID, bookId );	
			startActivity(intent);								
		}

	@Override
	public void openActiveBookInfo(String bookId) {
		Intent intent = new Intent(this,ClientActiveBookingInfoActivity.class);
		intent.putExtra(EXTRA_BOOKING_ID, bookId );
		startActivity(intent);

	}

	@Override
	public void hideShowFilterButton(int tab) {

	}

	@Override
	public void login() {

	}
}
