package com.driverspa.util.otto.ws;

import com.driverspa.model.api.request.AddReviewRequest;

public class AddReviewRequestEvent {

	private AddReviewRequest request;

	public AddReviewRequestEvent(AddReviewRequest request){
		this.request = request;
	}
	
	public AddReviewRequest getRequest() {
		return request;
	}
}
