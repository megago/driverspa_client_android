package com.driverspa.util;

import android.content.Context;

import com.driverspa.model.ClientInfo;

public class ClientPreferences extends Preferences {
	
	private static final String CLIENT_PROFILE = "CLIENT_PROFILE";		
	
	public static ClientInfo getClientInfo(Context context){
		return JsonUtil.deserializeClientInfo(getString(context, CLIENT_PROFILE));
	}
	
	public static void setClientInfo(Context context, ClientInfo clientInfo){
		putString(context,CLIENT_PROFILE, JsonUtil.serialize(clientInfo));
	}
}
