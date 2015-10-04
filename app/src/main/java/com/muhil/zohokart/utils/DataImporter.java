package com.muhil.zohokart.utils;


import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muhil.zohokart.models.Category;
import com.muhil.zohokart.models.SubCategory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class DataImporter {

    Context context;
    DBHelper dbHelper;

    public DataImporter(Context context) {
        this.context = context;
    }

    private String getJsonContentAsString(String fileName) {
        StringBuffer stringBuffer = new StringBuffer();
        try (InputStream inputStream = context.getAssets().open(fileName + ".json");
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
        } catch (Exception e) {
            Log.e("DATA", e.getMessage());
        }
        return stringBuffer.toString();
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
        } else {
            Log.d("DB", "Data already imported");
        }

    }
}
