package com.driverspa;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
//import android.support.multidex.MultiDex;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.ThreadEnforcer;

import java.util.Locale;

import com.driverspa.model.NotificationType;
import com.driverspa.util.HttpClient;
import com.driverspa.util.JsonUtil;
import com.driverspa.util.MainOttoThreadBus;
import com.driverspa.util.UserPreferences;

public class BaseApplication extends Application {

	public static boolean DEBUG = true;
	private static MainOttoThreadBus eventBus; // otto - for event registration
	private static Context context;
	private static Reference reference;
	private static NotificationType notificationType = NotificationType.NOTHING;
	
	@Override 
	public void onCreate() {
		super.onCreate();
		context = this;
		init();	
	}

	private void init() {
		HttpClient.init(getApplicationContext(), getCacheSize());
//		HttpClient.setPicassoDebugging(DEBUG);		
		eventBus = new MainOttoThreadBus(ThreadEnforcer.MAIN);			
	}

	private int getCacheSize() {
		return getResources().getInteger(R.integer.image_cache_size);
	}
	
	public static Context getContext(){
        return context;
    }
	
	public static MainOttoThreadBus getEventBus() {
        return eventBus;
    }

	public static Reference getReference(){
		return JsonUtil.deserializeReference(UserPreferences.getUserDictionary(context));
	}
	
	public static void setReference(Reference reference){
		BaseApplication.reference = reference;
		UserPreferences.putUserDictionary(context, JsonUtil.serialize(reference));
	}
	
	public static OkHttpClient getHttpClient() {
		return HttpClient.getHttpClient();
	}

	public static void setLocale(String lang){		
		Locale locale = new Locale(Reference.EN);
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		context.getResources().updateConfiguration(config,context.getResources().getDisplayMetrics());
		UserPreferences.onLocaleChange(context, lang);
		
	}
	
	public static void initLocale(){		
		String defaultLocale;
		 if(Locale.getDefault().toString().indexOf(Reference.RU) != -1)
			 defaultLocale = Reference.RU;
		 else if(Locale.getDefault().toString().indexOf(Reference.EN) != -1)
			 defaultLocale = Reference.EN;
		 else if(Locale.getDefault().toString().indexOf(Reference.KZ) != -1)
			 defaultLocale = Reference.KZ;
		 else
			 defaultLocale = Reference.RU;
		 setLocale(Reference.RU);
	}

	@Override
	protected void attachBaseContext(Context context) {
		super.attachBaseContext(context);
//		MultiDex.install(this);
	}

	public static void setNotificationType(NotificationType notificationType) {
		BaseApplication.notificationType = notificationType;
	}
	public static NotificationType getNotificationType() {
		return notificationType;
	}

}