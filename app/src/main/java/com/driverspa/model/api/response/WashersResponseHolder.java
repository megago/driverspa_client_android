package com.driverspa.model.api.response;

import com.google.gson.annotations.SerializedName;

public class WashersResponseHolder extends BaseResponseHolder {

	@SerializedName("data")
	private WashersResponse response;

	public WashersResponse getResponse() {
		return response;
	}
	
}
