package com.driverspa.util.otto.ws;

public class BookCancelRequestEvent {
     private String bookId;
     
	 public String getBookId() {
		return bookId;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
	}

	public BookCancelRequestEvent(String bookId){
    	 this.bookId = bookId;
     }
	
}
