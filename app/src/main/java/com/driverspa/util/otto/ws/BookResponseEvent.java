package com.driverspa.util.otto.ws;

import com.driverspa.model.api.response.BookResponseHolder;

public class BookResponseEvent {

	BookResponseHolder data;

	public BookResponseEvent(BookResponseHolder data) {
		this.data = data;
	}

   public BookResponseHolder getResult(){
	   return this.data;
   }
   
}
