package com.driverspa.model;

import com.google.gson.annotations.SerializedName;

public enum CampaignType {

	@SerializedName("online")
	Online("online"),

	@SerializedName("offline")
	Offline("offline"),

	@SerializedName("both")
	Both("both"),

	Unknown("unknown");

	public static CampaignType toEnum (String enumString) {
		try {
			return valueOf(enumString);
		} catch (Exception ex) {
			// For error cases
			return Unknown;
		}
	}

	private final String id;
	CampaignType(String id) { this.id = id; }
	public String getValue() { return id; }

	public String toString() {
		return getValue();
	}
	
}
