package com.driverspa.util.otto.ws;

import com.driverspa.model.api.response.UserResponseHolder;


public class UserUpdateSelfResponseEvent {

	private UserResponseHolder data;
	
	public UserUpdateSelfResponseEvent (UserResponseHolder data) {
		this.data = data;
	}
	
	public UserResponseHolder getData() {
		return data;
	}
	
}
