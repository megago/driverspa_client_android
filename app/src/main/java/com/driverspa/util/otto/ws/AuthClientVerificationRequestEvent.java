package com.driverspa.util.otto.ws;

import com.driverspa.model.api.request.AuthVerificationRequest;

public class AuthClientVerificationRequestEvent {

	private AuthVerificationRequest authVerifyRequest;
	
	public AuthClientVerificationRequestEvent(AuthVerificationRequest authVerifyRequest) {
		this.authVerifyRequest = authVerifyRequest;
	}
	
	public AuthVerificationRequest getAuthVerifyRequest() {
		return authVerifyRequest;
	}
	
}
