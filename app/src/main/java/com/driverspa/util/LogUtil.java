package com.driverspa.util;

import android.util.Log;

import com.driverspa.BaseApplication;


public class LogUtil {

	public static void v(String TAG, String str) {
		if( BaseApplication.DEBUG )
			Log.v(TAG, str);
	}
	
	public static void d(String TAG, String str) {
		if( BaseApplication.DEBUG )
			Log.d(TAG, str);
	}
	
	public static void i(String TAG, String str) {
		if( BaseApplication.DEBUG )
			Log.i(TAG, str);
	}
	
	public static void w(String TAG, String str) {
		if( BaseApplication.DEBUG )
			Log.w(TAG, str);
	}
	
	public static void e(String TAG, String str) {
		if( BaseApplication.DEBUG )
			Log.e(TAG, str);
	}
	
}
