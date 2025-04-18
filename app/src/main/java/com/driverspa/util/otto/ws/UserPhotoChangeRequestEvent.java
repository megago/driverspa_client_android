package com.driverspa.util.otto.ws;

public class UserPhotoChangeRequestEvent {

	private String userId;
	private String photoId;
	
	public UserPhotoChangeRequestEvent(String userId, String photoId) {
		super();
		this.userId = userId;
		this.photoId = photoId;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public String getPhotoId() {
		return photoId;
	}
	
}
