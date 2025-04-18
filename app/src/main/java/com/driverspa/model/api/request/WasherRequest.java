package com.driverspa.model.api.request;


import com.google.gson.annotations.SerializedName;

public class WasherRequest {
	
    @SerializedName ("id")
	private String washerId;
    
    public WasherRequest(String washerId){
    	this.washerId = washerId;
    }

	public String getWasherId() {
		return washerId;
	}

	public void setWasherId(String washerId) {
		this.washerId = washerId;
	}
    
}
