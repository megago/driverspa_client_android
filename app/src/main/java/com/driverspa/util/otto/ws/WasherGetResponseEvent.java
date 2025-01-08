package com.driverspa.util.otto.ws;

import com.driverspa.model.Washer;

/**
 * Created by Yerzhan Tanatov on 20/12/15.
 */
public class WasherGetResponseEvent {
    Washer washer;

    public WasherGetResponseEvent(Washer washer) {
        this.washer = washer;
    }

    public Washer getWasher() {
        return washer;
    }

    public void setWasher(Washer washer) {
        this.washer = washer;
    }
}
