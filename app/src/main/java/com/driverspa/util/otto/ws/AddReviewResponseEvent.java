package com.driverspa.util.otto.ws;

import com.driverspa.model.api.response.BaseResponseHolder;

public class AddReviewResponseEvent {
   BaseResponseHolder response;
   
   public AddReviewResponseEvent(BaseResponseHolder response){
	   this.response = response;
   }
   
   public BaseResponseHolder getResponse(){
	   return this.response;			   
   }
}
