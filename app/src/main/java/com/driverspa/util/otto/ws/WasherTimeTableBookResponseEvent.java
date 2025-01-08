package com.driverspa.util.otto.ws;

import com.driverspa.model.api.response.WasherTimeTableResponse;

public class WasherTimeTableBookResponseEvent {

	WasherTimeTableResponse washerTimeTable;

	public WasherTimeTableBookResponseEvent(WasherTimeTableResponse washerTimeTable){
		this.washerTimeTable = washerTimeTable;
	}
	
	public WasherTimeTableResponse getWasherTimeTable() {
		return washerTimeTable;
	}

}
