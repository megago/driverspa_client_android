package com.driverspa.model.api.response;

import com.google.gson.annotations.SerializedName;

import com.driverspa.model.FareRequest;

public class FareRequestResponseHolder extends BaseResponseHolder {

	@SerializedName("data")
	private FareRequest data;

	public FareRequest getData() {
		return data;
	}

}
