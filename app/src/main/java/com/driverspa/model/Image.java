package com.driverspa.model;

public class Image {	
	private String id;	
	
	private ImageDetail original;
	private ImageDetail thumb1;
	private ImageDetail thumb2;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ImageDetail getOriginal() {
		return original;
	}

	public void setOriginal(ImageDetail original) {
		this.original = original;
	}

	public ImageDetail getThumb1() {
		return thumb1;
	}

	public void setThumb1(ImageDetail thumb1) {
		this.thumb1 = thumb1;
	}

	public ImageDetail getThumb2() {
		return thumb2;
	}

	public void setThumb2(ImageDetail thumb2) {
		this.thumb2 = thumb2;
	}
	
	public PhotoOrm getPhotoOrm(String userId){
		PhotoOrm imageOrm = new PhotoOrm();
		imageOrm.setId(getId());
		imageOrm.setOrginalUrl(getOriginal().getUrl());
		imageOrm.setThumb1Url(getThumb1().getUrl());
		imageOrm.setThumb2Url(getThumb2().getUrl());
		imageOrm.setUserId(userId);
		return imageOrm;
	}
}
