package com.driverspa.util.otto.ws;

import com.driverspa.model.api.response.AuthClientRegistrationResponseHolder;


public class AuthClientRegistrationResponseEvent {

   private AuthClientRegistrationResponseHolder authLoginResponse;
	
	public AuthClientRegistrationResponseEvent(AuthClientRegistrationResponseHolder authLoginResponse) {
		this.authLoginResponse = authLoginResponse;
	}
	
	public AuthClientRegistrationResponseHolder getAuthLoginResponse() {
		return authLoginResponse;
	}
	
}
