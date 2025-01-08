package com.driverspa.model.api.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import com.driverspa.model.BookInfo;

public class BooksResponse extends BaseResponse {

	@SerializedName("objects")
	private ArrayList<BookInfo> result;
	private ListMetaDataResponse meta;
	
	public ArrayList<BookInfo> getBookInfoList() {
		return result;
	}		
		
	public ListMetaDataResponse getMeta() {
		return meta;
	}
		
}
