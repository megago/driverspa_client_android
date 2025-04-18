package com.driverspa.util.otto.ws;

public class WasherInfoRequestEvent {
     private String washerId;
     
	 public WasherInfoRequestEvent(String washerId){
    	 this.washerId = washerId;
     }
	
    public String getWasherId() {
		return washerId;
	}

	public void setWasherId(String washerId) {
		this.washerId = washerId;
	}

}
