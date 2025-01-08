package com.driverspa.model.api.response;

import com.google.gson.annotations.SerializedName;

public class WasherTimeTableResponseHolder  extends BaseResponseHolder {

	@SerializedName("data")
	private WasherTimeTableResponse response;

	public WasherTimeTableResponse getResponse() {
		return response;
	}


}
