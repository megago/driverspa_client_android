package com.driverspa.model.api.response;

import com.google.gson.annotations.SerializedName;

import com.driverspa.model.User;

public class AuthClientVerificationResponseHolder extends BaseResponseHolder {
	
	@SerializedName("data")
	private User response;

	public User getResponse() {
		return response;
	}

	public void setResponse(User response) {
		this.response = response;
	}

}
