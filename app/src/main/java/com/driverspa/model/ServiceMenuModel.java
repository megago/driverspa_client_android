package com.driverspa.model;

import java.util.ArrayList;

import com.driverspa.model.Washer.Prices;

public class ServiceMenuModel {
	Integer carType;
	ArrayList<Prices> prices;

	public Integer getCarType() {
		return carType;
	}

	public void setCarType(Integer carType) {
		this.carType = carType;
	}

	public ArrayList<Prices> getPrices() {
		return prices;
	}

	public void setPrices(ArrayList<Prices> prices) {
		this.prices = prices;
	}

}
