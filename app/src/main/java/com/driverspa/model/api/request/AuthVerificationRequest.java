package com.driverspa.model.api.request;

import android.os.Parcel;
import android.os.Parcelable;

public class AuthVerificationRequest implements Parcelable {

	private String mobile;
	private String activation_code;	
	
	public AuthVerificationRequest(String mobile, String activation_code) {
		this.mobile = mobile;
		this.activation_code = activation_code;
	}

	public String getMobile() {
		return mobile;
	}

	public String getActivation_code() {
		return activation_code;
	}

	// # Parcelable stuff
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mobile);
		dest.writeString(activation_code);
	}
	
	public AuthVerificationRequest(Parcel in) {
		mobile = in.readString();
		activation_code = in.readString();
	}
	
	public static final Creator<AuthVerificationRequest> CREATOR = new Creator<AuthVerificationRequest>() {
		public AuthVerificationRequest createFromParcel(Parcel in) {
			return new AuthVerificationRequest(in);
		}

		public AuthVerificationRequest[] newArray(int size) {
			return new AuthVerificationRequest[size];
		}
	};
	
}
