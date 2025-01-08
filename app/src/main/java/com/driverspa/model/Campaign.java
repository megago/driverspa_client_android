package com.driverspa.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class Campaign {

	@SerializedName("end_time")
	Long endTime;

	@SerializedName("campaign_type")
	CampaignType campaignType;

	@SerializedName("description")
	String description;
	@SerializedName("ts")
	Long ts;

	@SerializedName("campaign_discount")
	Integer campaignDiscount;


	public CampaignType getCampaignType() {
		return campaignType;
	}

	public void setCampaignType(CampaignType campaignType) {
		this.campaignType = campaignType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public Integer getCampaignDiscount() {
		return campaignDiscount;
	}

	public void setCampaignDiscount(Integer campaignDiscount) {
		this.campaignDiscount = campaignDiscount;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public Long getTs() {
		return ts;
	}

	public void setTs(Long ts) {
		this.ts = ts;
	}

	// GSON
	public String serialize() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	public static Campaign deserialize(String serializedData) {
		Gson gson = new Gson();
		return gson.fromJson(serializedData, Campaign.class);
	}

}
