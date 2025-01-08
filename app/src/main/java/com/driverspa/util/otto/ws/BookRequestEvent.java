package com.driverspa.util.otto.ws;

import com.driverspa.model.api.request.BookingRequest;

public class BookRequestEvent {     
     
     private BookingRequest bookingRequest;
     
     public BookRequestEvent(BookingRequest bookingRequest){
    	 this.bookingRequest = bookingRequest; 
     }
	 public BookingRequest getBookingRequest() {
		return bookingRequest;
	}

	public void setBookingRequest(BookingRequest bookingRequest) {
		this.bookingRequest = bookingRequest;
	}

}
