package com.driverspa.model;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Yerzhan Tanatov on 30/06/16.
 */
public class TmpGoWash {

    List<GWash> washers;

    public static class GWash{
        Long id;
        String name;
        String address;
        Double latitude;
        Double longitude;
        List<String> phones;
        String info;
        List<WorkingHour> workingHours;
        List<String> images;
        List<Serv> services;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        public List<String> getPhones() {
            return phones;
        }

        public void setPhones(List<String> phones) {
            this.phones = phones;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public List<WorkingHour> getWorkingHours() {
            return workingHours;
        }

        public void setWorkingHours(List<WorkingHour> workingHours) {
            this.workingHours = workingHours;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }

        public List<Serv> getServices() {
            return services;
        }

        public void setServices(List<Serv> services) {
            this.services = services;
        }
    }

    public static class WorkingHour{
        Integer day;
        Integer start;
        Integer end;

        public Integer getDay() {
            return day;
        }

        public void setDay(Integer day) {
            this.day = day;
        }

        public Integer getStart() {
            return start;
        }

        public void setStart(Integer start) {
            this.start = start;
        }

        public Integer getEnd() {
            return end;
        }

        public void setEnd(Integer end) {
            this.end = end;
        }
    }



    public class Serv{
        String name;
        String description;
        String price;
        Integer duration;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public Integer getDuration() {
            return duration;
        }

        public void setDuration(Integer duration) {
            this.duration = duration;
        }
    }

    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static TmpGoWash deserialize(String serializedData) {
        Gson gson = new Gson();
        return gson.fromJson(serializedData, TmpGoWash.class);
    }


    public List<GWash> getWashers() {
        return washers;
    }

    public void setWashers(List<GWash> washers) {
        this.washers = washers;
    }
}
