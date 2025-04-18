package com.driverspa;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.squareup.otto.Subscribe;

import com.driverspa.activity.MainActivity;
import com.driverspa.util.Singleton;
import com.driverspa.util.ToastUtil;
import com.driverspa.util.otto.ws.ApiErrorEvent;

public class BA extends BaseApplication {

	@Override
	public void onCreate() {
		super.onCreate();
		getSingleton(); // initialize singleton
		getEventBus().register(this); // register for OTTO events
		initLocale();
	}

	public static Singleton getSingleton() {
		return Singleton.getInstance();
	}

	@Subscribe
	public void onApiError(ApiErrorEvent event) {
		ToastUtil.displayShort(this, "Ошибка соединения");
		event.getRetrofitError().printStackTrace();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public static void restart() {
		final int DELAY = 200; //milliseconds
		Intent mStartActivity = new Intent(getContext(), MainActivity.class);
		mStartActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		int mPendingIntentId = 123456;
		PendingIntent mPendingIntent = PendingIntent.getActivity(getContext(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager mgr = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + DELAY, mPendingIntent);
		System.exit(2);
	}

}
