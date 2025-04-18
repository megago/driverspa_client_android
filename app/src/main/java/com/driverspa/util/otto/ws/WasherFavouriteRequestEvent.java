package com.driverspa.util.otto.ws;

import com.driverspa.model.SearchFilter;


public class WasherFavouriteRequestEvent {

	private SearchFilter searchFilter;
	
	public WasherFavouriteRequestEvent () {
		this(new SearchFilter());
	}
	
	public WasherFavouriteRequestEvent(SearchFilter searchFilter) {
		this.searchFilter = searchFilter;
	}
	
	public SearchFilter getSearchFilter() {
		return searchFilter;
	}
	
}
