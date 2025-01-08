package com.driverspa.util.otto.ws;

import com.driverspa.model.api.response.ClientResponseHolder;

public class ClientResponseEvent {

	ClientResponseHolder data;

	public ClientResponseEvent(ClientResponseHolder data) {
		this.data = data;
	}

   public ClientResponseHolder getResult(){
	   return this.data;
   }
   
}
