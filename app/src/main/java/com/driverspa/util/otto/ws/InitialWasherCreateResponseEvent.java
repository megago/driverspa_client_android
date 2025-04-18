package com.driverspa.util.otto.ws;

import com.driverspa.model.api.response.WasherResponseHolder;

public class InitialWasherCreateResponseEvent {
	WasherResponseHolder data;
	
	public InitialWasherCreateResponseEvent(WasherResponseHolder data){
	   this.data = data;	
	}
	
	public WasherResponseHolder getData(){
		return data;
	}
	
}
