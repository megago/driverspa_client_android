package com.driverspa.util.otto.ws;

import com.driverspa.model.api.response.FareRequestResponseHolder;

public class FareRequestAcceptFareResponseEvent {

	FareRequestResponseHolder data;

	public FareRequestAcceptFareResponseEvent(FareRequestResponseHolder data) {
		this.data = data;
	}

   public FareRequestResponseHolder getData(){
	   return this.data;
   }
   
}
