package com.driverspa.model;

import android.location.Location;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Washer extends WasherPublic {

	public Washer() {
		super();
		setImages(new ArrayList<Image>());
//		setBoxSettings(new ArrayList<BoxSettings>());
		setPayOptions(new ArrayList<String>());
//        setReviews(new ArrayList<Review>());
	}


	private ArrayList<Review> reviews;

	//	private AdditionalInfo additionalInfo;

	private Location washerLocation;

	public static class Prices {
		private Double price;
		private Integer time;
		private Integer type;

		public Double getPrice() {
			return price;
		}

		public void setPrice(Double price) {
			this.price = price;
		}

		public Integer getTime() {
			return time;
		}

		public void setTime(Integer time) {
			this.time = time;
		}

		public Integer getType() {
			return type;
		}

		public void setType(Integer type) {
			this.type = type;
		}
	}

	public static class BoxSettings {
		@SerializedName("booking_type")
		private BookingType bookingType;
		private String uid;
		@SerializedName("box_name")
        private String boxName;
		private Date ts;
		@SerializedName("washer_person")
		private String washerPerson;

		public BookingType getBookingType() {
			return bookingType;
		}

		public void setBookingType(BookingType bookingType) {
			this.bookingType = bookingType;
		}

		public String getUid() {
			return uid;
		}

		public void setUid(String uid) {
			this.uid = uid;
		}

		public String getBoxName() {
			return boxName;
		}

		public void setBoxName(String boxName) {
			this.boxName = boxName;
		}

		public Date getTs() {
			return ts;
		}

		public void setTs(Date ts) {
			this.ts = ts;
		}

		public String getWasherPerson() {
			return washerPerson;
		}

		public void setWasherPerson(String washerPerson) {
			this.washerPerson = washerPerson;
		}
	}

	public static class AdditionalInfo {

		@SerializedName("carwash_automatic")
		private boolean carwashAutomatic;

		@SerializedName("carwash_cafe")
		private boolean carwashCafe;

		@SerializedName("carwash_chemical")
		private boolean carwashChemical;

		@SerializedName("carwash_hand")
		private boolean carwashHand;

		@SerializedName("carwash_truck")
		private boolean carwashTruck;

		@SerializedName("carwash_wifi")
		private boolean carwashWifi;

		@SerializedName("free_tea")
		private boolean freeTea;

		public boolean isFreeTea() {
			return freeTea;
		}

		public void setFreeTea(boolean freeTea) {
			this.freeTea = freeTea;
		}

		private String currency;

		public boolean isCarwashAutomatic() {
			return carwashAutomatic;
		}

		public void setCarwashAutomatic(boolean carwashAutomatic) {
			this.carwashAutomatic = carwashAutomatic;
		}

		public boolean isCarwashCafe() {
			return carwashCafe;
		}

		public void setCarwashCafe(boolean carwashCafe) {
			this.carwashCafe = carwashCafe;
		}

		public boolean isCarwashChemical() {
			return carwashChemical;
		}

		public void setCarwashChemical(boolean carwashChemical) {
			this.carwashChemical = carwashChemical;
		}

		public boolean isCarwashHand() {
			return carwashHand;
		}

		public void setCarwashHand(boolean carwashHand) {
			this.carwashHand = carwashHand;
		}

		public boolean isCarwashTruck() {
			return carwashTruck;
		}

		public void setCarwashTruck(boolean carwashTruck) {
			this.carwashTruck = carwashTruck;
		}

		public boolean isCarwashWifi() {
			return carwashWifi;
		}

		public void setCarwashWifi(boolean carwashWifi) {
			this.carwashWifi = carwashWifi;
		}

		public String getCurrency() {
			return currency;
		}

		public void setCurrency(String currency) {
			this.currency = currency;
		}
	}

	public static class Contact {
		private String alias;

		private String comment;

		private String type;

		private String value;

		public String getAlias() {
			return alias;
		}

		public void setAlias(String alias) {
			this.alias = alias;
		}

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	public static class Schedule {

		String comment;

		@SerializedName("mon")
		ArrayList<Day> monday;

		@SerializedName("tue")
		ArrayList<Day> tuesday;

		@SerializedName("wed")
		ArrayList<Day> wednesday;

		@SerializedName("thu")
		ArrayList<Day> thursday;

		@SerializedName("fri")
		ArrayList<Day> friday;

		@SerializedName("sat")
		ArrayList<Day> saturday;

		@SerializedName("sun")
		ArrayList<Day> sunday;

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public ArrayList<Day> getMonday() {
			return monday;
		}

		public void setMonday(ArrayList<Day> monday) {
			this.monday = monday;
		}

		public ArrayList<Day> getTuesday() {
			return tuesday;
		}

		public void setTuesday(ArrayList<Day> tuesday) {
			this.tuesday = tuesday;
		}

		public ArrayList<Day> getWednesday() {
			return wednesday;
		}

		public void setWednesday(ArrayList<Day> wednesday) {
			this.wednesday = wednesday;
		}

		public ArrayList<Day> getThursday() {
			return thursday;
		}

		public void setThursday(ArrayList<Day> thursday) {
			this.thursday = thursday;
		}

		public ArrayList<Day> getFriday() {
			return friday;
		}

		public void setFriday(ArrayList<Day> friday) {
			this.friday = friday;
		}

		public ArrayList<Day> getSaturday() {
			return saturday;
		}

		public void setSaturday(ArrayList<Day> saturday) {
			this.saturday = saturday;
		}

		public ArrayList<Day> getSunday() {
			return sunday;
		}

		public void setSunday(ArrayList<Day> sunday) {
			this.sunday = sunday;
		}

		public ArrayList<Day> getToday() {
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
			
			switch (dayOfWeek) {
			case 2:
				return getMonday();
			case 3:
				return getTuesday();
			case 4:
				return getWednesday();
			case 5:
				return getThursday();
			case 6:
				return getFriday();
			case 7:
				return getSaturday();
			case 1:
				return getSunday();
			default:
				return null;
			}
		}

		public ArrayList<Day> getTomorrow() {
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			c.add(Calendar.DAY_OF_YEAR, 1);
			int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
			switch (dayOfWeek) {
			case Calendar.MONDAY:
				return monday;
			case Calendar.TUESDAY:
				return tuesday;
			case Calendar.WEDNESDAY:
				return wednesday;
			case Calendar.THURSDAY:
				return thursday;
			case Calendar.FRIDAY:
				return friday;
			case Calendar.SATURDAY:
				return saturday;
			case Calendar.SUNDAY:
				return sunday;
			default:
				return null;
			}
		}
	}

	public static class Day {
		@SerializedName("fr")
		int from;
		int to;

		public int getFrom() {
			return from;
		}

		public void setFrom(int from) {
			this.from = from;
		}

		public int getTo() {
			return to;
		}

		public void setTo(int to) {
			this.to = to;
		}
	}

	public static class Review {
		String author;
		@SerializedName("author_id")
		String authorId;
		String id;
		Integer mark;
		String text;

		public String getAuthor() {
			return author;
		}

		public void setAuthor(String author) {
			this.author = author;
		}

		public String getAuthorId() {
			return authorId;
		}

		public void setAuthorId(String authorId) {
			this.authorId = authorId;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public Integer getMark() {
			return mark;
		}

		public void setMark(Integer mark) {
			this.mark = mark;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}
	}


	public Location getWasherLocation() {
		try {
			washerLocation.setLatitude(Double.parseDouble(getLonLat().get(1)
					.toString()));
			washerLocation.setLongitude(Double.parseDouble(getLonLat().get(0)
					.toString()));
		} catch (Exception e) {
		}
		return washerLocation;
	}

	public ArrayList<Review> getReviews() {
		return reviews;
	}

	public void setReviews(ArrayList<Review> reviews) {
		this.reviews = reviews;
	}

	// GSON
	public String serialize() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	public static Washer deserialize(String serializedData) {
		Gson gson = new Gson();
		return gson.fromJson(serializedData, Washer.class);
	}

}
