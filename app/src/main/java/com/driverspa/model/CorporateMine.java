package com.driverspa.model;

import com.driverspa.model.api.response.BaseResponseHolder;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * corporate_client/mine?carwash=: the signed-in driver's company cars (by the plates of the
 * driver's cars or the driver's phone), docs/corporate-clients.md. A company car's wash is paid by
 * the company, with the agreement's discount.
 */
public class CorporateMine extends BaseResponseHolder {
	@SerializedName("data")
	Data data;

	public List<Item> getObjects() {
		return data == null ? null : data.objects;
	}

	public static class Data {
		@SerializedName("objects")
		List<Item> objects;
	}

	public static class Item {
		@SerializedName("company")
		public Company company;
		@SerializedName("car")
		public Car car;
		/** The car washes (of the company's owner) where it is a company car. */
		@SerializedName("carwashes")
		public List<String> carwashes;
	}

	public static class Company {
		@SerializedName("id")
		public String id;
		@SerializedName("name")
		public String name;
		/** The agreement's discount, % of the normal price. */
		@SerializedName("discount")
		public int discount;
	}

	public static class Car {
		@SerializedName("uid")
		public String uid;
		@SerializedName("plate")
		public String plate;
		@SerializedName("car_type")
		public Integer carType;
		@SerializedName("model")
		public String model;
	}

	/** A plate as the server compares it: no name in brackets, spaces or dashes; upper case. */
	public static String key(String plate) {
		if (plate == null) return "";
		int bracket = plate.indexOf('(');
		if (bracket >= 0) plate = plate.substring(0, bracket);
		return plate.replaceAll("[\\s-]", "").toUpperCase(java.util.Locale.ROOT);
	}
}
