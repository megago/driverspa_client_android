package com.driverspa.util.otto.ws;

import com.driverspa.model.api.response.FareRequestResponseHolder;

public class FareRequestIncreaseFareResponseEvent {

	FareRequestResponseHolder data;

	public FareRequestIncreaseFareResponseEvent(FareRequestResponseHolder data) {
		this.data = data;
	}

   public FareRequestResponseHolder getData(){
	   return this.data;
   }
   
}
