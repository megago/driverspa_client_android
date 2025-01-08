package com.driverspa.util.otto.ws;

public class WasherTimeTableBookRequestEvent {
     private String washerId;
     private String day;

	 public WasherTimeTableBookRequestEvent(String washerId, String day){
    	 this.washerId = washerId;
    	 this.day = day;
     }
	
	public String getDay(){
		return day;
	}
	
    public String getWasherId() {
		return washerId;
	}

	public void setWasherId(String washerId) {
		this.washerId = washerId;
	}

}
