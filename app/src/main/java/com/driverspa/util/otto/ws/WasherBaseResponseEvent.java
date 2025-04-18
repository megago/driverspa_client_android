package com.driverspa.util.otto.ws;

import java.util.ArrayList;

import com.driverspa.model.WasherPublic;

public abstract class WasherBaseResponseEvent{

	private ArrayList<WasherPublic> washers;
	private int count;
	
	public WasherBaseResponseEvent(ArrayList<WasherPublic> washers) {
		this.washers = washers;
	}
	
	public ArrayList<WasherPublic> getWashers() {
		return washers;
	}

	public int getCount() {
		return count;
	}
	
}
