package com.muhil.zohokart.models;

import com.google.gson.annotations.SerializedName;

public class Tablet implements IProduct
{

    private int id;
    @SerializedName("sub_category_id")
    private int categoryId;
    private String name;
    private String brand;
    private String color;
    @SerializedName("internal_memory")
    private int internalMemory;
    private String connectivity;
    private double price;
    private String thumbnail;
    private double stars;
    private int ratings;
    private String warranty;

    public Tablet()
    {
    }

    public Tablet( int id, int categoryId, String name, String brand, String color, int internalMemory, String connectivity, double price, String thumbnail, double stars, int ratings, String warranty)
    {
        this.brand = brand;
        this.categoryId = categoryId;
        this.color = color;
        this.connectivity = connectivity;
        this.id = id;
        this.internalMemory = internalMemory;
        this.name = name;
        this.price = price;
        this.ratings = ratings;
        this.stars = stars;
        this.thumbnail = thumbnail;
        this.warranty = warranty;
    }

    @Override
    public int getId()
    {
        return id;
    }

    @Override
    public int getCategoryId()
    {
        return categoryId;
    }

    @Override
    public String getBrand()
    {
        return brand;
    }

    @Override
    public String getTitle()
    {
        return name;
    }

    @Override
    public String getDescription()
    {
        return color + ", " + internalMemory + " GB, " + connectivity;
    }

    @Override
    public String getThumbnail()
    {
        return thumbnail;
    }

    @Override
    public double getPrice()
    {
        return price;
    }

    @Override
    public double getStars()
    {
        return stars;
    }

    @Override
    public int getRatings()
    {
        return ratings;
    }

    @Override
    public String getWarranty()
    {
        return warranty;
    }

    @Override
    public Product getProduct()
    {
        return new Product(this.getId(), this.getCategoryId(), this.getBrand(),
                this.getTitle(), this.getDescription(), this.getThumbnail(), this.getPrice(),
                this.getStars(), this.getRatings(), this.getWarranty());
    }
}
