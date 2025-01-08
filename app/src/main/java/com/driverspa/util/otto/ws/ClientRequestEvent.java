package com.driverspa.util.otto.ws;

import com.driverspa.model.AdminSearchFilter;


public class ClientRequestEvent {

	private AdminSearchFilter searchFilter;

	public ClientRequestEvent() {
		this(new AdminSearchFilter());
	}

	public ClientRequestEvent(AdminSearchFilter searchFilter) {
		this.searchFilter = searchFilter;
	}
	
	public AdminSearchFilter getSearchFilter() {
		return searchFilter;
	}

}
