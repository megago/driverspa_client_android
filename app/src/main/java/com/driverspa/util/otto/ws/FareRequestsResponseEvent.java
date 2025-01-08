package com.driverspa.util.otto.ws;

import com.driverspa.model.api.response.BooksResponseHolder;
import com.driverspa.model.api.response.FareRequestsResponseHolder;

public class FareRequestsResponseEvent {

	FareRequestsResponseHolder data;
	public FareRequestsResponseEvent(FareRequestsResponseHolder data) {
		this.data = data;
	}
   
	public FareRequestsResponseHolder getData(){
		return this.data;
	}
	
}
