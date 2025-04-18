package com.driverspa.util.otto.ws;

import com.driverspa.model.api.request.AuthAdminRegistrationRequest;

public class AuthAdminRegistrationRequestEvent {

	private AuthAdminRegistrationRequest authLoginRequest;
	
	public AuthAdminRegistrationRequestEvent(AuthAdminRegistrationRequest authLoginRequest) {
		this.authLoginRequest = authLoginRequest;
	}
	
	public AuthAdminRegistrationRequest getAuthLoginRequest() {
		return authLoginRequest;
	}
	
}
