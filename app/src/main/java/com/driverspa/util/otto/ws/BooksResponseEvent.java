package com.driverspa.util.otto.ws;

import com.driverspa.model.api.response.BooksResponseHolder;

public class BooksResponseEvent {

	BooksResponseHolder data;
	public BooksResponseEvent(BooksResponseHolder data) {
		this.data = data;
	}
   
	public BooksResponseHolder getData(){
		return this.data;
	}
	
}
