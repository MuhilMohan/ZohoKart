package com.muhil.zohokart.models;


public interface IProduct {

    int getId();

    int getCategoryId();

    String getTitle();

    String getDescription();

    double getPrice();

    double getStars();

    int getRatings();

    Product getProduct();

}
