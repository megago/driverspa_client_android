package com.driverspa.util.otto.ws;

import com.driverspa.model.api.request.AuthClientRegistrationRequest;

public class AuthClientRegistrationRequestEvent {

	private AuthClientRegistrationRequest authLoginRequest;
	
	public AuthClientRegistrationRequestEvent(AuthClientRegistrationRequest authLoginRequest) {
		this.authLoginRequest = authLoginRequest;
	}
	
	public AuthClientRegistrationRequest getAuthLoginRequest() {
		return authLoginRequest;
	}
	
}
