package com.driverspa.model.api.response;

import com.google.gson.annotations.SerializedName;

public class NotificationResponseHolder extends BaseResponseHolder {

	@SerializedName("data")
	private NotificationResponse response;

	public NotificationResponse getResponse() {
		return response;
	}
	
}
