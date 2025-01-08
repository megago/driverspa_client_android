package com.driverspa.util.otto.ws;

public class InitRequestEvent {
	String lang;

	public InitRequestEvent(String lang){
		this.lang = lang;
	}
	
	public String getLang() {
		return lang;
	}

}
