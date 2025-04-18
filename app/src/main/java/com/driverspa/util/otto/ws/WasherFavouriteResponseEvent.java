package com.driverspa.util.otto.ws;

import java.util.ArrayList;

import com.driverspa.model.WasherPublic;

public class WasherFavouriteResponseEvent extends WasherBaseResponseEvent {

	public WasherFavouriteResponseEvent(ArrayList<WasherPublic> washers) {
		super(washers);
	}

}
