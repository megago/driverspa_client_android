package com.driverspa.util.otto.ws;

public class WasherTimeTableRequestEvent {
     private String washerId;
     private String day;
     
	 public WasherTimeTableRequestEvent(String washerId, String day){
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
