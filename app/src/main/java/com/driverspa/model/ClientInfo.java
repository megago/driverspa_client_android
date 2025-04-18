package com.driverspa.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class ClientInfo {
	
	String name;
	@SerializedName("mobile")
	String phone;
	@SerializedName("car_no")
	String carNo;
	@SerializedName("car_mark")
	String carMark;
	@SerializedName("avatar")
	String profileImageUrl;
	@SerializedName("finished_books_counts")
	Integer finishedBooksCount;
	@SerializedName("rejected_by_client_books_count")
	Integer rejectedByClientBooksCount;
	@SerializedName("nocome_books_count")
	Integer nocomeBooksCount;
	@SerializedName("last_finished_book_date")
	Date lastFinishedBookDate;
	@SerializedName("is_registered")
	Boolean isRegistered;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCarNo() {
		return carNo;
	}

	public void setCarNo(String carNo) {
		this.carNo = carNo;
	}

	public String getCarMark() {
		return carMark;
	}

	public void setCarMark(String carMark) {
		this.carMark = carMark;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	public Integer getFinishedBooksCount() {
		return finishedBooksCount;
	}

	public void setFinishedBooksCount(Integer finishedBooksCount) {
		this.finishedBooksCount = finishedBooksCount;
	}

	public Integer getRejectedByClientBooksCount() {
		return rejectedByClientBooksCount;
	}

	public void setRejectedByClientBooksCount(Integer rejectedByClientBooksCount) {
		this.rejectedByClientBooksCount = rejectedByClientBooksCount;
	}

	public Integer getNocomeBooksCount() {
		return nocomeBooksCount;
	}

	public void setNocomeBooksCount(Integer nocomeBooksCount) {
		this.nocomeBooksCount = nocomeBooksCount;
	}

	public Date getLastFinishedBookDate() {
		return lastFinishedBookDate;
	}

	public void setLastFinishedBookDate(Date lastFinishedBookDate) {
		this.lastFinishedBookDate = lastFinishedBookDate;
	}

	public Boolean getRegistered() {
		return isRegistered;
	}

	public void setRegistered(Boolean registered) {
		isRegistered = registered;
	}

	// GSON
	public String serialize() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	public static ClientInfo deserialize(String serializedData) {
		Gson gson = new Gson();
		return gson.fromJson(serializedData, ClientInfo.class);
	}

}
