package com.muhil.zohokart.models;

import android.content.ContentResolver;
import android.net.Uri;

import com.muhil.zohokart.utils.ZohokartContentProvider;

public class Product {

    public static final String TABLE_NAME = "products";
    public static final String _ID = "_id";
    public static final String SUB_CATEGORY_ID = "sub_category_id";
    public static final String BRAND = "brand";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String THUMBNAIL = "thumbnail";
    public static final String PRICE = "price";
    public static final String STARS = "stars";
    public static final String RATINGS = "ratings";

    public static final String[] PROJECTION = {_ID, SUB_CATEGORY_ID, BRAND, TITLE, DESCRIPTION, THUMBNAIL, PRICE, STARS, RATINGS};

    public static final Uri CONTENT_URI = Uri.withAppendedPath(ZohokartContentProvider.CONTENT_URI, TABLE_NAME);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/com.muhil.zohokart.models.Product";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/com.muhil.zohokart.models.Product";

    private int id;
    private int subCategoryId;
    private String brand;
    private String title;
    private String description;
    private String thumbnail;
    private double price;
    private double stars;
    private int ratings;

    public Product(int id, int subCategoryId, String brand, String title, String description, String thumbnail, double price, double stars, int ratings) {
        this.id = id;
        this.subCategoryId = subCategoryId;
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
                ", categoryId=" + subCategoryId +
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

    public int getSubCategoryId() {
        return subCategoryId;
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
