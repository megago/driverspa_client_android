package com.driverspa.util.otto;

public class ApiErrorText {

	private String text;
	private Class<?> requestClass;
	
	public ApiErrorText(Class<?> requestClass, String text) {
		super();
		this.requestClass = requestClass;
		this.text = text;
	}
	
	
	public String getText() {
		return text;
	}

	public Class<?> getRequestClass() {
		return requestClass;
	}
	
}
