package com.driverspa.util.otto.ws;

import com.driverspa.model.User;


public class UserGetSelfResponseEvent {

	private User user;
	
	public UserGetSelfResponseEvent (User user) {
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}
	
}
