package com.driverspa.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PhotoParcelable extends PhotoOrm implements Parcelable {

	public PhotoParcelable() {
		super();
	}
	
	public static PhotoParcelable newPhoto(PhotoOrm photoOrm) {
		PhotoParcelable photo = new PhotoParcelable();
		photo.setId(photoOrm.getId());
		photo.setOrginalUrl(photoOrm.getOrginalUrl());
		photo.setThumb1Url(photoOrm.getThumb1Url());
		photo.setThumb2Url(photoOrm.getThumb2Url());		
		photo.setUserId(photoOrm.getUserId());
		return photo;
	}
	
	// # PARCELABLE STUFF
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel destination, int flags) {
		destination.writeString(getId());
		destination.writeString(getOrginalUrl());
		destination.writeString(getThumb1Url());
		destination.writeString(getThumb2Url());
		destination.writeString(getUserId());
	}

	public PhotoParcelable(Parcel in){
		setId(in.readString());
		setOrginalUrl(in.readString());
		setThumb1Url(in.readString());
		setThumb2Url(in.readString());
		setUserId(in.readString());
	}

	public static final Creator<PhotoParcelable> CREATOR = new Creator<PhotoParcelable>() {
		public PhotoParcelable createFromParcel(Parcel in) {
			return new PhotoParcelable(in);
		}

		public PhotoParcelable[] newArray(int size) {
			return new PhotoParcelable[size];
		}
	};

	@Override
	public String toString() {
		return "PhotoParcelable [getImageUrl()=";
	}

}
