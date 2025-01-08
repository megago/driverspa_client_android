package com.driverspa.util.otto;

import android.location.Location;

public class LocationResponseEvent {

	private Location location;
	
	public LocationResponseEvent(Location location) {
		this.location = location;
	}
	
	public Location getLocation() {
		return location;
	}
	
}
