package com.driverspa.client.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;
import android.view.animation.Animation;

import com.driverspa.R;
import com.driverspa.listener.OnActionbarTitleChange;

@SuppressLint("NewApi")
public class ClientBaseActivity extends AppCompatActivity implements OnActionbarTitleChange {

    private ProgressDialog pd;
	private final String TAG = "YERZHAN";
	public static final String OPENING_ANIMATION = "OPENING_ANIMATION";

	public void startActivity(Intent intent) {
		super.startActivity(intent);
		boolean openingAnimation = intent.getBooleanExtra(OPENING_ANIMATION, true);
		if( openingAnimation )
			overridePendingTransitionWithCommoOpeningTransition(this);
	}
			
	public void startActivityForResult(Intent intent, int requestCode ) {
		super.startActivityForResult(intent, requestCode);
		boolean openingAnimation = intent.getBooleanExtra(OPENING_ANIMATION, true);
		if( openingAnimation )
			overridePendingTransitionWithCommoOpeningTransition(this);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if( ! (this instanceof ClientHomeActivity) )
			overridePendingTransitionWithCommonCloseTransition();
	}

	/**
	 * Play activity close animation
	 */
	protected void overridePendingTransitionWithCommonCloseTransition() {
		overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
	}

	/**
	 * Play activity open animation
	 */
	public static void overridePendingTransitionWithCommoOpeningTransition(AppCompatActivity activity) {
		activity.overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
	}

	@Override
	public void setTitle(String title) {
		getActionBar().setTitle(title);
	}
	
	protected void startAnimation(View view, Animation anim){
		view.setAnimation(anim);
		anim.start();		
	}

	  /**
		 * Shows wait dialog
		 */
	  void setWaitScreen(boolean set) {
		  if(pd == null) {
			  pd = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
			  pd.setTitle("");
			  pd.setIndeterminate(true);
			  pd.setMessage("Загрузка...");
		  }
		  try {
			  if (set) pd.show();
			  else pd.dismiss();
		  }
		  catch(Exception e){}
	  }

	public void addFragmentContent(int resource,Fragment fragment){
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.add(resource, fragment);
		fragmentTransaction.commit();
	}

	public void replaceFragment(Fragment fragment){
		String backStateName = fragment.getClass().getName();
		FragmentManager manager = getSupportFragmentManager();
		boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);
		if (!fragmentPopped){ //fragment not in back stack, create it.
			FragmentTransaction ft = manager.beginTransaction();
			ft.replace(R.id.fragment_container, fragment);
			ft.addToBackStack(backStateName);
			ft.commit();
		}
	}
}
