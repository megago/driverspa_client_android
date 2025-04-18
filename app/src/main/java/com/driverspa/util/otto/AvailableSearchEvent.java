package com.driverspa.util.otto;

public class AvailableSearchEvent {
	String query;
	public AvailableSearchEvent(String query){
		this.query = query;
	}

	public String getQuery(){
		return this.query;
	}
}
