package com.driverspa.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.splunk.mint.Mint;

import java.util.ArrayList;

import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.client.activity.ClientHomeActivity;
import com.driverspa.client.fragment.ClientRegisterUserFragment;
import com.driverspa.client.fragment.ClientRegistrationFragment;
import com.driverspa.client.fragment.ClientVerificationFragment;
import com.driverspa.db.WashmeOrmLiteSqlHelper;
import com.driverspa.fragment.MainFragment;
//import com.driverspa.gcm.RegistrationIntentService;

import com.driverspa.model.CurrentGeoPosition;
import com.driverspa.model.UserLocation;
import com.driverspa.util.ToastUtil;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.ws.UserLocationUpdateEvent;

/**
 * Used for logging in, checking whether user is logged in.
 * @author Yerzhan
 *
 */
public class MainActivity extends BaseActivity implements
		MainFragment.ActivityActions,
		ClientRegistrationFragment.ActivityActions,
		ClientVerificationFragment.ActivityActions,
        ClientRegisterUserFragment.ActivityActions{

	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Mint.initAndStartSession(this.getApplication(), "b054ddc0");
		setContentView(R.layout.activity_main);

        if( savedInstanceState == null ) {
        	Fragment fragment = new MainFragment();
        	getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commitAllowingStateLoss();
        }

		// create database tables. Next time db request won't take much time
		new WashmeOrmLiteSqlHelper(BA.getContext());
    }

	private boolean processUserLoginCheck() {
    	boolean isLoggedIn = UserPreferences.isUserLoggedIn(this);
    	if( isLoggedIn ){
			UserLocation userLocation = new UserLocation();
			ArrayList<Double> lonLat;
			if (!TextUtils.isEmpty(UserPreferences.getGPSData(BA.getContext()))) {
				CurrentGeoPosition gpsData = CurrentGeoPosition.deserialize(UserPreferences.getGPSData(BA.getContext()));
				lonLat = new ArrayList<Double>();
				lonLat.add(gpsData.getLng());
				lonLat.add(gpsData.getLat());
				userLocation.setLonLat(lonLat);
			}

			String city;
			if(!TextUtils.isEmpty(UserPreferences.getCity(BA.getContext()))) {
				city = UserPreferences.getCity(BA.getContext());
				userLocation.setCity(city);
			}
			BA.getEventBus().post(new UserLocationUpdateEvent(userLocation));

			openClientHomeActivity();

    	}
    	return isLoggedIn;    
    }	    

	private boolean processAdminLoginCheck() {
    	boolean isLoggedIn = UserPreferences.isAdminLoggedIn(this);
    	if( isLoggedIn ){
			openAdminRegistration();
			//openAdminHomeActivity();
    	}
    	return isLoggedIn;
    }

	//Client
	@Override
	public void openClientRegistration() {
		openClientHomeActivity();
	}

	@Override
	public void openClientVerification() {
		replaceFragment(new ClientVerificationFragment());
	}

	@Override
	public void openClientHomeActivity() {
		finish();
		overridePendingTransition(0,0);
		startActivity(new Intent(this, ClientHomeActivity.class).putExtra(OPENING_ANIMATION, false));
	}

	//TODO Administration registration
	@Override
	public void openAdminRegistration() {
		try {
			Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.driverspabox");
			if (launchIntent != null) {
				startActivity(launchIntent);//null pointer check in case package name was not found
			}
			else{
				openAdminMarket();
			}
		}
		catch(Exception e) {
			openAdminMarket();
		}
	}

	@Override
	public void openAdminHomeActivity() {

	}

	public void openAdminMarket() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://details?id=com.driverspabox"));
		startActivity(intent);
	}


	public void registerGCM() {
//		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String PUSH_TOKEN = UserPreferences.getPushToken(this);//sharedPreferences.getString(RegistrationIntentService.PUSH_TOKEN, "");
		if (TextUtils.isEmpty(PUSH_TOKEN) && checkPlayServices()) {
			//TODO register service
//			Intent intent = new Intent(MainActivity.this, RegistrationIntentService.class);
//			startService(intent);
		}
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				ToastUtil.displayAtTop(this,"This device is not supported.");
			}
			return false;
		}
		return true;
	}

}