package com.driverspa.util.otto.ws;

import com.driverspa.model.api.response.BaseResponseHolder;

public class UserPhotoChangeResponseEvent {
	
	private BaseResponseHolder data;
	
	public UserPhotoChangeResponseEvent (BaseResponseHolder data) {
		this.data = data;
	}
	
	public BaseResponseHolder getData() {
		return data;
	}

}
