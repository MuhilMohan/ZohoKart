package com.muhil.zohokart.models;

public class Product {
    private int id;
    private int categoryId;
    private String brand;
    private String title;
    private String description;
    private String thumbnail;
    private double price;
    private double stars;
    private int ratings;

    public Product(int id, int categoryId, String brand, String title, String description, String thumbnail, double price, double stars, int ratings) {
        this.id = id;
        this.categoryId = categoryId;
        this.brand = brand;
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
        this.price = price;
        this.stars = stars;
        this.ratings = ratings;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", categoryId=" + categoryId +
                ", brand='" + brand + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", price=" + price +
                ", stars=" + stars +
                ", ratings=" + ratings +
                '}';
    }

    public int getId() {
        return id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getBrand() {
        return brand;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public double getPrice() {
        return price;
    }

    public double getStars() {
        return stars;
    }

    public int getRatings() {
        return ratings;
    }
}
