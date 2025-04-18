package com.driverspa.model;

import com.j256.ormlite.field.DatabaseField;

public class PhotoUserOrm{
	
	public PhotoUserOrm() {	
	}

	public PhotoUserOrm(PhotoOrm image, User user) {
		this.image = image;
		this.user = user;
	}
	
	@DatabaseField(generatedId=true)
	private Long id;
	
	@DatabaseField(foreign = true, foreignAutoRefresh = true, uniqueCombo=true)
	private PhotoOrm image;
	
	public PhotoOrm getImageOrm() {
		return image;
	}

	public void setImageOrm(PhotoOrm image) {
		this.image = image;
	}

	@DatabaseField(foreign = true, foreignAutoRefresh = true, uniqueCombo=true)
	private User user;

}
