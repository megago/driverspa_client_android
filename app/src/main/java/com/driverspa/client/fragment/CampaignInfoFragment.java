package com.driverspa.client.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.squareup.otto.Subscribe;
import com.todddavies.components.progressbar.ProgressWheel;
import java.util.Date;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.driverspa.BA;

import com.driverspa.R;

import com.driverspa.model.Campaign;
import com.driverspa.model.CampaignType;
import com.driverspa.model.Washer;
import com.driverspa.util.Constants;
import com.driverspa.util.Functions;
import com.driverspa.util.UserPreferences;
import com.driverspa.util.otto.ws.WasherInfoRequestEvent;
import com.driverspa.util.otto.ws.WasherInfoResponseEvent;

public class CampaignInfoFragment extends ClientBaseFragment{

	public static final int MAX_SECS_TO_REFRESH = 10;

	public static CampaignInfoFragment newInstance(String washerId, String washer){
		CampaignInfoFragment fragment = new CampaignInfoFragment();
		Bundle b = new Bundle();
		b.putString(Constants.EXTRA_WASHER_ID,washerId);
		b.putString(Constants.WASHER_DATA,washer);
		fragment.setArguments(b);
		return fragment;
	}

    public interface ActivityActions {
		public void noCampaign();
		public void startBooking(Washer washer);
		public void login();
	}

	private ActivityActions activityActions;

    @InjectView(R.id.description)
    TextView description;
    @InjectView(R.id.endDate)
    TextView endDate;
    @InjectView(R.id.discount)
    TextView discount;
    @InjectView(R.id.campaignType)
    TextView campaignType;
    Campaign campaign;
    Washer washer;

	boolean loading = false;
	private Handler customHandler = new Handler();

	@InjectView(R.id.months)
	TextView months;
	@InjectView(R.id.months_label)
	TextView monthsLabel;
	@InjectView(R.id.days)
	TextView days;
	@InjectView(R.id.days_label)
	TextView daysLabel;
	@InjectView(R.id.hours)
	TextView hours;
	@InjectView(R.id.hours_label)
	TextView hoursLabel;
	@InjectView(R.id.minutes)
	TextView minutes;
	@InjectView(R.id.minutes_label)
	TextView minutesLabel;
	@InjectView(R.id.seconds)
	TextView seconds;
	@InjectView(R.id.seconds_label)
	TextView secondsLabel;
	Long lastRemainingSeconds;
	@InjectView(R.id.remaining_time_unknown)
	TextView timeUnknown;
	@InjectView(R.id.times_layout)
	View timesLayout;
	@InjectView(R.id.mon_days_layout)
	View monthsDaysLayout;

	String[] daysArr;
	String[] hoursArr;
	String[] minutesArr;
	String[] secondsArr;
	boolean runned;
	int progress = 0;
	int currentProgress;
	@InjectView(R.id.progress_wheel)
	ProgressWheel progressWheel;
	TextView titleView;
	String washerId;

	Thread wheelInitialAnimationThread;

	private Runnable updateTimerThread = new Runnable() {
		public void run() {
			customHandler.postDelayed(this, 1000);
			if(lastRemainingSeconds!=null) {
				lastRemainingSeconds = lastRemainingSeconds - 1;
				//Refresh data from server every MAX_SECS_TO_REFRESH
				calculateRemainingTime(lastRemainingSeconds);
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_campaign_info, container, false);
	}
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
		
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		ButterKnife.bind(this, view);
		Toolbar mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_actionbar);
		titleView = (TextView) mToolbar.findViewById(R.id.action_bar_title);
		titleView.setText("");

		daysArr = getResources().getStringArray(R.array.day);
		hoursArr = getResources().getStringArray(R.array.hour);
		minutesArr = getResources().getStringArray(R.array.minute);
		secondsArr = getResources().getStringArray(R.array.second);
		wheelInitialAnimationThread = new Thread(r);

