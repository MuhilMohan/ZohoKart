package com.muhil.zohokart.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PromotionBanner {
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

    public String getBanner() {
        return banner;
    }

    public List<Integer> getProductIds() {
        return productIds;
    }
}
