package com.driverspa.util.otto;

public class NearSearchEvent {
	String query;
	public NearSearchEvent(String query){
		this.query = query;
	}

	public String getQuery(){
		return this.query;
	}
}
