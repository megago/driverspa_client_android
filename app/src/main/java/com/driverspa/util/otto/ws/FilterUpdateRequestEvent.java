package com.driverspa.util.otto.ws;

import com.driverspa.model.SearchFilter;

public class FilterUpdateRequestEvent {

    SearchFilter searchFilter;

	public FilterUpdateRequestEvent(SearchFilter searchFilter) {
		this.searchFilter = searchFilter;
	}

	public SearchFilter getSearchFilter(){
		return searchFilter;
	}
	
}
