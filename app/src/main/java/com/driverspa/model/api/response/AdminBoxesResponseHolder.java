package com.driverspa.model.api.response;

import com.google.gson.annotations.SerializedName;

public class AdminBoxesResponseHolder extends BaseResponse {
	
	@SerializedName("data")
	private AdminBoxesResponse response;

	public AdminBoxesResponse getResponse() {
		return response;
	}

	public void setResponse(AdminBoxesResponse response) {
		this.response = response;
	}	 
}
