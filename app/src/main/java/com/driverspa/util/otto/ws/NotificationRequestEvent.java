package com.driverspa.util.otto.ws;

import com.driverspa.model.SearchFilter;


public class NotificationRequestEvent {

	private SearchFilter searchFilter;

	public NotificationRequestEvent() {
		this(new SearchFilter());
	}

	public NotificationRequestEvent(SearchFilter searchFilter) {
		this.searchFilter = searchFilter;
	}
	
	public SearchFilter getSearchFilter() {
		return searchFilter;
	}

}
