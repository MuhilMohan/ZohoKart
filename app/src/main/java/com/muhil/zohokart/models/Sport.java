package com.muhil.zohokart.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by muhil-ga42 on 04/11/15.
 */
public class Sport implements IProduct
{
    private int id;
    @SerializedName("sub_category_id")
    private int categoryId;
    private String name;
    private String brand;
    private String color;
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
        SELECTION_ARGS.add(String.valueOf(500));
        FILTER_OPTIONS_GROUP.put("Rs. 500 and below", new FilterPair(Product.FILTER_PRICE_LESSER_THAN, SELECTION_ARGS));

        SELECTION_ARGS = new ArrayList<>();
        SELECTION_ARGS.add(String.valueOf(501));
        SELECTION_ARGS.add(String.valueOf(750));
        FILTER_OPTIONS_GROUP.put("Rs. 501 - Rs. 750", new FilterPair(Product.FILTER_PRICE_RANGE, SELECTION_ARGS));

        SELECTION_ARGS = new ArrayList<>();
        SELECTION_ARGS.add(String.valueOf(751));
        SELECTION_ARGS.add(String.valueOf(1000));
        FILTER_OPTIONS_GROUP.put("Rs. 751 - Rs. 1000", new FilterPair(Product.FILTER_PRICE_RANGE, SELECTION_ARGS));

        SELECTION_ARGS = new ArrayList<>();
        SELECTION_ARGS.add(String.valueOf(1001));
        FILTER_OPTIONS_GROUP.put("Rs. 1001 and above", new FilterPair(Product.FILTER_PRICE_GREATER_THAN, SELECTION_ARGS));

        FILTER_OPTIONS.put("Price", FILTER_OPTIONS_GROUP);

    }

    public Sport() {
    }

    public Sport(int id, int categoryId, String name, String brand, String color, double price, String thumbnail, double stars, int ratings, String warranty)
    {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.brand = brand;
        this.color = color;
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
        return color;
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
