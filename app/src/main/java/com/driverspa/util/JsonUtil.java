package com.driverspa.util;

import android.text.TextUtils;

import com.google.gson.Gson;

import com.driverspa.Reference;
import com.driverspa.model.ClientInfo;
import com.driverspa.model.Washer;
import com.driverspa.model.WasherBase;
import com.driverspa.model.api.request.BookingRequest;

public class JsonUtil {

	public static String serialize(Object object) {
		Gson gson = Converters.gsonWithDate();
		return gson.toJson(object);
	}
	
	public static Washer deserializeToWasher(String str) {
		Gson gson = Converters.gsonWithDate();
		return gson.fromJson(str, Washer.class);
	}

	public static WasherBase deserializeToWasherBase(String str) {
		Gson gson = Converters.gsonWithDate();
		return gson.fromJson(str, WasherBase.class);
	}
	
	public static BookingRequest deserializeToBookingRequest(String str) {
		Gson gson = Converters.gsonWithDate();
		return gson.fromJson(str, BookingRequest.class);
	}
	
	public static Reference deserializeReference(String str) {
		Gson gson = Converters.gsonWithDate();
		return gson.fromJson(str, Reference.class);
	}

	public static ClientInfo deserializeClientInfo(String str) {
		Gson gson = Converters.gsonWithDate();
		if(!TextUtils.isEmpty(str))
		  return gson.fromJson(str, ClientInfo.class);
		else
		  return new ClientInfo();
	}
}
