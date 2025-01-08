package com.driverspa.util.otto.ws;

public class FavouriteWasherRequestEvent {
     private String washerId;
     
	 public FavouriteWasherRequestEvent(String washerId){
    	 this.washerId = washerId;
     }
	
    public String getWasherId() {
		return washerId;
	}

	public void setWasherId(String washerId) {
		this.washerId = washerId;
	}

}
