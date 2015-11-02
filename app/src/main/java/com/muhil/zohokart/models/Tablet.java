package com.muhil.zohokart.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public static Map<String, Map<String, FilterPair>> FILTER_OPTIONS;
    public static Map<String, FilterPair> FILTER_OPTIONS_GROUP;
    public static List<String> SELECTION_ARGS;

    public static List<String> SPECS_FILTER;

    static
    {
        FILTER_OPTIONS_GROUP = new LinkedHashMap<>();
        FILTER_OPTIONS = new LinkedHashMap<>();

        SELECTION_ARGS = new ArrayList<>();
        SELECTION_ARGS.add(String.valueOf(8000));
        FILTER_OPTIONS_GROUP.put("Rs. 8000 and below", new FilterPair(Product.FILTER_PRICE_LESSER_THAN, SELECTION_ARGS));

        SELECTION_ARGS = new ArrayList<>();
        SELECTION_ARGS.add(String.valueOf(8001));
        SELECTION_ARGS.add(String.valueOf(20000));
        FILTER_OPTIONS_GROUP.put("Rs. 8001 - Rs. 20000", new FilterPair(Product.FILTER_PRICE_RANGE, SELECTION_ARGS));

        SELECTION_ARGS = new ArrayList<>();
        SELECTION_ARGS.add(String.valueOf(20001));
        SELECTION_ARGS.add(String.valueOf(40000));
        FILTER_OPTIONS_GROUP.put("Rs. 20001 - Rs. 40000", new FilterPair(Product.FILTER_PRICE_RANGE, SELECTION_ARGS));

        SELECTION_ARGS = new ArrayList<>();
        SELECTION_ARGS.add(String.valueOf(40001));
        FILTER_OPTIONS_GROUP.put("Rs. 40001 and above", new FilterPair(Product.FILTER_PRICE_GREATER_THAN, SELECTION_ARGS));

        FILTER_OPTIONS.put("Price", FILTER_OPTIONS_GROUP);

        SPECS_FILTER = new ArrayList<>();
        SPECS_FILTER.add("SIM Type");
        SPECS_FILTER.add("OS");
        SPECS_FILTER.add("Screen Size");
        SPECS_FILTER.add("Internal Storage");

    }

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
