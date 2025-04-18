package com.driverspa.util.otto.ws;

public class UnfavouriteWasherResponseEvent {
	boolean success;
	
	public UnfavouriteWasherResponseEvent(boolean success){
		this.success = success;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
   	 
}
