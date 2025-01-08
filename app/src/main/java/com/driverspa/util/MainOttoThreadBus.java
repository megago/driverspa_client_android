package com.driverspa.util;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class MainOttoThreadBus extends Bus {
	private final Handler mainThread = new Handler(Looper.getMainLooper());

	public MainOttoThreadBus(ThreadEnforcer thread) {
		super(thread);
	}

	@Override
	public void post(final Object event) {
		if (Looper.myLooper() == Looper.getMainLooper()) {
			super.post(event);
		} else {
			mainThread.post(new Runnable() {
				@Override
				public void run() {
					MainOttoThreadBus.super.post(event);
				}
			});
		}
	}
}