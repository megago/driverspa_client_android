package com.driverspa.util.otto.ws;

import com.driverspa.model.api.response.WasherTimeTableResponse;

public class WasherTimeTableResponseEvent {
   
	WasherTimeTableResponse washerTimeTable;
	
	public WasherTimeTableResponseEvent(WasherTimeTableResponse washerTimeTable){
		this.washerTimeTable = washerTimeTable;
	}
	
	public WasherTimeTableResponse getWasherTimeTable() {
		return washerTimeTable;
	}

}
