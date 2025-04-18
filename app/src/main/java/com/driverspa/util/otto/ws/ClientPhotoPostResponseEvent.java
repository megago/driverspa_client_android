package com.driverspa.util.otto.ws;

import com.driverspa.model.api.response.BaseResponseHolder;

public class ClientPhotoPostResponseEvent {
	BaseResponseHolder response;

	public ClientPhotoPostResponseEvent(BaseResponseHolder response) {
		this.response = response;
	}

	public BaseResponseHolder getResponse(){
		return this.response;
	}
}
