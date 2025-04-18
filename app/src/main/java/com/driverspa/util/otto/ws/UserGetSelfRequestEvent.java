package com.driverspa.util.otto.ws;

public class UserGetSelfRequestEvent {

	private String id;
	
	public UserGetSelfRequestEvent(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
}
