package com.muhil.zohokart.models;


public interface IProduct {

    int getId();

    int getCategoryId();

    String getBrand();

    String getTitle();

    String getDescription();

    String getThumbnail();

    double getPrice();

    double getStars();

    int getRatings();

    String getWarranty();

    Product getProduct();

}
