package com.driverspa.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Yerzhan Tanatov on 11/12/16.
 */
public class Notification {


    String id;
    @SerializedName("resource_uri")
    String resourceUri;
    String text;
    @SerializedName("ts")
    Date TS;

    @SerializedName("attrs")
    AdditionalObject additionalObject;

    public static class AdditionalObject {
        @SerializedName("object_id")
        String objectId;
        String type;

        public String getObjectId() {
            return objectId;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getTS() {
        return TS;
    }

    public void setTS(Date TS) {
        this.TS = TS;
    }

    public AdditionalObject getAdditionalObject() {
        return additionalObject;
    }

    public void setAdditionalObject(AdditionalObject additionalObject) {
        this.additionalObject = additionalObject;
    }
}
