package com.driverspa.model.api.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import com.driverspa.model.BookInfo;
import com.driverspa.model.Notification;

public class NotificationResponse extends BaseResponse {

	@SerializedName("objects")
	private ArrayList<Notification> result;
	private ListMetaDataResponse meta;

	public ArrayList<Notification> getResult() {
		return result;
	}

	public ListMetaDataResponse getMeta() {
		return meta;
	}
		
}
