package com.driverspa.util.otto.ws;

import com.driverspa.model.AdminSearchFilter;


public class ClientsRequestEvent {

	private AdminSearchFilter searchFilter;

	public ClientsRequestEvent() {
		this(new AdminSearchFilter());
	}

	public ClientsRequestEvent(AdminSearchFilter searchFilter) {
		this.searchFilter = searchFilter;
	}
	
	public AdminSearchFilter getSearchFilter() {
		return searchFilter;
	}

}
