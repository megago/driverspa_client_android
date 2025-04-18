package com.driverspa.util.otto.ws;

import com.driverspa.model.api.request.UserUpdateRequest;

public class UserUpdateSelfRequestEvent {

	private String id;
	private UserUpdateRequest user;
	
	public UserUpdateSelfRequestEvent(String id, UserUpdateRequest user) {
		this.id = id;
		this.user = user;
	}
	
	public String getId() {
		return id;
	}
	
	public UserUpdateRequest getUserRequest(){
		return user;
	}
	
}
