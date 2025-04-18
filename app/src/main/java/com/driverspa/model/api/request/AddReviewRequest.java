package com.driverspa.model.api.request;


import com.google.gson.annotations.SerializedName;
import com.squareup.otto.Subscribe;

public class AddReviewRequest {

	private String carwash;
	private Integer mark;
	private String text;
	@SerializedName("review_type")
	private String reviewType;

	public String getCarwash() {
		return carwash;
	}
	public void setCarwash(String carwash) {
		this.carwash = carwash;
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

	public String getReviewType() {
		return reviewType;
	}

	public void setReviewType(String reviewType) {
		this.reviewType = reviewType;
	}
}
