package com.driverspa.util.otto.ws;

import com.driverspa.model.api.response.BaseResponseHolder;

/**
 * Created by Yerzhan Tanatov on 05/07/16.
 */
public class DeleteDeviceResponseEvent {
    BaseResponseHolder response;

    public DeleteDeviceResponseEvent(BaseResponseHolder response) {
        this.response = response;
    }

    public BaseResponseHolder getResponse(){
        return this.response;
    }

}
