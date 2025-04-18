package com.driverspa.client.activity;

import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.splunk.mint.Mint;

import com.driverspa.R;
import com.driverspa.activity.BaseActivity;
import com.driverspa.client.fragment.ClientAboutUSFragment;
import com.driverspa.util.UserPreferences;

public class ClientAboutUSActivity extends BaseActivity implements ClientAboutUSFragment.ActivityActions{
	public static FragmentManager fragmentManager;
	  @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		  Mint.initAndStartSession(this.getApplication(), "b054ddc0");
		  Mint.setUserIdentifier(UserPreferences.getUserPhone(this));
        setContentView(R.layout.activity_common);

		  Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
		  setSupportActionBar(mToolbar);
		  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		  getSupportActionBar().setDisplayShowTitleEnabled(false);

          fragmentManager = getSupportFragmentManager();

        if( savedInstanceState == null ) {
			Fragment fragment = new ClientAboutUSFragment();
        	getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
		}        
	  }
	  
	    @Override
		public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	            case android.R.id.home:
	            	setResult(RESULT_OK, new Intent());
	                finish();
//					overridePendingTransition(0,0);
	                overridePendingTransitionWithCommonCloseTransition();
	                break;          
	        }
	        return true;
	    }

}
