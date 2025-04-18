package com.driverspa.util.otto.ws;

import com.driverspa.model.SearchFilter;


public class WasherCampaignsRequestEvent {

	private SearchFilter searchFilter;

	public WasherCampaignsRequestEvent() {
		this(new SearchFilter());
	}

	public WasherCampaignsRequestEvent(SearchFilter searchFilter) {
		this.searchFilter = searchFilter;
	}

	public SearchFilter getSearchFilter() {
		return searchFilter;
	}
	
}
