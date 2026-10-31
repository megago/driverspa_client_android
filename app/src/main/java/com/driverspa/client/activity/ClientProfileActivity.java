package com.driverspa.client.activity;
import com.driverspa.BA;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import android.view.MenuItem;
import com.splunk.mint.Mint;
import com.driverspa.R;
import com.driverspa.activity.BaseActivity;
import com.driverspa.client.fragment.ClientProfileFragment;
import com.driverspa.util.UserPreferences;

public class ClientProfileActivity extends BaseActivity implements ClientProfileFragment.ActivityActions{

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

	    // Tint the back (up) arrow white so it stays visible on the toolbar.
	    Drawable navIcon = mToolbar.getNavigationIcon();
	    if (navIcon != null) {
	        navIcon.setColorFilter(ContextCompat.getColor(this, R.color.White), PorterDuff.Mode.SRC_ATOP);
	        mToolbar.setNavigationIcon(navIcon);
	    }

        if( savedInstanceState == null ) {
        	getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new ClientProfileFragment()).commitAllowingStateLoss();
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
}
