package com.driverspa.util.otto.ws;

import com.driverspa.model.UserLocation;


public class UserLocationUpdateEvent {

	private UserLocation userLocation;


	public UserLocationUpdateEvent(UserLocation userLocation) {
		this.userLocation = userLocation;
	}

	public UserLocation getUserLocation() {
		return userLocation;
	}

	public void setUserLocation(UserLocation userLocation) {
		this.userLocation = userLocation;
	}
}
