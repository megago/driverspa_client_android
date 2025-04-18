package com.driverspa.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.splunk.mint.Mint;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.assist.UserSelfAssist;
import com.driverspa.client.activity.ClientHomeActivity;
import com.driverspa.model.CurrentGeoPosition;
import com.driverspa.model.User;
import com.driverspa.model.UserLocation;
import com.driverspa.util.ToastUtil;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.ws.AboutUSRequestEvent;
import com.driverspa.util.otto.ws.InitRequestEvent;
import com.driverspa.util.otto.ws.InitResponseEvent;
import com.driverspa.util.otto.ws.UserGetSelfRequestEvent;
import com.driverspa.util.otto.ws.UserLocationUpdateEvent;

/**
 * @author Yerzhan
 *
 */

public class SplashActivity extends BaseActivity implements AnimationListener{
	@InjectView (R.id.noConnectionLayout)
	View noConnectionLayout;
	@InjectView (R.id.logo)
	View logo;
	@InjectView (R.id.circle1)
	ImageView circle1;
	@InjectView (R.id.circle2)
	ImageView circle2;
	@InjectView (R.id.circle3)
	ImageView circle3;

	Animation animCircle1;
	Animation animCircle2;
	Animation animCircle3;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		Mint.initAndStartSession(this.getApplication(), "b054ddc0");
		setContentView(R.layout.activity_splash);
		ButterKnife.bind(this);

		animCircle1 = AnimationUtils.loadAnimation(this,R.anim.circle1_anim);
		animCircle2 = AnimationUtils.loadAnimation(this,R.anim.circle2_anim);
		animCircle3 = AnimationUtils.loadAnimation(this,R.anim.circle3_anim);

		circle1.startAnimation(animCircle1);
		circle2.startAnimation(animCircle2);
		circle3.startAnimation(animCircle3);

		boolean isLoggedIn = UserPreferences.isUserLoggedIn(this);
		User user = UserSelfAssist.getUserFromDb(UserPreferences.getUserId(BA.getContext()));

		if(isLoggedIn) {
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

			if(user == null) {
				//If database was recreated then ask for user data again
				String userId = UserPreferences.getUserId(BA.getContext());
				if(!TextUtils.isEmpty(userId)) {
					BA.getEventBus().post(new UserGetSelfRequestEvent(userId));
				}
			}
		}
	}

	@Override
	public void onAnimationEnd(Animation animation) {
	}
	
	@Override
	protected void onDestroy() {
		BA.getEventBus().register(this);
		super.onDestroy();
	}	
	
	@Override
	public void onAnimationStart(Animation animation) {}
	@Override
	public void onAnimationRepeat(Animation animation) {}
	
    private void goToMainActivity(){
    	finish();
		boolean splashShown = UserPreferences.isInfoPageShown(this);
		if(splashShown) {
			boolean isLoggedIn = UserPreferences.isAdminLoggedIn(this);
			//control for old washme applications user is logged in
			if(isLoggedIn){
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
				return;
			}
			openClientHomeActivity();
		}
		else {
			finish();
			overridePendingTransition(0,0);
			startActivity(new Intent(this, SplashInfoActivity.class).putExtra(OPENING_ANIMATION, false).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
		}
   }

	@Override
	protected void onResume() {
		super.onResume();
		BA.getEventBus().register(this);
		BA.getEventBus().post(new InitRequestEvent(UserPreferences.getUserLocale(this)));
		BA.getEventBus().post(new AboutUSRequestEvent());
	}
	@Override
	protected void onPause() {
		super.onPause();
		try {
			BA.getEventBus().unregister(this);
		}
		catch(Exception e){}
	}

	@Subscribe
  public void onReferenceReceived(InitResponseEvent event){
	  if(event.getReference() != null){
		  BA.setReference(event.getReference());
		  goToMainActivity();
	  }
	  else{
		  noConnectionLayout.setVisibility(View.VISIBLE);
		  logo.setVisibility(View.GONE);
		  ToastUtil.display(this, "Ошибка при инициализации, проверьте соединение");
	  }	  
  }
  
  @OnClick (R.id.repeatConnect)
  public void onRepeatConnect(){
	  logo.setVisibility(View.VISIBLE);
	  noConnectionLayout.setVisibility(View.GONE);
	  BA.getEventBus().post(new InitRequestEvent(UserPreferences.getUserLocale(this)));
	  BA.getEventBus().post(new AboutUSRequestEvent());
  }

	public void openClientHomeActivity() {
			 finish();
			 overridePendingTransition(0,0);
			 startActivity(new Intent(this, ClientHomeActivity.class).putExtra(OPENING_ANIMATION, false));
	}

	public void openMainActivity() {
		startActivity(new Intent(this, MainActivity.class).putExtra(OPENING_ANIMATION, false));
	}

	public void openAdminMarket() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://details?id=com.driverspabox"));
		startActivity(intent);
	}
}
