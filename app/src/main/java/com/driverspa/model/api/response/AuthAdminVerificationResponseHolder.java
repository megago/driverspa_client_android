package com.driverspa.model.api.response;

import com.google.gson.annotations.SerializedName;

import com.driverspa.model.User;

public class AuthAdminVerificationResponseHolder extends BaseResponseHolder {
	
	@SerializedName("data")
	private User response;

	public User getResponse() {
		return response;
	}

}
