package com.driverspa.util.otto.ws;

import com.driverspa.model.api.response.FareRequestResponseHolder;

public class FareRequestResponseEvent {

	FareRequestResponseHolder data;

	public FareRequestResponseEvent(FareRequestResponseHolder data) {
		this.data = data;
	}

   public FareRequestResponseHolder getData(){
	   return this.data;
   }
   
}
