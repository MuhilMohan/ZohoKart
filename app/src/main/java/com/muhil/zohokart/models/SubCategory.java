package com.muhil.zohokart.models;


import android.content.ContentResolver;
import android.net.Uri;

import com.google.gson.annotations.SerializedName;
import com.muhil.zohokart.utils.ZohokartContentProvider;

public class SubCategory {

    public static final String TABLE_NAME = "sub_categories";
    public static final String _ID = "_id";
    public static final String CATEGORY_ID = "category_id";
    public static final String NAME = "name";

    public static final String[] PROJECTION = {_ID, CATEGORY_ID, NAME};

    public static final Uri CONTENT_URI = Uri.withAppendedPath(ZohokartContentProvider.CONTENT_URI, TABLE_NAME);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/com.muhil.zohokart.models.SubCategory";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/com.muhil.zohokart.models.SubCategory";

    private int id;
    @SerializedName("category_id")
    private int categoryId;
    private String name;

    public SubCategory(int id, int categoryId, String name) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SubCategory{" +
                "id=" + id +
                ", categoryId=" + categoryId +
                ", name='" + name + '\'' +
                '}';
    }
}
