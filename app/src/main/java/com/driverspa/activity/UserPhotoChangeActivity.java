package com.driverspa.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.splunk.mint.Mint;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import com.driverspa.R;
import com.driverspa.fragment.UserPhotoChangeFragment;
import com.driverspa.model.PhotoParcelable;
import com.driverspa.util.ActivityForResult;

public class UserPhotoChangeActivity extends BaseActivity implements UserPhotoChangeFragment.ActivityActions {

	TextView title;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Mint.initAndStartSession(this.getApplication(), "b054ddc0");
		ButterKnife.inject(this);
		setContentView(R.layout.activity_user_photo);
		Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		title = (TextView) mToolbar.findViewById(R.id.action_bar_title);
		title.setText("Выберите фото");
		if( savedInstanceState == null ) {
			String userId = getIntent().getStringExtra(UserPhotoChangeFragment.USER_ID);
			ArrayList<PhotoParcelable> photos = getIntent().getParcelableArrayListExtra(UserPhotoChangeFragment.USER_PHOTOS);
			Fragment fragment = UserPhotoChangeFragment.newInstance(userId, photos);
        	getFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
		}
		
	}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            	setResult(RESULT_OK, new Intent());
                finish();	                
                overridePendingTransitionWithCommonCloseTransition();
                break;          
        }
        return true;
    }
	
	public static Intent newIntent(Context context, String userId, ArrayList<PhotoParcelable> photos) {
		Intent intent = new Intent(context, UserPhotoChangeActivity.class);
		intent.putExtra(UserPhotoChangeFragment.USER_ID, userId);
		intent.putParcelableArrayListExtra(UserPhotoChangeFragment.USER_PHOTOS, photos);
		return intent;
	}

//
//	/**
//	 * User has changed photo. Use user info in result and finish activity
//	 */
	@Override
	public void finishAfterUpdate() {
		Intent intent = new Intent();
		setResult(ActivityForResult.PHOTO_CHANGED_YES, intent);
		finish();
	}
	
}
