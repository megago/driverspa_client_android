package com.driverspa.util.otto.ws;

import com.driverspa.model.SearchFilter;

public class FilterResponseEvent {

	private SearchFilter searchFilter;

	public FilterResponseEvent(SearchFilter searchFilter) {
		this.searchFilter = searchFilter;
	}
	
	public SearchFilter getSearchFilter() {
		return searchFilter;
	}
	
}
