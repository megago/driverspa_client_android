package com.driverspa.client.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.splunk.mint.Mint;

import com.driverspa.R;
import com.driverspa.activity.BaseActivity;
import com.driverspa.client.fragment.ClientAddWasherReviewFragment;
import com.driverspa.util.UserPreferences;

public class ClientAddWasherReviewActivity extends BaseActivity implements ClientAddWasherReviewFragment.ActivityActions{
	
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
        	getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new ClientAddWasherReviewFragment()).commitAllowingStateLoss();
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
	    public boolean onCreateOptionsMenu(Menu menu) {
	    	super.onCreateOptionsMenu(menu);	    	
	    	return true;
	    }	    
	    
	   @Override
	   public void showProgressBar(boolean set) {
	   }

	@Override
	public void setReviewAndClose() {
		setResult(RESULT_OK, new Intent());
		finish();				
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
    	setResult(RESULT_CANCELED, new Intent());
		finish();
		overridePendingTransitionWithCommonCloseTransition();

	}
}
