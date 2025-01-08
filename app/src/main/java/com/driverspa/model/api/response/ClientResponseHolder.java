package com.driverspa.model.api.response;

import com.google.gson.annotations.SerializedName;

import com.driverspa.model.ClientInfo;

public class ClientResponseHolder extends BaseResponseHolder {

	@SerializedName("data")
	private ClientInfo clientInfo;

	public ClientInfo getClientInfo() {
		return clientInfo;
	}

}
