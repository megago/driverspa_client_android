package com.driverspa.util.otto.ws;

import com.driverspa.model.SearchFilter;


public class WashersNearMapRequestEvent {

	private double latitide;
	private double longitude;
	private SearchFilter searchFilter;

	public WashersNearMapRequestEvent(double latitide, double longitude) {
		this(new SearchFilter());
	}

	public WashersNearMapRequestEvent( SearchFilter searchFilter) {
		super();
		this.searchFilter = searchFilter;
	}

	public double getLatitide() {
		return latitide;
	}

	public double getLongitude() {
		return longitude;
	}
	
	public SearchFilter getSearchFilter() {
		return searchFilter;
	}
	
}
