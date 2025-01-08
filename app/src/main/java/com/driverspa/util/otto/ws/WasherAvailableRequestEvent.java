package com.driverspa.util.otto.ws;

import com.driverspa.model.SearchFilter;


public class WasherAvailableRequestEvent {

	private SearchFilter searchFilter;
	
	public WasherAvailableRequestEvent() {
		this(new SearchFilter());
	}
	
	public WasherAvailableRequestEvent(SearchFilter searchFilter) {
		this.searchFilter = searchFilter;
	}
	
	public SearchFilter getSearchFilter() {
		return searchFilter;
	}

}
