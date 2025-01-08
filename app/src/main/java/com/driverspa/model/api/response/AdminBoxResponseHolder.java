package com.driverspa.model.api.response;

import com.google.gson.annotations.SerializedName;

import com.driverspa.model.api.response.AdminBoxesResponse.BoxType;

public class AdminBoxResponseHolder extends BaseResponseHolder {
	
	@SerializedName("data")
	private AdminBoxesResponse.BoxType response;

	public BoxType getResponse() {
		return response;
	}

	public void setResponse(BoxType response) {
		this.response = response;
	}	 
}
