package com.driverspa.util.otto.ws;

import com.driverspa.model.SearchFilter;


public class WashersNearRequestEvent {

	private SearchFilter searchFilter;
	
	public WashersNearRequestEvent() {
		this(new SearchFilter());
	}

	public WashersNearRequestEvent( SearchFilter searchFilter) {
		this.searchFilter = searchFilter;
	}

	public SearchFilter getSearchFilter() {
		return searchFilter;
	}
	
}
