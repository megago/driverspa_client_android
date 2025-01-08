package com.driverspa.util.otto.ws;

import java.util.ArrayList;

import com.driverspa.model.BookInfo;

public abstract class BooksBaseResponseEvent {

	private ArrayList<BookInfo> books;
	private int count;
	
	public BooksBaseResponseEvent(ArrayList<BookInfo> books) {
		this.books = books;
	}
	
	public ArrayList<BookInfo> getBooks() {
		return books;
	}

	public int getCount() {
		return count;
	}
	
}
