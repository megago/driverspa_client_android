package com.driverspa.util.otto.ws;

import com.driverspa.model.SearchFilter;


public class WasherPromoRequestEvent {

	private SearchFilter searchFilter;

	public WasherPromoRequestEvent(SearchFilter searchFilter) {
		this.searchFilter = searchFilter;
	}

	public WasherPromoRequestEvent () {
		this(new SearchFilter());
	}
	
	public SearchFilter getSearchFilter() {
		return searchFilter;
	}
	
}
