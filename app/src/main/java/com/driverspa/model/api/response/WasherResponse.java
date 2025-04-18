package com.driverspa.model.api.response;

import com.driverspa.model.Washer;


public class WasherResponse extends BaseResponse {
	
	private Washer washer;
	
	public WasherResponse(){}
	
	public WasherResponse(Washer washer){
		this.washer = washer;
	}
	
	public Washer getWasher(){
		return washer;
	}
}
