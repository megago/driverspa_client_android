package com.driverspa.util.otto;

/**
 * Created by Yerzhan Tanatov on 08/02/16.
 */
public class SmsRecoveryReceivedEvent {
    String code;

    public SmsRecoveryReceivedEvent(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
