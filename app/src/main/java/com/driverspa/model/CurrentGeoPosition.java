package com.driverspa.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Yerzhan Tanatov on 15/08/16.
 */
public class CurrentGeoPosition {
    Double lat;
    Double lng;

    public CurrentGeoPosition(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String serialize() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }

    public static CurrentGeoPosition deserialize(String serializedData) {
        Gson gson = new Gson();
        return gson.fromJson(serializedData, CurrentGeoPosition.class);
    }
}
