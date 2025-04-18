package com.driverspa.model.api.response;

import com.google.gson.annotations.SerializedName;

import com.driverspa.Reference;

public class ReferenceResponseHolder extends BaseResponseHolder {

	@SerializedName("data")
	private Reference response;

	public Reference getResponse() {
		return response;
	}
	
}
