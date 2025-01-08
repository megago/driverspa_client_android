package com.driverspa.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Yerzhan Tanatov on 14/07/16.
 */
public class AboutUs {
    @SerializedName("about_us")
    String aboutUS;

    public String getAboutUS() {
        return aboutUS;
    }

    public void setAboutUS(String aboutUS) {
        this.aboutUS = aboutUS;
    }
}
