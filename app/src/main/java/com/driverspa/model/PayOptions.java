package com.driverspa.model;

import com.google.gson.annotations.SerializedName;

public enum PayOptions {

	@SerializedName("cash")
	Cash("cash"),

	@SerializedName("visa")
	Visa("visa"),

	@SerializedName("mastercard")
	Mastercard("mastercard"),

	Unknown("unknown");

	public static PayOptions toEnum (String enumString) {
		try {
			return valueOf(enumString);
		} catch (Exception ex) {
			// For error cases
			return Unknown;
		}
	}

	private final String id;
	PayOptions(String id) { this.id = id; }
	public String getValue() { return id; }

	public String toString() {
		return getValue();
	}
	
}
