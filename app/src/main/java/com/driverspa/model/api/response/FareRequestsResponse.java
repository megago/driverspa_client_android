package com.driverspa.model.api.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import com.driverspa.model.BookInfo;
import com.driverspa.model.FareRequest;

public class FareRequestsResponse extends BaseResponse {

	@SerializedName("objects")
	private ArrayList<FareRequest> result;
	private ListMetaDataResponse meta;
	
	public ArrayList<FareRequest> getFareRequests() {
		return result;
	}		
		
	public ListMetaDataResponse getMeta() {
		return meta;
	}
		
}
