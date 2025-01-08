package com.driverspa.util.otto.ws;

import com.driverspa.model.api.response.AboutUSResponseHolder;

/**
 * Created by Yerzhan Tanatov on 14/07/16.
 */
public class AboutUSResponseEvent {
    AboutUSResponseHolder data;

    public AboutUSResponseEvent(AboutUSResponseHolder data) {
        this.data = data;
    }

    public AboutUSResponseHolder getData() {
        return data;
    }

    public void setData(AboutUSResponseHolder data) {
        this.data = data;
    }


}
