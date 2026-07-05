package com.driverspa.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.splunk.mint.Mint;

import java.util.ArrayList;

import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.assist.UserSelfAssist;
import com.driverspa.client.fragment.ClientCreatePasswordFragment;
import com.driverspa.client.fragment.ClientPasswordLoginFragment;
import com.driverspa.client.fragment.ClientRegisterUserFragment;
import com.driverspa.client.fragment.ClientRegistrationFragment;
import com.driverspa.client.fragment.ClientResetPasswordFragment;
import com.driverspa.client.fragment.ClientVerificationFragment;
import com.driverspa.client.fragment.ClientWhatsappFragment;
import com.driverspa.fragment.MainFragment;
//import com.driverspa.gcm.RegistrationIntentService;
import com.driverspa.model.CurrentGeoPosition;
import com.driverspa.model.User;
import com.driverspa.model.UserLocation;
import com.driverspa.util.ToastUtil;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.ws.UserLocationUpdateEvent;

/**
 * Used for logging in, checking whether user is logged in.
 * @author Yerzhan
 *
 */
public class LoginActivity extends BaseActivity implements
		MainFragment.ActivityActions,
		ClientRegistrationFragment.ActivityActions,
		ClientVerificationFragment.ActivityActions,
		ClientPasswordLoginFragment.ActivityActions,
		ClientCreatePasswordFragment.ActivityActions,
		ClientResetPasswordFragment.ActivityActions,
        ClientRegisterUserFragment.ActivityActions{

	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Mint.initAndStartSession(this.getApplication(), "b054ddc0");
		setContentView(R.layout.activity_main);
		//TODO for release version only astana city
		// check if user is logged in

        boolean userLoggedIn = processUserLoginCheck();
        if( userLoggedIn )
        	return;
        if( savedInstanceState == null ) {
			openClientRegistration();
        }
	}

	private boolean processUserLoginCheck() {
		
    	boolean isLoggedIn = UserPreferences.isUserLoggedIn(this);
    	if( isLoggedIn ){
			openClientHomeActivity();
    	}
    	return isLoggedIn;    
    }	    

	private boolean processAdminLoginCheck() {
		
    	boolean isLoggedIn = UserPreferences.isAdminLoggedIn(this);
    	if( isLoggedIn ){
    		openAdminHomeActivity();
    	}
    	return isLoggedIn;
    }

	//Client
	@Override
	public void openClientRegistration() {
		replaceFragment(new ClientRegistrationFragment());
	}

	@Override
	public void openClientVerification() {
		replaceFragment(new ClientVerificationFragment());
	}

	@Override
	public void openClientHomeActivity() {
		registerGCM();
		User user = UserSelfAssist.getUserFromDb(UserPreferences.getUserId(BA.getContext()));
		if(user == null) {
			ToastUtil.display(BA.getContext(),"Ошибка при входе");
			return;
		}

		boolean profileIncomplete = TextUtils.isEmpty(user.getFirstName())
				|| !(user.getCars() != null && user.getCars().size() > 0);

		// New users (password_required) must finish the profile and then set a
		// password before entering the app.
		if (UserPreferences.isPasswordRequired(BA.getContext())) {
			if (profileIncomplete) {
				clearBackStack();
				replaceFragment(new ClientRegisterUserFragment());
			} else {
				openClientCreatePassword();
			}
			return;
		}

		if (profileIncomplete) {
			clearBackStack();
			replaceFragment(new ClientRegisterUserFragment());
		} else {
			finish();
			overridePendingTransition(0, 0);
		}
	}

	private void clearBackStack() {
		for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); ++i) {
			getSupportFragmentManager().popBackStack();
		}
	}

	@Override
	public void openClientWhatsappRegistration(String phone, String email) {
		ClientWhatsappFragment fragment = new ClientWhatsappFragment();
		Bundle args = new Bundle();
		args.putString(ClientWhatsappFragment.ARG_PHONE, phone);
		args.putString(ClientWhatsappFragment.ARG_EMAIL, email);
		fragment.setArguments(args);
		replaceFragment(fragment);
	}

	@Override
	public void openClientPasswordLogin(String phone) {
		ClientPasswordLoginFragment fragment = new ClientPasswordLoginFragment();
		Bundle args = new Bundle();
		args.putString(ClientPasswordLoginFragment.ARG_PHONE, phone);
		fragment.setArguments(args);
		replaceFragment(fragment);
	}

	@Override
	public void openClientResetPassword(String phone) {
		ClientResetPasswordFragment fragment = new ClientResetPasswordFragment();
		Bundle args = new Bundle();
		args.putString(ClientResetPasswordFragment.ARG_PHONE, phone);
		fragment.setArguments(args);
		replaceFragment(fragment);
	}

	@Override
	public void openClientCreatePassword() {
		replaceFragment(new ClientCreatePasswordFragment());
	}

	@Override
	public void openAdminRegistration() {

	}

	@Override
	public void openAdminHomeActivity() {

	}

	public void registerGCM() {
//		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String PUSH_TOKEN = UserPreferences.getPushToken(this);//sharedPreferences.getString(RegistrationIntentService.PUSH_TOKEN, "");
		if (TextUtils.isEmpty(PUSH_TOKEN) && checkPlayServices()) {
//			Intent intent = new Intent(LoginActivity.this, RegistrationIntentService.class);
//			startService(intent);
		}

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
		if (!TextUtils.isEmpty(UserPreferences.getCity(BA.getContext()))) {
			city = UserPreferences.getCity(BA.getContext());
			userLocation.setCity(city);
		}
		BA.getEventBus().post(new UserLocationUpdateEvent(userLocation));
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
				GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				ToastUtil.displayAtTop(this,"This device is not supported.");
			}
			return false;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (getSupportFragmentManager().getBackStackEntryCount() == 0){
			finish();
		}
	}

}