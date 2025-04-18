package com.driverspa.util.otto.ws;

import com.driverspa.model.api.response.BooksResponseHolder;
import com.driverspa.model.api.response.NotificationResponseHolder;

public class NotificationResponseEvent {

	NotificationResponseHolder data;
	public NotificationResponseEvent(NotificationResponseHolder data) {
		this.data = data;
	}
   
	public NotificationResponseHolder getData(){
		return this.data;
	}
	
}
