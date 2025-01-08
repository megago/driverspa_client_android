package com.driverspa.model;

import com.google.gson.Gson;

import java.util.HashMap;

/**
 * Created by Yerzhan Tanatov on 06/07/16.
 */
public class NewPushInformation {

    HashMap<String,PushData> data;

    public NewPushInformation(HashMap<String, PushData> data) {
        this.data = data;
    }

    public HashMap<String, PushData> getData() {
        return data;
    }

    public void setData(HashMap<String, PushData> data) {
        this.data = data;
    }

    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static NewPushInformation deserialize(String serializedData) {
        Gson gson = new Gson();
        return gson.fromJson(serializedData, NewPushInformation.class);
    }
}
