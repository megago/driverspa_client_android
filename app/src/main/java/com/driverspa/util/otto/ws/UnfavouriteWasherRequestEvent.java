package com.driverspa.util.otto.ws;

public class UnfavouriteWasherRequestEvent {
     private String washerId;
     
	 public UnfavouriteWasherRequestEvent(String washerId){
    	 this.washerId = washerId;
     }
	
    public String getWasherId() {
		return washerId;
	}

	public void setWasherId(String washerId) {
		this.washerId = washerId;
	}

}
