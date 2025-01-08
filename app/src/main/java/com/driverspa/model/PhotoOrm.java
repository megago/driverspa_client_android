package com.driverspa.model;

import com.j256.ormlite.field.DatabaseField;

public class PhotoOrm {
	public static final String FIELD_NAME_USER_ID = "user_id";
	
	@DatabaseField(id=true)
	private String id;
	
	@DatabaseField
	private String orginalUrl;
	
	@DatabaseField
	private String thumb1Url;
	
	@DatabaseField
	private String thumb2Url;
	
	@DatabaseField(columnName = "user_id")
	private String userId;
	
	public String getOrginalUrl() {
		return orginalUrl;
	}

	public void setOrginalUrl(String orginalUrl) {
		this.orginalUrl = orginalUrl;
	}

	public String getThumb1Url() {
		return thumb1Url;
	}

	public void setThumb1Url(String thumb1Url) {
		this.thumb1Url = thumb1Url;
	}

	public String getThumb2Url() {
		return thumb2Url;
	}

	public void setThumb2Url(String thumb2Url) {
		this.thumb2Url = thumb2Url;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public Image getImage(){
		Image image = new Image();
		image.setId(getId());
		image.setOriginal(new ImageDetail(getOrginalUrl()));
		image.setThumb1(new ImageDetail(getThumb1Url()));
		image.setThumb2(new ImageDetail(getThumb2Url()));
		return image;
	}

}
