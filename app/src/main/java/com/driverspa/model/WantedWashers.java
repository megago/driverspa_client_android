package com.driverspa.model;

import com.google.gson.Gson;

import java.util.HashMap;

/**
 * Created by Yerzhan Tanatov on 18/01/18.
 */
public class WantedWashers {
    HashMap<String, String> wantedWashers;

    public WantedWashers(HashMap<String, String> wantedWashers) {
        this.wantedWashers = wantedWashers;
    }

    public HashMap<String, String> getWantedWashers() {
        return wantedWashers;
    }

    public void setWantedWashers(HashMap<String, String> wantedWashers) {
        this.wantedWashers = wantedWashers;
    }


    // GSON
    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static WantedWashers deserialize(String serializedData) {
        Gson gson = new Gson();
        return gson.fromJson(serializedData, WantedWashers.class);
    }
}
