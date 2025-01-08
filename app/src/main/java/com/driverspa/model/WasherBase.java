package com.driverspa.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.driverspa.model.Washer.BoxSettings;
import com.driverspa.model.Washer.Contact;
import com.driverspa.model.Washer.Prices;
import com.driverspa.model.Washer.Schedule;
import com.driverspa.model.api.response.BaseResponse;

public class WasherBase extends BaseResponse  implements Cloneable  {

	public WasherBase() {}

	@DatabaseField
	private String name;

	@SerializedName("contacts")
	private ArrayList<Contact> contacts;

	@DatabaseField
	private String address;

	@SerializedName("lonlat")
	private ArrayList<Double> lonLat;

	@SerializedName("cartypes")
	private ArrayList<Integer> carTypes;

	@SerializedName("services")
	private ArrayList<Integer> services;

	@SerializedName("menu")
	private LinkedHashMap<Integer, ArrayList<Prices>> menu;

	@SerializedName("box_settings")
	private ArrayList<BoxSettings> boxSettings;

	@SerializedName("schedule")
	private Schedule schedule;
    
	@SerializedName("payoptions")
    private ArrayList<String> payOptions;

	@SerializedName("additional_info")
	private ArrayList<String> additionalInfo;
	
	@SerializedName("is_public")
	private boolean isWasherPublic;

	@SerializedName("group_menu")
	private List<GroupMenu> groupMenu;

	@SerializedName("is_user_wanted")
	private Boolean isUserWanted;
	@SerializedName("wants_count")
	private Integer wantsCount;


	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public LinkedHashMap<Integer, ArrayList<Prices>> getMenu() {
		return menu;
	}

	public void setMenu(LinkedHashMap<Integer, ArrayList<Prices>> menu) {
		this.menu = menu;
	}

	public ArrayList<Integer> getCarTypes() {
		return carTypes;
	}

	public void setCarTypes(ArrayList<Integer> carTypes) {
		this.carTypes = carTypes;
	}

	public ArrayList<Integer> getServices() {
		return services;
	}

	public void setServices(ArrayList<Integer> services) {
		this.services = services;
	}

	public ArrayList<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(ArrayList<Contact> contacts) {
		this.contacts = contacts;
	}

	public ArrayList<Double> getLonLat() {
		return lonLat;
	}

	public void setLonLat(ArrayList<Double> lonLat) {
		this.lonLat = lonLat;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public ArrayList<BoxSettings> getBoxSettings() {
		return boxSettings;
	}

	public void setBoxSettings(ArrayList<BoxSettings> boxSettings) {
		this.boxSettings = boxSettings;
	}
	
	public ArrayList<String> getPayOptions() {
		return payOptions;
	}

	public void setPayOptions(ArrayList<String> payOptions) {
		this.payOptions = payOptions;
	}
	

	public ArrayList<String> getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(ArrayList<String> additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isWasherPublic() {
		return isWasherPublic;
	}

	public void setWasherPublic(boolean isWasherPublic) {
		this.isWasherPublic = isWasherPublic;
	}

	public List<GroupMenu> getGroupMenu() {
		return groupMenu;
	}

	public void setGroupMenu(List<GroupMenu> groupMenu) {
		this.groupMenu = groupMenu;
	}

	public Boolean getUserWanted() {
		return isUserWanted;
	}

	public void setUserWanted(Boolean userWanted) {
		isUserWanted = userWanted;
	}

	public Integer getWantsCount() {
		return wantsCount;
	}

	public void setWantsCount(Integer wantsCount) {
		this.wantsCount = wantsCount;
	}

	// GSON
	public String serialize() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	public static WasherBase deserialize(String serializedData) {
		Gson gson = new Gson();
		return gson.fromJson(serializedData, WasherBase.class);
	}

	@Override
	public WasherBase clone() {
		try {
			WasherBase cloned = (WasherBase) super.clone();
			cloned.setName(cloned.getName());
			cloned.setAddress(cloned.getAddress());
			cloned.setCarTypes(cloned.getCarTypes());
			cloned.setLonLat(cloned.getLonLat());
			cloned.setServices(cloned.getServices());
			cloned.setMenu(cloned.getMenu());
			cloned.setSchedule(cloned.getSchedule());
			cloned.setAdditionalInfo(cloned.getAdditionalInfo());
			cloned.setBoxSettings(cloned.getBoxSettings());
			cloned.setPayOptions(cloned.getPayOptions());
			cloned.setContacts(cloned.getContacts());
			cloned.setBoxSettings(null); //Temp
			cloned.setWasherPublic(cloned.isWasherPublic());
			cloned.setGroupMenu(cloned.getGroupMenu());
			return cloned;
		}
		catch (CloneNotSupportedException e){
		}
		return null;
	}


	public static class GroupMenu{
		@SerializedName("uid")
		String id;
		String name;
		List<Integer> services;
		List<ReversePrices> prices;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<Integer> getServices() {
			return services;
		}

		public void setServices(List<Integer> services) {
			this.services = services;
		}

		public List<ReversePrices> getPrices() {
			return prices;
		}

		public void setPrices(List<ReversePrices> prices) {
			this.prices = prices;
		}
	}

}
