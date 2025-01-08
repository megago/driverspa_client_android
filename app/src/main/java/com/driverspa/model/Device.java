package com.driverspa.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Yerzhan Tanatov on 05/07/16.
 */
public class Device {
    String device;
    String os;
    @SerializedName("token_id")
    String tokenId;
    @SerializedName("push_type")
    String pushType;

    public Device(String device, String os, String tokenId, String pushType) {
        this.device = device;
        this.os = os;
        this.tokenId = tokenId;
        this.pushType = pushType;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getPushType() {
        return pushType;
    }

    public void setPushType(String pushType) {
        this.pushType = pushType;
    }
}
