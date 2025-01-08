package com.driverspa.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Yerzhan Tanatov on 05/07/16.
 */
public class Settings {
    @SerializedName("push_token")
    String pushToken;

    public Settings(String pushToken) {
        this.pushToken = pushToken;
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }
}
