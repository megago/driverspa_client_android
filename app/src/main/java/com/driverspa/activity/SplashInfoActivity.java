package com.driverspa.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.viewpager.widget.ViewPager;

import com.splunk.mint.Mint;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import com.driverspa.BA;
import com.driverspa.R;
import com.driverspa.adapter.SplashInfoPagerAdapter;
import com.driverspa.client.activity.ClientHomeActivity;
import com.driverspa.fragment.SplashInfoFragment;
import com.driverspa.util.UserPreferences;

/**
 * @author Yerzhan
 *
 */

public class SplashInfoActivity extends BaseActivity implements SplashInfoFragment.ActivityActions{

	@BindView(R.id.pager)
	ViewPager viewPager;
	@BindView(R.id.dot1)
	ImageView dot1;
	@BindView(R.id.dot2)
	ImageView dot2;
	@BindView(R.id.dot3)
	ImageView dot3;
	@BindView(R.id.btnNext)
	Button nextButton;

	SplashInfoPagerAdapter viewPagerAdapter;
	int selectedTab;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		Mint.initAndStartSession(this.getApplication(), "b054ddc0");
		setContentView(R.layout.activity_splash_info);
		BA.getEventBus().register(this);
		ButterKnife.bind(this);

		viewPagerAdapter = new SplashInfoPagerAdapter(getSupportFragmentManager());
		viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				if(position > 2){
					start();
				}
				selectedTab = position;
				setAnketaDots(selectedTab);
			}
		});

		viewPager.setOffscreenPageLimit(4);
		viewPager.setAdapter(viewPagerAdapter);
		viewPager.setCurrentItem(selectedTab);
	}
		

	@Override
	protected void onDestroy() {
		BA.getEventBus().register(this);
		super.onDestroy();
	}

	private void setAnketaDots(int tab){
		if(dot1!=null)
			switch (tab){
				case 0:
					dot1.setImageResource(R.drawable.ic_selected_dot);
					dot2.setImageResource(R.drawable.ic_unselected_dot);
					dot3.setImageResource(R.drawable.ic_unselected_dot);
					nextButton.setText("Далее");
					nextButton.setVisibility(View.GONE);
					break;
				case 1:
					dot1.setImageResource(R.drawable.ic_unselected_dot);
					dot2.setImageResource(R.drawable.ic_selected_dot);
					dot3.setImageResource(R.drawable.ic_unselected_dot);
					nextButton.setText("Далее");
					nextButton.setVisibility(View.GONE);
					break;
				case 2:
					dot1.setImageResource(R.drawable.ic_unselected_dot);
					dot2.setImageResource(R.drawable.ic_unselected_dot);
					dot3.setImageResource(R.drawable.ic_selected_dot);
					nextButton.setText("Начать");
					nextButton.setVisibility(View.VISIBLE);
					break;
			}
	}

	@Override
	public void nextPage() {
		viewPager.setCurrentItem(selectedTab+1);
	}

	@Override
	public void start() {
		UserPreferences.onInfoPageShown(this);
		finish();
		overridePendingTransition(0,0);
		openMainActivity();
	}

	@OnClick(R.id.btnNext)
	public void onButtonNextClicked(){
		if(selectedTab < 2)
			nextPage();
		else
			start();
	}

	public void openClientHomeActivity() {
		startActivity(new Intent(this, ClientHomeActivity.class).putExtra(OPENING_ANIMATION, false));
	}

	public void openMainActivity() {
		startActivity(new Intent(this, MainActivity.class).putExtra(OPENING_ANIMATION, false));
	}
}
