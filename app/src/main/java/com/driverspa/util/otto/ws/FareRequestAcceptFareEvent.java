package com.driverspa.util.otto.ws;


import com.driverspa.model.FareRequest;

public class FareRequestAcceptFareEvent {


	public FareRequestAcceptFareEvent(FareRequest fareRequest) {
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
