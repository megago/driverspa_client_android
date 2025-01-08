package com.driverspa.client.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toolbar;
import android.view.MenuItem;
import com.splunk.mint.Mint;
import com.driverspa.R;
import com.driverspa.activity.BaseActivity;
import com.driverspa.client.fragment.ClientProfileFragment;
import com.driverspa.util.ToastUtil;
import com.driverspa.util.UserPreferences;

public class ClientProfileActivity extends BaseActivity implements ClientProfileFragment.ActivityActions{

	public final int STORAGE_PERMISSION = 10001;

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case STORAGE_PERMISSION: {

				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					// permission was granted, yay! Do the
					// contacts-related task you need to do.
				} else {

					// permission denied, boo! Disable the
					// functionality that depends on this permission.
					ToastUtil.display(ClientProfileActivity.this,"Доступ запрещен");
//					Toast.makeText(ClientProfileActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
				}
				return;
			}

			// other 'case' lines to check for other
			// permissions this app might request
		}
	}

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
        	getFragmentManager().beginTransaction().add(R.id.fragment_container, new ClientProfileFragment()).commitAllowingStateLoss();
		}

		if(!checkIfAlreadyhavePermission()) {
			ActivityCompat.requestPermissions(ClientProfileActivity.this,
					new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
					STORAGE_PERMISSION);
		}
	  }

	private boolean checkIfAlreadyhavePermission() {
		int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
		if (result == PackageManager.PERMISSION_GRANTED) {
			return true;
		} else {
			return false;
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
