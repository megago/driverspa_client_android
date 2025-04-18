package com.driverspa.util.otto.ws;

public class BookInfoRequestEvent {
     private String bookId;
     
	 public String getBookId() {
		return bookId;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
	}

	public BookInfoRequestEvent(String bookId){
    	 this.bookId = bookId;
     }
	
}