		washerId = getArguments().getString(Constants.EXTRA_WASHER_ID,"");
		String washerStr = getArguments().getString(Constants.WASHER_DATA,"");
		if(!TextUtils.isEmpty(washerStr)){
			washer = Washer.deserialize(washerStr);
			campaign = washer.getActiveCampaign();
			BA.getEventBus().post(new WasherInfoRequestEvent(washerId));
			setData(campaign);
		}
		else{
			setWaitScreen(true);
			BA.getEventBus().post(new WasherInfoRequestEvent(washerId));
		}
	}

	@Subscribe
	public void onWasherInfoReceived(WasherInfoResponseEvent event) {
		setWaitScreen(false);
		if (event.getWasher() != null) {
			washer = event.getWasher();
            campaign = event.getWasher().getActiveCampaign();
			setData(campaign);
		}
	}
	private void setData(Campaign campaign){
		if(campaign != null) {
			titleView.setText(washer.getName().toLowerCase().contains("автомойка")?washer.getName():"Автомойка "+washer.getName());
//			titleView.setCompoundDrawablePadding((int)Functions.dipToPixels(getActivity(),5f));
//			titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_discount_list, 0);
			if (campaign.getEndTime() != null) {
				Date utcDate = new Date(campaign.getTs() * 1000);
				Date utcEndDate = new Date(campaign.getEndTime() * 1000);
				lastRemainingSeconds = (utcEndDate.getTime() - (new Date()).getTime()) / 1000;
				if (lastRemainingSeconds < 0)
					activityActions.noCampaign();
				long totalSecs = (utcEndDate.getTime() - utcDate.getTime()) / 1000;
				double doubleProgress = ((double) (totalSecs - lastRemainingSeconds) / (double) totalSecs) * 360;
				currentProgress = (int) doubleProgress;
				if (!runned)
					wheelInitialAnimationThread.start();
				else
					incrementProgressWheel(currentProgress);
				timeUnknown.setVisibility(View.GONE);
				timesLayout.setVisibility(View.VISIBLE);
				monthsDaysLayout.setVisibility(View.VISIBLE);
			} else {
				timeUnknown.setVisibility(View.VISIBLE);
				timesLayout.setVisibility(View.GONE);
				monthsDaysLayout.setVisibility(View.GONE);
			}

			description.setText(campaign.getDescription());
			discount.setText("-"+campaign.getCampaignDiscount() + "%");
			if (campaign.getCampaignType() == CampaignType.Both) {
				campaignType.setText("Распространяется на все виды мойки (Онлайн бронирование, живая очередь)");
			} else if (campaign.getCampaignType() == CampaignType.Online) {
				campaignType.setText("Распространяется только на онлайн бронирования");
			} else if (campaign.getCampaignType() == CampaignType.Offline) {
				campaignType.setText("Распространяется только на живую очередь");
			}
		}			else{
			activityActions.noCampaign();
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		if(customHandler != null && updateTimerThread != null)
			customHandler.postDelayed(updateTimerThread, 0);
		BA.getEventBus().register(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		customHandler.removeCallbacks(updateTimerThread);
		BA.getEventBus().unregister(this);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		activityActions = (ActivityActions) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		activityActions = null;
	}

	private void calculateRemainingTime(Long remainingSeconds) {
		if (monthsDaysLayout.getVisibility() == View.VISIBLE && remainingSeconds > 0) {
			long diff = remainingSeconds;
			long diffSeconds = diff % 60;
			long diffMinutes = (diff / (60)) % 60;
			long diffHours = (diff / (60 * 60)) % 24;
			long diffDays = diff / (24 * 60 * 60);
			long diffMonths = diff / (30 * 24 * 60 * 60);

			months.setText(Long.toString(diffMonths));
			days.setText(String.format("%01d", diffDays));
			hours.setText(String.format("%02d", diffHours));
			minutes.setText(String.format("%02d", diffMinutes));
			seconds.setText(String.format("%02d", diffSeconds));

			daysLabel.setText(Functions.getRussianFineLabel(Long.toString(diffDays), daysArr));
			hoursLabel.setText(Functions.getRussianFineLabel(Long.toString(diffHours), hoursArr));
			minutesLabel.setText(Functions.getRussianFineLabel(Long.toString(diffMinutes), minutesArr));
			secondsLabel.setText(Functions.getRussianFineLabel(Long.toString(diffSeconds), secondsArr));
		}
		else{
			activityActions.noCampaign();
		}
	 }


	private void incrementProgressWheel(int currentProgress) {
		progress = 0;
		progressWheel.resetCount();
		while (progress < currentProgress) {
			progressWheel.incrementProgress();
			progress++;
		}

	}

	final Runnable r = new Runnable() {
		public void run() {
			runned = true;
			while (progress < currentProgress) {
				progressWheel.incrementProgress();
				progress++;
				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};

	public void showOptions(){
		if(washer != null){
			if(washer.getStatus().equals(Constants.APPROVED)) {
				AlertDialog.Builder b = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
				b.setItems(R.array.campaign_info_options, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						switch (i) {
							case 0:
								boolean loggedIn = UserPreferences.isUserLoggedIn(BA.getContext());
								if(loggedIn) {
									activityActions.startBooking(washer);
								}
								else
									showLoginWarning();
								break;
						}
					}
				});
				b.show();
			}
		}
	}

	private void showLoginWarning(){
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
		dialog.setTitle("Необходимо войти");
		dialog.setPositiveButton("Войти", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				activityActions.login();
			}
		});
		dialog.setNegativeButton("Отмена",null);
		dialog.show();
	}

	@OnClick(R.id.btnBookStart)
	public void onButtonBookClicked(){
		boolean loggedIn = UserPreferences.isUserLoggedIn(BA.getContext());
		if(loggedIn) {
			if(washer != null)
				activityActions.startBooking(washer);
		}
		else
			showLoginWarning();

	}
}
