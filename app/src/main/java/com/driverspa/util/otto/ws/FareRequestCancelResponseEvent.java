package com.driverspa.util.otto.ws;

import com.driverspa.model.api.response.FareRequestResponseHolder;

public class FareRequestCancelResponseEvent {

	FareRequestResponseHolder data;

	public FareRequestCancelResponseEvent(FareRequestResponseHolder data) {
		this.data = data;
	}

   public FareRequestResponseHolder getData(){
	   return this.data;
   }
   
}
