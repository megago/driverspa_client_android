package com.driverspa.model.api.response;

import com.google.gson.annotations.SerializedName;

import com.driverspa.model.BookInfo;

public class BookInfoResponseHolder  extends BaseResponseHolder {

	@SerializedName("data")
	private BookInfo response;

	public BookInfo getResponse() {
		return response;
	}

}
