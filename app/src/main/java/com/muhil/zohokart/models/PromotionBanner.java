package com.muhil.zohokart.models;

import android.content.ContentResolver;
import android.net.Uri;

import com.google.gson.annotations.SerializedName;
import com.muhil.zohokart.utils.ZohokartContentProvider;

import java.io.Serializable;
import java.util.List;

public class PromotionBanner implements Serializable
{

    public static final String TABLE_NAME = "promotion_banners";
    public static final String _ID = "_id";
    public static final String BANNER_URL = "banner_url";
    public static final String PRODUCTS_RELATED = "products_related";

    public static final String[] PROJECTION = {_ID, BANNER_URL, PRODUCTS_RELATED};

    public static final Uri CONTENT_URI = Uri.withAppendedPath(ZohokartContentProvider.CONTENT_URI, TABLE_NAME);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/com.muhil.zohokart.models.PromotionBanner";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/com.muhil.zohokart.models.PromotionBanner";

    private int id;
    private String banner;
    @SerializedName("product_ids")
    private List<Integer> productIds;

    public PromotionBanner() {
    }

    public PromotionBanner(int id, String banner, List<Integer> productIds) {
        this.id = id;
        this.banner = banner;
        this.productIds = productIds;
    }

    public int getId() {
        return id;
    }

    public String getBanner () {
        return banner;
    }

    public List<Integer> getProductIds() {
        return productIds;
    }
}
