package com.driverspa.util.otto.ws;

import com.driverspa.model.api.response.AuthClientVerificationResponseHolder;


public class AuthClientVerificationResponseEvent {

   private AuthClientVerificationResponseHolder authVerificationResponse;
	
	public AuthClientVerificationResponseEvent(AuthClientVerificationResponseHolder authVerificationResponse) {
		this.authVerificationResponse = authVerificationResponse;
	}
	
	public AuthClientVerificationResponseHolder getAuthLoginResponse() {
		return authVerificationResponse;
	}
	
}
