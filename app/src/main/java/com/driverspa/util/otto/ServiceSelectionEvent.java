package com.driverspa.util.otto;

import java.util.List;

import com.driverspa.model.BookInfo;
import com.driverspa.model.WasherPublic;

/**
 * Created by Yerzhan Tanatov on 26/01/18.
 */
public class ServiceSelectionEvent {

    WasherPublic washer; List<BookInfo.PriceDetail> priceDetails;
    List<Integer> services;
    List<String> groupServices;
    Double price;
    int minutes;

    public ServiceSelectionEvent(WasherPublic washer, List<BookInfo.PriceDetail> priceDetails, List<Integer> services, List<String> groupServices, Double price, int minutes) {
        this.washer = washer;
        this.priceDetails = priceDetails;
        this.services = services;
        this.groupServices = groupServices;
        this.price = price;
        this.minutes = minutes;
    }

    public WasherPublic getWasher() {
        return washer;
    }

    public List<BookInfo.PriceDetail> getPriceDetails() {
        return priceDetails;
    }

    public List<Integer> getServices() {
        return services;
    }

    public List<String> getGroupServices() {
        return groupServices;
    }

    public Double getPrice() {
        return price;
    }

    public int getMinutes() {
        return minutes;
    }
}
