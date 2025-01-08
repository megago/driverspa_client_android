package com.driverspa.model.api.response;

import com.google.gson.annotations.SerializedName;

public class FareRequestsResponseHolder extends BaseResponseHolder {

	@SerializedName("data")
	private FareRequestsResponse response;

	public FareRequestsResponse getResponse() {
		return response;
	}
	
}
