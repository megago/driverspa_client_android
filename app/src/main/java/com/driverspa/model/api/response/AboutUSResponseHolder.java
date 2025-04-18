package com.driverspa.model.api.response;

import com.google.gson.annotations.SerializedName;

import com.driverspa.model.AboutUs;

public class AboutUSResponseHolder extends BaseResponseHolder {

	@SerializedName("data")
	private AboutUs response;

	public AboutUs getResponse() {
		return response;
	}
}
