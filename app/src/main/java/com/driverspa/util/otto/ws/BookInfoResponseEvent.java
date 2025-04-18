package com.driverspa.util.otto.ws;

import com.driverspa.model.BookInfo;

public class BookInfoResponseEvent {

	BookInfo response;

	public BookInfoResponseEvent(BookInfo bookResponse) {
		this.response = bookResponse;
	}

	public BookInfo getResponse() {
		return response;
	}

	public void setBookResponse(BookInfo response) {
		this.response = response;
	}

}
