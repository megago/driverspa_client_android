package com.driverspa.model.api.response;

public class BaseResponseHolder {

	private String status;	
	public void setStatus(String status) {
		this.status = status;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	private String message;
	
	public String getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}	
	
	@Override
	public String toString() {
		return "BaseResponseHolder [status=" + status + ", message="+message+"]";
	}
	
}
