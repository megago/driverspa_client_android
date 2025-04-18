package com.driverspa.util.otto.ws;

import com.driverspa.model.Washer;

public class WasherInfoResponseEvent {
   
	Washer washer;
	
	public WasherInfoResponseEvent(Washer washer){
		this.washer = washer;
	}
	
	public Washer getWasher() {
		return washer;
	}

	public void setWasher(Washer washer) {
		this.washer = washer;
	}

}
