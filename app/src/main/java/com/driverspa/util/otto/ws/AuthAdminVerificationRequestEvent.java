package com.driverspa.util.otto.ws;

import com.driverspa.model.api.request.AuthVerificationRequest;

public class AuthAdminVerificationRequestEvent {

	private AuthVerificationRequest authVerifyRequest;
	
	public AuthAdminVerificationRequestEvent(AuthVerificationRequest authVerifyRequest) {
		this.authVerifyRequest = authVerifyRequest;
	}
	
	public AuthVerificationRequest getAuthVerifyRequest() {
		return authVerifyRequest;
	}
	
}
