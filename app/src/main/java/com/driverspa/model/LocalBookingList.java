package com.driverspa.model;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Yerzhan Tanatov on 05/08/16.
 */
public class LocalBookingList {
    List<BookInfo> books;

    public List<BookInfo> getBooks() {
        return books;
    }

    public void setBooks(List<BookInfo> books) {
        this.books = books;
    }

    // GSON
    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static LocalBookingList deserialize(String serializedData) {
        Gson gson = new Gson();
        return gson.fromJson(serializedData, LocalBookingList.class);
    }
}
