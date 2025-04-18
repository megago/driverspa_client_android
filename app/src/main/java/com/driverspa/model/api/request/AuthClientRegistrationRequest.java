package com.driverspa.model.api.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class AuthClientRegistrationRequest  implements Parcelable {

	@SerializedName("mobile")
	private String phone;
	
	public AuthClientRegistrationRequest (String phone) {
		this.phone = phone;
	}
	
	public String getPhone() {
		return phone;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(phone);		
	}		
	
	public AuthClientRegistrationRequest(Parcel in) {
		phone = in.readString();		
	}
	
	public static final Creator<AuthClientRegistrationRequest> CREATOR = new Creator<AuthClientRegistrationRequest>() {
		public AuthClientRegistrationRequest createFromParcel(Parcel in) {
			return new AuthClientRegistrationRequest(in);
		}

		public AuthClientRegistrationRequest[] newArray(int size) {
			return new AuthClientRegistrationRequest[size];
		}
	};
		
}
