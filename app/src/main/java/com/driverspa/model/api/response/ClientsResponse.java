package com.driverspa.model.api.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.driverspa.model.ClientInfo;

public class ClientsResponse {

	 @SerializedName("company_clients")
     List<ClientInfo> clients;

	public List<ClientInfo> getClients() {
		return clients;
	}

	public void setClients(List<ClientInfo> clients) {
		this.clients = clients;
	}
}
