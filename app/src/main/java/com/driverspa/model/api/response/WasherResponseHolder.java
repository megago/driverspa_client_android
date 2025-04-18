package com.driverspa.model.api.response;

import com.google.gson.annotations.SerializedName;

import com.driverspa.model.Washer;

public class WasherResponseHolder extends BaseResponseHolder {

	@SerializedName("data")
	private Washer response;

	public WasherResponse getResponse() {
		return new WasherResponse(response);
	}
	
}
