package com.driverspa.util.otto.ws;

import com.driverspa.model.api.request.InitialWasherCreateRequest;

public class InitialWasherCreateRequestEvent {

	 InitialWasherCreateRequest request;
	 
	 public InitialWasherCreateRequestEvent (InitialWasherCreateRequest request){
		 this.request = request;
	 }
	 
	 public InitialWasherCreateRequest getRequest(){
		 return this.request;
	 }
}
