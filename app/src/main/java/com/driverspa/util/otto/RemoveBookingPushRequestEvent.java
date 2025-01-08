package com.driverspa.util.otto;

import com.driverspa.model.PushData;

/**
 * Created by Yerzhan Tanatov on 06/07/16.
 */
public class RemoveBookingPushRequestEvent {
    PushData data;

    public RemoveBookingPushRequestEvent(PushData data) {
        this.data = data;
    }

    public PushData getData() {
        return data;
    }

    public void setData(PushData data) {
        this.data = data;
    }
}
