package com.muhil.zohokart.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by muhil-ga42 on 04/11/15.
 */
public class Lighting implements IProduct
{
    private int id;
    @SerializedName("sub_category_id")
    private int categoryId;
    private String name;
    private String brand;
    private String color;
    private String type;
    private double price;
    private String thumbnail;
    private double stars;
    private int ratings;
    private String warranty;

    public static Map<String, Map<String, FilterPair>> FILTER_OPTIONS;
    public static Map<String, FilterPair> FILTER_OPTIONS_GROUP;
    public static List<String> SELECTION_ARGS;

    static
    {
        FILTER_OPTIONS_GROUP = new LinkedHashMap<>();
        FILTER_OPTIONS = new LinkedHashMap<>();

        SELECTION_ARGS = new ArrayList<>();
        SELECTION_ARGS.add(String.valueOf(400));
        FILTER_OPTIONS_GROUP.put("Rs. 400 and below", new FilterPair(Product.FILTER_PRICE_LESSER_THAN, SELECTION_ARGS));

        SELECTION_ARGS = new ArrayList<>();
        SELECTION_ARGS.add(String.valueOf(401));
        SELECTION_ARGS.add(String.valueOf(800));
        FILTER_OPTIONS_GROUP.put("Rs. 401 - Rs. 800", new FilterPair(Product.FILTER_PRICE_RANGE, SELECTION_ARGS));

        SELECTION_ARGS = new ArrayList<>();
        SELECTION_ARGS.add(String.valueOf(801));
        SELECTION_ARGS.add(String.valueOf(1200));
        FILTER_OPTIONS_GROUP.put("Rs. 801 - Rs. 1200", new FilterPair(Product.FILTER_PRICE_RANGE, SELECTION_ARGS));

        SELECTION_ARGS = new ArrayList<>();
        SELECTION_ARGS.add(String.valueOf(1201));
        FILTER_OPTIONS_GROUP.put("Rs. 1201 and above", new FilterPair(Product.FILTER_PRICE_GREATER_THAN, SELECTION_ARGS));

        FILTER_OPTIONS.put("Price", FILTER_OPTIONS_GROUP);

    }

    public Lighting() {
    }

    public Lighting(int id, int categoryId, String name, String brand, String color, String type, double price, String thumbnail, double stars, int ratings, String warranty)
    {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.brand = brand;
        this.color = color;
        this.type = type;
        this.price = price;
        this.thumbnail = thumbnail;
        this.stars = stars;
        this.ratings = ratings;
        this.warranty = warranty;

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
    public String getBrand() {
        return brand;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getDescription() {
        return color + ", " + type;
    }

    @Override
    public String getThumbnail() {
        return thumbnail;
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
    public String getWarranty() {
        return warranty;
    }


    @Override
    public Product getProduct() {
        return new Product(this.getId(), this.getCategoryId(), this.getBrand(),
                this.getTitle(), this.getDescription(), this.getThumbnail(), this.getPrice(),
                this.getStars(), this.getRatings(), this.getWarranty());
    }

}
