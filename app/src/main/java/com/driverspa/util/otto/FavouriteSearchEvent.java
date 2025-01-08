package com.driverspa.util.otto;

public class FavouriteSearchEvent {
	String query;
	public FavouriteSearchEvent(String query){
		this.query = query;
	}

	public String getQuery(){
		return this.query;
	}
}
