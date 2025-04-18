package com.driverspa.model.api.response;

import com.google.gson.annotations.SerializedName;

public class BaseAuthResponseHolder extends BaseResponseHolder {
    
	@SerializedName("data")
	private BaseAuthResponse response;

	public BaseAuthResponse getResponse() {
		return response;
	}
	
}
