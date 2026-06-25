package com.driverspa.model.api.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class AuthClientRegistrationRequest  implements Parcelable {

	@SerializedName("mobile")
	private String phone;
	// OTP delivery channel: "email" (default), "sms" or "whatsapp".
	@SerializedName("channel")
	private String channel = "email";
	// Email destination — required when channel == "email".
	@SerializedName("email")
	private String email;

	public AuthClientRegistrationRequest (String phone) {
		this.phone = phone;
	}

	public AuthClientRegistrationRequest (String phone, String channel, String email) {
		this.phone = phone;
		this.channel = channel;
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(phone);
		dest.writeString(channel);
		dest.writeString(email);
	}

	public AuthClientRegistrationRequest(Parcel in) {
		phone = in.readString();
		channel = in.readString();
		email = in.readString();
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
