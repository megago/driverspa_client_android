package com.driverspa.util.otto.ws;

import com.driverspa.model.api.response.AuthAdminVerificationResponseHolder;
import com.driverspa.model.api.response.BaseResponseHolder;


public class AuthAdminVerificationResponseEvent{

   private AuthAdminVerificationResponseHolder authVerificationResponse;

	public AuthAdminVerificationResponseEvent(BaseResponseHolder data){
		authVerificationResponse = new AuthAdminVerificationResponseHolder();
		authVerificationResponse.setMessage(data.getMessage());
		authVerificationResponse.setStatus(data.getStatus());
	}

	public AuthAdminVerificationResponseEvent(AuthAdminVerificationResponseHolder authVerificationResponse) {
		this.authVerificationResponse = authVerificationResponse;
	}
	
	public AuthAdminVerificationResponseHolder getAuthLoginResponse() {
		return authVerificationResponse;
	}
}
