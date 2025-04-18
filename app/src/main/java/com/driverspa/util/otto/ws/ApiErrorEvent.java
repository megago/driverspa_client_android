package com.driverspa.util.otto.ws;

import retrofit.RetrofitError;

public class ApiErrorEvent {

	private RetrofitError error;
	
	public ApiErrorEvent(RetrofitError retrofitError) {
		this.error = retrofitError;
	}
	
	public RetrofitError getRetrofitError() {
		return error;
	}
	
}
