package com.driverspa.util.otto.ws;

import com.driverspa.model.api.response.BaseResponseHolder;
/**
 * Created by Yerzhan Tanatov on 19.05.15.
 */
public class PushResponseEvent {
    BaseResponseHolder data;

    public PushResponseEvent(BaseResponseHolder data) {
        this.data = data;
    }

    public BaseResponseHolder getData() {
        return data;
    }
}
