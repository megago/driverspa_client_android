package com.driverspa.model.api.response;

import com.google.gson.annotations.SerializedName;

public class BaseAuthResponse extends BaseResponse {

	private String id;
	
	@SerializedName("mobile")
	private String phone;
	private String token;
	
	public String getId() {
		return id;
	}
	
	public String getPhone() {
		return phone;
	}

	public String getToken() {
		return token;
	}
	
}
