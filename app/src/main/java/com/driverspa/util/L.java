package com.driverspa.util;

import android.util.Log;

import com.driverspa.BaseApplication;


public class L {

	private static String TAG = "YERZHAN";

	public static void v(String str) {
		if( BaseApplication.DEBUG )
			Log.v(TAG, str);
	}
	
	public static void d(String str) {
		if( BaseApplication.DEBUG )
			Log.d(TAG, str);
	}
	
	public static void i(String str) {
		if( BaseApplication.DEBUG )
			Log.i(TAG, str);
	}
	
	public static void w(String str) {
		if( BaseApplication.DEBUG )
			Log.w(TAG, str);
	}
	
	public static void e(String str) {
		if( BaseApplication.DEBUG )
			Log.e(TAG, str);
	}
}
