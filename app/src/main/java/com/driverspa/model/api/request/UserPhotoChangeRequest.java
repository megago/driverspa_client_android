package com.driverspa.model.api.request;

import com.google.gson.annotations.SerializedName;

public class UserPhotoChangeRequest {

	@SerializedName("user")
	private String userId;
	@SerializedName("avatar")
	private String photoId;
	
	public UserPhotoChangeRequest(String userId, String photoId) {
		super();
		this.userId = userId;
		this.photoId = photoId;
	}
	
}
