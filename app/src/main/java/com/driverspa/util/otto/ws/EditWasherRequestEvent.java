package com.driverspa.util.otto.ws;

import com.driverspa.model.WasherBase;

public class EditWasherRequestEvent {

	 WasherBase request;
	 String washerId;
	 
	 public EditWasherRequestEvent (String washerId,WasherBase request){
		 this.request = request;
		 this.washerId = washerId;
	 }
	 
	 public WasherBase getRequest(){
		 return this.request;
	 }
	 
	 public String getWasherId(){
		 return washerId;
	 }
}
