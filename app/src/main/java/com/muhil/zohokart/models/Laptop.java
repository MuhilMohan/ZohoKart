package com.muhil.zohokart.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by muhil-ga42 on 18/10/15.
 */
public class Laptop implements IProduct
{

    private int id;
    @SerializedName("sub_category_id")
    private int categoryId;
    private String name;
    private String brand;
    private String color;
    private int ram;
    private double inches;
    private double price;
    private String thumbnail;
    private double stars;
    private int ratings;
    private String warranty;

    public Laptop()
    {
    }

    public Laptop(int id, int categoryId, String name, String brand, String color, int ram, double inches, double price, String thumbnail, double stars, int ratings, String warranty)
    {
        this.brand = brand;
        this.categoryId = categoryId;
        this.color = color;
        this.id = id;
        this.inches = inches;
        this.name = name;
        this.price = price;
        this.ram = ram;
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
        return color + ", " + ram + " GB, " + inches + "\"";
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
