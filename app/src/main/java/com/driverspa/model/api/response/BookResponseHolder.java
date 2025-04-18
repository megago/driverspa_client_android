package com.driverspa.model.api.response;

import com.google.gson.annotations.SerializedName;

public class BookResponseHolder  extends BaseResponseHolder {

	@SerializedName("data")
	private BookResponse response;

	public BookResponse getResponse() {
		return response;
	}

}
