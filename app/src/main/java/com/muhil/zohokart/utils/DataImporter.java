package com.muhil.zohokart.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muhil.zohokart.models.Book;
import com.muhil.zohokart.models.Category;
import com.muhil.zohokart.models.Furniture;
import com.muhil.zohokart.models.Kitchen;
import com.muhil.zohokart.models.Laptop;
import com.muhil.zohokart.models.Mobile;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.models.PromotionBanner;
import com.muhil.zohokart.models.SubCategory;
import com.muhil.zohokart.models.Tablet;
import com.muhil.zohokart.models.specification.SpecificationGroup;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataImporter
{

    Context context;
    DBHelper dbHelper;
    ZohokartDAO zohokartDAO;

    public DataImporter(Context context) {
        this.context = context;
    }

    @SuppressLint("NewApi")
    private String getJsonContentAsString(String fileName)
    {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream = context.getAssets().open(fileName + ".json");
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream)))
        {
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(line);
            }
        }
        catch (Exception e)
        {
            Log.e("DATA", e.getMessage());
        }
        return stringBuilder.toString();
    }

    public void importData()
    {
        Gson gson = new Gson();
        int records;
        dbHelper = new DBHelper(context);
        zohokartDAO = new ZohokartDAO(context);
        int recordsInDatabase = dbHelper.numberOfRows();
        if (recordsInDatabase <= 0)
        {
            String categoriesAsString = getJsonContentAsString("metadata/categories");
            List<Category> categories = gson.fromJson(categoriesAsString, new TypeToken<List<Category>>() {}.getType());

            Log.d("JSON", "Number of categories from JSON = " + categories.size());
            records = zohokartDAO.addCategories(categories);
            Log.d("DB", "Number of categories in DB = " + records);

            String subCategoriesAsString = getJsonContentAsString("metadata/sub-categories");
            List<SubCategory> subCategories = gson.fromJson(subCategoriesAsString, new TypeToken<List<SubCategory>>() {}.getType());

            Log.d("JSON", "Number of sub_categories from JSON = " + subCategories.size());
            records = zohokartDAO.addSubCategories(subCategories);
            Log.d("DB", "Number of sub_categories in DB = " + records);

            String mobilesAsString = getJsonContentAsString("products/mobiles");
            List<Mobile> mobiles = gson.fromJson(mobilesAsString, new TypeToken<List<Mobile>>() {}.getType());

            Log.d("JSON", "Number of mobiles from JSON = " + mobiles.size());
            List<Product> products = new ArrayList<>();
            for (Mobile mobile : mobiles)
            {
                products.add(mobile.getProduct());
            }
            records = zohokartDAO.addProducts(products);
            products.clear();
            Log.d("DB", "Number of products in DB = " + records);

            String tabletsAsString = getJsonContentAsString("products/tablets");
            List<Tablet> tablets = gson.fromJson(tabletsAsString, new TypeToken<List<Tablet>>() {}.getType());

            Log.d("JSON", "Number of tablets from JSON = " + tablets.size());
            for (Tablet tablet : tablets)
            {
                products.add(tablet.getProduct());
            }
            records = zohokartDAO.addProducts(products);
            products.clear();
            Log.d("DB", "Number of products in DB = " + records);

            String laptopsAsString = getJsonContentAsString("products/laptops");
            List<Laptop> laptops = gson.fromJson(laptopsAsString, new TypeToken<List<Laptop>>() {}.getType());

            Log.d("JSON", "Number of laptops from JSON = " + laptops.size());
            for (Laptop laptop : laptops)
            {
                products.add(laptop.getProduct());
            }
            records = zohokartDAO.addProducts(products);
            products.clear();
            Log.d("DB", "Number of products in DB = " + records);

            String fictionBooksAsString = getJsonContentAsString("products/books_fiction");
            List<Book> books = gson.fromJson(fictionBooksAsString, new TypeToken<List<Book>>() {}.getType());

            Log.d("JSON", "Number of ficBooks from JSON = " + books.size());
            for (Book book : books)
            {
                products.add(book.getProduct());
            }
            records = zohokartDAO.addProducts(products);
            products.clear();
            books.clear();
            Log.d("DB", "Number of products in DB = " + records);

            String romanceBooksAsString = getJsonContentAsString("products/books_romance");
            books = gson.fromJson(romanceBooksAsString, new TypeToken<List<Book>>() {}.getType());

            Log.d("JSON", "Number of romanceBooks from JSON = " + books.size());
            for (Book book : books)
            {
                products.add(book.getProduct());
            }
            records = zohokartDAO.addProducts(products);
            products.clear();
            books.clear();
            Log.d("DB", "Number of products in DB = " + records);

            String furnituresString = getJsonContentAsString("products/furnitures");
            List<Furniture> furnitures = gson.fromJson(furnituresString, new TypeToken<List<Furniture>>()
            {
            }.getType());

            Log.d("JSON", "Number of furnitures from JSON = " + furnitures.size());
            for (Furniture furniture : furnitures)
            {
                products.add(furniture.getProduct());
            }
            records = zohokartDAO.addProducts(products);
            products.clear();
            furnitures.clear();
            Log.d("DB", "Number of products in DB = " + records);

            String kitchenString = getJsonContentAsString("products/kitchen");
            List<Kitchen> kitchens = gson.fromJson(kitchenString, new TypeToken<List<Kitchen>>() {}.getType());

            Log.d("JSON", "Number of kitchens from JSON = " + kitchens.size());
            for (Kitchen kitchen : kitchens)
            {
                products.add(kitchen.getProduct());
            }
            records = zohokartDAO.addProducts(products);
            products.clear();
            kitchens.clear();
            Log.d("DB", "Number of products in DB = " + records);

            String promotionBannersAsString = getJsonContentAsString("promotion_banners");
            List<PromotionBanner> promotionBanners = gson.fromJson(promotionBannersAsString, new TypeToken<List<PromotionBanner>>() {}.getType());

            Log.d("JSON", "Number of promotion banners = " + promotionBanners.size());

            records = zohokartDAO.addBanners(promotionBanners);

            Log.d("JSON", "Number of promotion banners in db = " + records);

            String specificationsAsString = getJsonContentAsString("specifications");
            Map<String, List<SpecificationGroup>> specifications = gson.fromJson(specificationsAsString, new TypeToken<Map<String, List<SpecificationGroup>>>() {
            }.getType());

            records = zohokartDAO.addSpecifications(specifications);

            Log.d("JSON", "Number of product specfications = " + records);

        }
        else
        {
            Log.d("DB", "Data already imported");
        }

    }
}
