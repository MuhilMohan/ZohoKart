package com.muhil.zohokart.models;

public class Product {
    private int id;
    private int categoryId;
    private String title;
    private String description;
    private double price;
    private double stars;
    private int ratings;

    public Product(int id, int categoryId, String title, String description, double price, double stars, int ratings) {
        this.id = id;
        this.categoryId = categoryId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.stars = stars;
        this.ratings = ratings;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", categoryId=" + categoryId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
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

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
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
