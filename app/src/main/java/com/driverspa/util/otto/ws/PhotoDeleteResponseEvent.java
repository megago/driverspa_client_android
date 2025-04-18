package com.driverspa.util.otto.ws;

import com.driverspa.model.api.response.BaseResponseHolder;

public class PhotoDeleteResponseEvent {
	BaseResponseHolder response;

	public PhotoDeleteResponseEvent(BaseResponseHolder response) {
		this.response = response;
	}

	public BaseResponseHolder getResponse(){
		return this.response;
	}
}
