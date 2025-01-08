package com.driverspa.util.otto.ws;

public class FavouriteWasherResponseEvent {
   
	boolean success;
	
	public FavouriteWasherResponseEvent(boolean success){
		this.success = success;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}
