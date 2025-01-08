package com.driverspa.model.api.response;

import com.google.gson.annotations.SerializedName;

public class BooksResponseHolder extends BaseResponseHolder {

	@SerializedName("data")
	private BooksResponse response;

	public BooksResponse getResponse() {
		return response;
	}
	
}
