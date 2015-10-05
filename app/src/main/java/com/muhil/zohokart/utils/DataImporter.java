package com.muhil.zohokart.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muhil.zohokart.models.Category;
import com.muhil.zohokart.models.Mobile;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.models.PromotionBanner;
import com.muhil.zohokart.models.SubCategory;
import com.muhil.zohokart.models.specification.SpecificationGroup;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataImporter {

    Context context;
    DBHelper dbHelper;

    public DataImporter(Context context) {
        this.context = context;
    }

    @SuppressLint("NewApi")
    private String getJsonContentAsString(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream = context.getAssets().open(fileName + ".json");
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (Exception e) {
            Log.e("DATA", e.getMessage());
        }
        return stringBuilder.toString();
    }

    public void importData() {
        Gson gson = new Gson();
        dbHelper = new DBHelper(context);
        int recordsInDatabase = dbHelper.numberOfRows();
        if (recordsInDatabase <= 0) {
            String categoriesAsString = getJsonContentAsString("metadata/categories");
            List<Category> categories = gson.fromJson(categoriesAsString, new TypeToken<List<Category>>() {
            }.getType());

            Log.d("JSON", "Number of categories from JSON = " + categories.size());
            int records = dbHelper.addCategories(categories);
            Log.d("DB", "Number of categories in DB = " + records);

            String subCategoriesAsString = getJsonContentAsString("metadata/sub-categories");
            List<SubCategory> subCategories = gson.fromJson(subCategoriesAsString,
                    new TypeToken<List<SubCategory>>() {
                    }.getType());

            Log.d("JSON", "Number of sub_categories from JSON = " + categories.size());
            records = dbHelper.addSubCategories(subCategories);
            Log.d("DB", "Number of sub_categories in DB = " + records);

            String mobilesAsString = getJsonContentAsString("products/mobiles");
            List<Mobile> mobiles = gson.fromJson(mobilesAsString, new TypeToken<List<Mobile>>() {
            }.getType());

            Log.d("JSON", "Number of mobiles from JSON = " + mobiles.size());
            List<Product> products = new ArrayList<>();
            for (Mobile mobile : mobiles) {
                products.add(mobile.getProduct());
            }
            records = dbHelper.addProducts(products);
            Log.d("DB", "Number of products in DB = " + records);

        } else {
            Log.d("DB", "Data already imported");
        }

        String specificationsAsString = getJsonContentAsString("specifications");
        Map<String, List<SpecificationGroup>> specifications = gson.fromJson(specificationsAsString, new TypeToken<Map<String, List<SpecificationGroup>>>() {
        }.getType());

        for (Map.Entry<String, List<SpecificationGroup>> entry : specifications.entrySet()) {
            DataHolder.specifications.put(entry.getKey(), entry.getValue());
        }

        Log.d("JSON", "Number of product specfications = " + DataHolder.specifications.size());

        String promotionBannersAsString = getJsonContentAsString("promotion_banners");
        List<PromotionBanner> promotionBanners = gson.fromJson(promotionBannersAsString, new TypeToken<List<PromotionBanner>>() {
        }.getType());

        for (PromotionBanner promotionBanner : promotionBanners) {
            DataHolder.promotionBanners.add(promotionBanner);
        }

        Log.d("JSON", "Number of promotion banners = " + DataHolder.promotionBanners.size());
    }
}
