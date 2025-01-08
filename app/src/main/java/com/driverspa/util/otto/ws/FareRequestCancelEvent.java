package com.driverspa.util.otto.ws;


import com.driverspa.model.FareRequest;

public class FareRequestCancelEvent {


	public FareRequestCancelEvent(FareRequest fareRequest) {
		this.fareRequest = fareRequest;
	}

	private FareRequest fareRequest;

	public FareRequest getFareRequest() {
		return fareRequest;
	}

	public void setFareRequest(FareRequest fareRequest) {
		this.fareRequest = fareRequest;
	}
}
