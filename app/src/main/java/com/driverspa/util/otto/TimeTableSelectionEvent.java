package com.driverspa.util.otto;

import java.util.Date;

import com.driverspa.model.WasherPublic;

/**
 * Created by Yerzhan Tanatov on 26/01/18.
 */
public class TimeTableSelectionEvent {
    WasherPublic washer;
    Date bookTime;

    public TimeTableSelectionEvent(WasherPublic washer, Date bookTime) {
        this.washer = washer;
        this.bookTime = bookTime;
    }

    public WasherPublic getWasher() {
        return washer;
    }

    public Date getBookTime() {
        return bookTime;
    }
}
