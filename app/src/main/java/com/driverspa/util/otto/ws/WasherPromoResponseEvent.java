package com.driverspa.util.otto.ws;

import java.util.ArrayList;

import com.driverspa.model.WasherPublic;

public class WasherPromoResponseEvent extends WasherBaseResponseEvent {

	public WasherPromoResponseEvent(ArrayList<WasherPublic> washers) {
		super(washers);
	}

}
