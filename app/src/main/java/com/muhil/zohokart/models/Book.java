package com.muhil.zohokart.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by muhil-ga42 on 19/10/15.
 */
public class Book implements IProduct
{

    private int id;
    @SerializedName("sub_category_id")
    private int categoryId;
    private String name;
    private String publisher;
    private String author;
    private String binding;
    private double price;
    private String thumbnail;
    private double stars;
    private int ratings;

    public Book()
    {
    }

    public Book(int id, int categoryId, String name, String publisher, String author, String binding, double price, String thumbnail, double stars, int ratings)
    {
        this.author = author;
        this.binding = binding;
        this.categoryId = categoryId;
        this.id = id;
        this.name = name;
        this.price = price;
        this.publisher = publisher;
        this.ratings = ratings;
        this.stars = stars;
        this.thumbnail = thumbnail;
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
        return publisher;
    }

    @Override
    public String getTitle()
    {
        return name;
    }

    @Override
    public String getDescription()
    {
        return author + ", " + binding;
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
        return null;
    }

    @Override
    public Product getProduct()
    {
        return new Product(this.getId(), this.getCategoryId(), this.getBrand(),
                this.getTitle(), this.getDescription(), this.getThumbnail(), this.getPrice(),
                this.getStars(), this.getRatings(), this.getWarranty());
    }
}
