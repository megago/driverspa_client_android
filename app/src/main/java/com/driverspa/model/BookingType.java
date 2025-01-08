package com.driverspa.model;

import com.google.gson.annotations.SerializedName;

public enum BookingType {

	@SerializedName("online")
	Online("online"),

	@SerializedName("offline")
	Offline("offline"),

	@SerializedName("hybrid")
	Hybrid("hybrid"),

	Unknown("unknown");

	public static BookingType toEnum (String enumString) {
		try {
			return valueOf(enumString);
		} catch (Exception ex) {
			// For error cases
			return Unknown;
		}
	}

	private final String id;
	BookingType(String id) { this.id = id; }
	public String getValue() { return id; }

	public String toString() {
		return getValue();
	}
	
}
