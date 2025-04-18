package com.driverspa.util.otto.ws;

import com.driverspa.model.SearchFilter;


public class BooksRequestEvent {

	private SearchFilter searchFilter;
	
	public BooksRequestEvent() {
		this(new SearchFilter());
	}
	
	public BooksRequestEvent(SearchFilter searchFilter) {
		this.searchFilter = searchFilter;
	}
	
	public SearchFilter getSearchFilter() {
		return searchFilter;
	}

}
