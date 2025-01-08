package com.driverspa.client.activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.splunk.mint.Mint;

import com.driverspa.R;
import com.driverspa.activity.BaseActivity;
import com.driverspa.client.fragment.ClientSettingsFragment;
import com.driverspa.util.UserPreferences;

public class ClientSettingsActivity extends BaseActivity{
	  @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		  Mint.initAndStartSession(this.getApplication(), "b054ddc0");
		  Mint.setUserIdentifier(UserPreferences.getUserPhone(this));
        setContentView(R.layout.activity_common);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        setTitle("");        

        if( savedInstanceState == null ) {
        	getFragmentManager().beginTransaction().add(R.id.fragment_container, new ClientSettingsFragment()).commitAllowingStateLoss();
		}        
	  }
	  
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	            case android.R.id.home:
	                finish();
	                overridePendingTransition(0, 0);
	                break;          
	        }
	        return true;
	    }

}
