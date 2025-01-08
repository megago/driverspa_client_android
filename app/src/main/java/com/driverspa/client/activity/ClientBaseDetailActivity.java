package com.driverspa.client.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;

import android.view.View;
import android.view.animation.Animation;

import com.driverspa.R;
import com.driverspa.listener.OnActionbarTitleChange;

@SuppressLint("NewApi")
public class ClientBaseDetailActivity extends Activity implements OnActionbarTitleChange {

	private final String TAG = "YERZHAN";
	public static final String OPENING_ANIMATION = "OPENING_ANIMATION";
	private ProgressDialog pd = null;
	
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		boolean openingAnimation = intent.getBooleanExtra(OPENING_ANIMATION, true);
//		LogUtil.d(TAG, "open activity with animation : " + String.valueOf(openingAnimation) + " " + getClass().getSimpleName());
		if( openingAnimation )
			overridePendingTransitionWithCommoOpeningTransition(this);
	}
	
	public void startActivityForResult(Intent intent, int requestCode ) {
		super.startActivityForResult(intent, requestCode);
		boolean openingAnimation = intent.getBooleanExtra(OPENING_ANIMATION, true);
//		LogUtil.d(TAG, "open activity with animation : " + String.valueOf(openingAnimation) + " " + getClass().getSimpleName());
		if( openingAnimation )
			overridePendingTransitionWithCommoOpeningTransition(this);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();		
//		if( ! (this instanceof MainActivity) )
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
	public static void overridePendingTransitionWithCommoOpeningTransition(ClientBaseDetailActivity baseActivity) {
		baseActivity.overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
	}

	@Override
	public void setTitle(String title) {
		getActionBar().setTitle(title);
	}
	
	protected void startAnimation(View view, Animation anim){
		view.setAnimation(anim);
		anim.start();		
	}

    protected void setWaitScreen(boolean set) {    	
    	if(pd == null) 
    		pd = ProgressDialog.show(this, "","Загрузка", true);
        if(set) pd.show();
        else  pd.dismiss();
     }

	public void addFragmentContent(int resource,Fragment fragment){
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.add(resource, fragment);
		fragmentTransaction.commit();
	}

	public void replaceFragment(Fragment fragment){
		String backStateName = fragment.getClass().getName();
		FragmentManager manager = getFragmentManager();
		boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);
		if (!fragmentPopped){ //fragment not in back stack, create it.
			FragmentTransaction ft = manager.beginTransaction();
			ft.replace(R.id.fragment_container, fragment);
			ft.addToBackStack(backStateName);
			ft.commit();
		}
	}


}
