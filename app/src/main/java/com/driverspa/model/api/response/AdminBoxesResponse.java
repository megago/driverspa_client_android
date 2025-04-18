package com.driverspa.model.api.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AdminBoxesResponse extends BaseResponse {

	@SerializedName("objects")
	ArrayList<BoxType> boxes;

	public ArrayList<BoxType> getBoxes(){
		return boxes;
	}
	
	public static class BoxType {
		@SerializedName("booking_type")
		private String bookingType;
		@SerializedName("uid")
		private String boxId;

		@SerializedName("box_name")
		private String boxName;

		@SerializedName("washer_person")
		private String washerPerson;

		public String getBoxName() {
			return boxName;
		}



		public void setBoxName(String boxName) {
			this.boxName = boxName;
		}

		public String getBookingType() {
			return bookingType;
		}

		public void setBookingType(String bookingType) {
			this.bookingType = bookingType;
		}

		public String getBoxId() {
			return boxId;
		}

		public void setBoxId(String boxId) {
			this.boxId = boxId;
		}

		public String getWasherPerson() {
			return washerPerson;
		}

		public void setWasherPerson(String washerPerson) {
			this.washerPerson = washerPerson;
		}
	}
	
	
}
