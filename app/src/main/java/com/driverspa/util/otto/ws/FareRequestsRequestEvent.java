package com.driverspa.util.otto.ws;

import com.driverspa.model.SearchFilter;


public class FareRequestsRequestEvent {

	private String status;


	public FareRequestsRequestEvent(String status) {
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}

}
