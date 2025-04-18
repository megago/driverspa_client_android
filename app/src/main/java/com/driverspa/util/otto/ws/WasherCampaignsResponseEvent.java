package com.driverspa.util.otto.ws;

import com.driverspa.model.api.response.WashersResponseHolder;


public class WasherCampaignsResponseEvent {

	WashersResponseHolder data;
	public WasherCampaignsResponseEvent(WashersResponseHolder data) {
		this.data = data;
//		super(washers);
	}

	public WashersResponseHolder getData() {
		return data;
	}
}
