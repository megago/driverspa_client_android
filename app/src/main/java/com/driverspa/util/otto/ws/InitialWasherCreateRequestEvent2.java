package com.driverspa.util.otto.ws;

import com.driverspa.model.api.request.InitialWasherCreateRequest;

public class InitialWasherCreateRequestEvent2 {

	 InitialWasherCreateRequest request;

	 public InitialWasherCreateRequestEvent2(InitialWasherCreateRequest request){
		 this.request = request;
	 }
	 
	 public InitialWasherCreateRequest getRequest(){
		 return this.request;
	 }
}
