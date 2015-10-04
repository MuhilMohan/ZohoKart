package com.muhil.zohokart.models;


import com.google.gson.annotations.SerializedName;

public class Mobile implements IProduct {

    private int id;
    @SerializedName("category_id")
    private int categoryId;
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

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getCategoryId() {
        return categoryId;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getDescription() {
        return color + ", " + internalMemory + " GB";
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public double getStars() {
        return stars;
    }

    @Override
    public int getRatings() {
        return ratings;
    }

    @Override
    public Product getProduct() {
        Product product = new Product(this.getId(), this.getCategoryId(),
                this.getTitle(), this.getDescription(), this.getPrice(),
                this.getStars(), this.getRatings());
        return product;
    }


}
