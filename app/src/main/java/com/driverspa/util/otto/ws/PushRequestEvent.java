package com.driverspa.util.otto.ws;

import com.driverspa.model.Device;

public class PushRequestEvent {

    String deviceId;
    Device device;

    public PushRequestEvent(String deviceId, Device device) {
        this.deviceId = deviceId;
        this.device = device;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
