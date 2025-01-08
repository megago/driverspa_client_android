package com.driverspa.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Yerzhan Tanatov on 06/07/16.
 */
public class PushData {

    @SerializedName("carwash_id")
    String carwashId;
    Integer mark;
    @SerializedName("object_id")
    String objectId;
    String text;
    String type;
    @SerializedName("bid_object")
    FareRequest.BidObject bidObject;

    public PushData(String objectId) {
        this.objectId = objectId;
    }

    public String getCarwashId() {
        return carwashId;
    }

    public void setCarwashId(String carwashId) {
        this.carwashId = carwashId;
    }

    public Integer getMark() {
        return mark;
    }

    public void setMark(Integer mark) {
        this.mark = mark;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public FareRequest.BidObject getBidObject() {
        return bidObject;
    }

    public void setBidObject(FareRequest.BidObject bidObject) {
        this.bidObject = bidObject;
    }

    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static PushData deserialize(String serializedData) {
        Gson gson = new Gson();
        return gson.fromJson(serializedData, PushData.class);
    }
}
