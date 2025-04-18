package com.driverspa.util.otto.ws;

import com.driverspa.Reference;

public class InitResponseEvent {
	
	Reference reference;
	
    public InitResponseEvent(Reference reference){
    	this.reference = reference;
    }
    
    public Reference getReference(){
    	return reference;
    }
}
