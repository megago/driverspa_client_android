package com.driverspa.util.otto.ws;

import com.driverspa.model.Washer;

/**
 * Created by Yerzhan Tanatov on 21/12/15.
 */
public class WasherLocalUpdateRequestEvent {
    Washer washer;

    public WasherLocalUpdateRequestEvent(Washer washer) {
        this.washer = washer;
    }

    public Washer getWasher() {
        return washer;
    }

    public void setWasher(Washer washer) {
        this.washer = washer;
    }
}
