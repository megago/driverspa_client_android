package com.driverspa.model.api.request;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import com.driverspa.model.Washer.BoxSettings;

public class InitialWasherCreateRequest {

	public InitialWasherCreateRequest(){
		
	}
	
	public InitialWasherCreateRequest(String name, ArrayList<Double> lonLat, String city){
		this.name = name;
		this.lonLat = lonLat;
		this.city = city;		
	}
	
	String name;	
	@SerializedName("lonlat")
	ArrayList<Double> lonLat;
	String city;
	@SerializedName("box_settings")
	private ArrayList<BoxSettings> boxSettings;


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<Double> getLonLat() {
		return lonLat;
	}
	public void setLonLat(ArrayList<Double> lonLat) {
		this.lonLat = lonLat;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public ArrayList<BoxSettings> getBoxSettings() {
		return boxSettings;
	}
	public void setBoxSettings(ArrayList<BoxSettings> boxSettings) {
		this.boxSettings = boxSettings;
	}

}
