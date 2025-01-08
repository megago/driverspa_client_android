package com.driverspa.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Yerzhan Tanatov on 23/11/16.
 */
public class ReversePrices {
    private Double price;
    private Integer time;
    @SerializedName("cartype")
    private Integer carType;
    private String carTypeName;


    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getCarType() {
        return carType;
    }

    public void setCarType(Integer carType) {
        this.carType = carType;
    }


    public String getCarTypeName() {
        return carTypeName;
    }

    public void setCarTypeName(String carTypeName) {
        this.carTypeName = carTypeName;
    }
}
