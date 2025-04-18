package com.driverspa.util.otto.ws;

import com.driverspa.model.api.response.AuthAdminRegistrationResponseHolder;

public class AuthAdminRegistrationResponseEvent {

   private AuthAdminRegistrationResponseHolder authLoginResponse;
	
	public AuthAdminRegistrationResponseEvent(AuthAdminRegistrationResponseHolder authLoginResponse) {
		this.authLoginResponse = authLoginResponse;
	}
	
	public AuthAdminRegistrationResponseHolder getAuthLoginResponse() {
		return authLoginResponse;
	}
	
}
