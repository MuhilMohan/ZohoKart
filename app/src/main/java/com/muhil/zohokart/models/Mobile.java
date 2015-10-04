package com.muhil.zohokart.models;


import com.google.gson.annotations.SerializedName;

public class Mobile implements Product {

    private int id;
    private String name;
    private String brand;
    private String color;
    @SerializedName("internal_memory")
    private int internalMemory;
    private double price;
    private String thumbnail;
    private double stars;
    private int ratings;

    public Mobile() {
    }

    public Mobile(int id, String name, String brand, String color, int internalMemory, double price, String thumbnail, double stars, int ratings) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.color = color;
        this.internalMemory = internalMemory;
        this.price = price;
        this.thumbnail = thumbnail;
        this.stars = stars;
        this.ratings = ratings;
    }
}
