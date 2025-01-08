package com.driverspa.util.otto;

import com.driverspa.model.NewPushInformation;

/**
 * Created by Yerzhan Tanatov on 06/07/16.
 */
public class NewReviewPushResponseEvent {
    NewPushInformation push;

    public NewReviewPushResponseEvent(NewPushInformation push) {
        this.push = push;
    }

    public NewPushInformation getPush() {
        return push;
    }

    public void setPush(NewPushInformation push) {
        this.push = push;
    }
}
