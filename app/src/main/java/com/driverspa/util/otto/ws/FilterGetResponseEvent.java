package com.driverspa.util.otto.ws;

import com.driverspa.model.SearchFilter;

public class FilterGetResponseEvent {

	private SearchFilter searchFilter;

	public FilterGetResponseEvent(SearchFilter searchFilter) {
		this.searchFilter = searchFilter;
	}
	
	public SearchFilter getSearchFilter() {
		return searchFilter;
	}
	
}
