package com.driverspa.util.otto.ws;

import com.driverspa.model.api.response.BaseResponse;
import com.driverspa.model.api.response.BookResponse;
import com.driverspa.model.api.response.BookResponseHolder;

public class BookCancelResponseEvent {

	
	BookResponseHolder data;

	public BookCancelResponseEvent(BookResponseHolder data) {
		this.data = data;
	}

	public BookResponseHolder getData() {
		return data;
	}
}
