package com.driverspa.model.api.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class AuthAdminRegistrationRequest  implements Parcelable {

	@SerializedName("mobile")
	private String phone;
	@SerializedName("company_name")
	private String companyName;
	
	public AuthAdminRegistrationRequest (String companyName, String phone) {
		this.phone = phone;
		this.companyName = companyName;
	}
	
	public String getPhone() {
		return phone;
	}

	public String getCompanyName() {
		return companyName;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(phone);		
		dest.writeString(companyName);		
	}		
	
	public AuthAdminRegistrationRequest(Parcel in) {
		phone = in.readString();
		companyName = in.readString();		
	}
	
	public static final Creator<AuthAdminRegistrationRequest> CREATOR = new Creator<AuthAdminRegistrationRequest>() {
		public AuthAdminRegistrationRequest createFromParcel(Parcel in) {
			return new AuthAdminRegistrationRequest(in);
		}

		public AuthAdminRegistrationRequest[] newArray(int size) {
			return new AuthAdminRegistrationRequest[size];
		}
	};
		
}
