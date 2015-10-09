package com.muhil.zohokart.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.muhil.zohokart.models.Account;
import com.muhil.zohokart.models.Cart;
import com.muhil.zohokart.models.Category;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.models.PromotionBanner;
import com.muhil.zohokart.models.SubCategory;
import com.muhil.zohokart.models.Wishlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "zohokart";
    public static int DATABASE_VERSION = 1;

    public static String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + Category.TABLE_NAME + " ( " + Category._ID + " INTEGER PRIMARY KEY NOT NULL, " + Category.NAME + " TEXT )";
    public static String CREATE_SUB_CATEGORIES_TABLE = "CREATE TABLE " + SubCategory.TABLE_NAME + " ( " + SubCategory._ID + " INTEGER PRIMARY KEY NOT NULL, " + SubCategory.CATEGORY_ID + " INTEGER, " + SubCategory.NAME + " TEXT )";
    public static String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + Product.TABLE_NAME + " ( " + Product._ID + " INTEGER, " + Product.SUB_CATEGORY_ID + " INTEGER, " + Product.BRAND + " TEXT, " + Product.TITLE + " TEXT, " + Product.DESCRIPTION + " TEXT, " + Product.THUMBNAIL + " TEXT, " + Product.PRICE + " REAL, " + Product.STARS + " REAL, " + Product.RATINGS + " INTEGER, PRIMARY KEY ( " + Product._ID + " , " + Product.SUB_CATEGORY_ID + " ))";
    public static String CREATE_ACCOUNTS_TABLE = "CREATE TABLE " + Account.TABLE_NAME + " ( " + Account.NAME + " TEXT, " + Account.EMAIL + " TEXT PRIMARY KEY, " + Account.PASSWORD + " TEXT, " + Account.PHONE_NUMBER + " TEXT, " + Account.DATE_OF_BIRTH + " DATE )";
    public static String CREATE_WISHLIST_TABLE = "CREATE TABLE " + Wishlist.TABLE_NAME + " ( " + Wishlist.PRODUCT_ID + " INTEGER, " + Wishlist.ADDED_ON + " DATETIME DEFAULT CURRENT_TIMESTAMP )";
    public static String CREATE_CART_TABLE = "CREATE TABLE " + Cart.TABLE_NAME + " ( " + Cart.PRODUCT_ID + " INTEGER, " + Cart.QUANTITY + " INTEGER DEFAULT 1, " + Cart.ADDED_ON + " DATETIME DEFAULT CURRENT_TIMESTAMP )";
    public static String CREATE_PROMOTION_BANNER_TABLE = "CREATE TABLE " + PromotionBanner.TABLE_NAME + " ( " + PromotionBanner._ID + " INTEGER, " + PromotionBanner.BANNER_URL + " TEXT, " + PromotionBanner.PRODUCTS_RELATED + " TEXT )";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DB", "Creating tables");
        db.execSQL(CREATE_CATEGORIES_TABLE);
        db.execSQL(CREATE_SUB_CATEGORIES_TABLE);
        db.execSQL(CREATE_PRODUCTS_TABLE);
        db.execSQL(CREATE_ACCOUNTS_TABLE);
        Log.d("DB", "accounts table created");
        db.execSQL(CREATE_WISHLIST_TABLE);
        db.execSQL(CREATE_CART_TABLE);
        db.execSQL(CREATE_PROMOTION_BANNER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    }

    public int numberOfRows() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(sqLiteDatabase, "categories");
    }

    public List<Category> getCategories() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select _id, name from categories", null);
        List<Category> categories = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            Category category = new Category(id, name);
            categories.add(category);
        }
        cursor.close();
        return categories;
    }

    public Map<Integer, List<SubCategory>> getSubCategoriesByCategory() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select _id, category_id, name from sub_categories", null);
        Map<Integer, List<SubCategory>> subCategoriesBycategory = new HashMap<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            int categoryId = cursor.getInt(1);
            String name = cursor.getString(2);
            SubCategory subCategory = new SubCategory(id, categoryId, name);
            if (subCategoriesBycategory.get(categoryId) != null) {
                subCategoriesBycategory.get(categoryId).add(subCategory);
            } else {
                List<SubCategory> subCategories = new ArrayList<>();
                subCategories.add(subCategory);
                subCategoriesBycategory.put(categoryId, subCategories);
            }

        }
        cursor.close();
        return subCategoriesBycategory;
    }

    public List<Product> getProductsForSubCategory(int subCategoryId) {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select _id, sub_category_id, brand, title, description, thumbnail," +
                        " price, stars, ratings from products where sub_category_id = ? ",
                new String[]{String.valueOf(subCategoryId)});
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            int categoryId = cursor.getInt(1);
            String brand = cursor.getString(2);
            String title = cursor.getString(3);
            String description = cursor.getString(4);
            String thumbnail = cursor.getString(5);
            double price = cursor.getDouble(6);
            double stars = cursor.getDouble(7);
            Log.d("STARS", String.valueOf(stars));
            int ratings = cursor.getInt(8);

            Product product = new Product(id, categoryId, brand, title, description, thumbnail, price, stars, ratings);
            products.add(product);
        }
        cursor.close();
        return products;
    }

    public List<Product> getProductsFromWishList() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from wishlist order by added_on asc", null);
        Log.d("WISHLIST_CURSOR", String.valueOf(cursor.getCount()));
        List<Integer> productsInWishList = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            productsInWishList.add(id);
        }
        if (!productsInWishList.isEmpty()) {
            for (Integer productId : productsInWishList) {
                cursor = sqLiteDatabase.rawQuery("select _id, sub_category_id, brand, title, description, thumbnail, " +
                        "price, stars, ratings from products where _id = ?", new String[]{String.valueOf(productId)});
                Log.d("WISHLIST_CURSOR", String.valueOf(cursor.getCount()));
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(0);
                    int categoryId = cursor.getInt(1);
                    String brand = cursor.getString(2);
                    String title = cursor.getString(3);
                    String description = cursor.getString(4);
                    String thumbnail = cursor.getString(5);
                    double price = cursor.getDouble(6);
                    double stars = cursor.getDouble(7);
                    int ratings = cursor.getInt(8);

                    Product product = new Product(id, categoryId, brand, title, description, thumbnail, price, stars, ratings);
                    products.add(product);
                }

            }
        } else {
            return null;
        }
        cursor.close();
        return products;
    }

    public List<Product> getProductsFromCart() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from cart order by added_on asc", null);
        Log.d("CART_CURSOR", String.valueOf(cursor.getCount()));
        List<Integer> productsInCart = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            productsInCart.add(id);
        }
        if (!productsInCart.isEmpty()) {
            for (Integer productId : productsInCart) {
                cursor = sqLiteDatabase.rawQuery("select _id, category_id, brand, title, description, thumbnail, " +
                        "price, stars, ratings from products where _id = ?", new String[]{String.valueOf(productId)});
                Log.d("CART_CURSOR", String.valueOf(cursor.getCount()));
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(0);
                    int categoryId = cursor.getInt(1);
                    String brand = cursor.getString(2);
                    String title = cursor.getString(3);
                    String description = cursor.getString(4);
                    String thumbnail = cursor.getString(5);
                    double price = cursor.getDouble(6);
                    double stars = cursor.getDouble(7);
                    int ratings = cursor.getInt(8);

                    Product product = new Product(id, categoryId, brand, title, description, thumbnail, price, stars, ratings);
                    products.add(product);
                }

            }
        } else {
            return null;
        }
        cursor.close();
        return products;
    }


    public List<Product> getProductsForProductIds(List<Integer> productIds) {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        if (!productIds.isEmpty()) {
            for (Integer productId : productIds) {
                Cursor cursor = sqLiteDatabase.rawQuery("select _id, category_id, brand, title, description, thumbnail, " +
                        "price, stars, ratings from products where _id = ?", new String[]{String.valueOf(productId)});
                Log.d("CART_CURSOR", String.valueOf(cursor.getCount()));
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(0);
                    int categoryId = cursor.getInt(1);
                    String brand = cursor.getString(2);
                    String title = cursor.getString(3);
                    String description = cursor.getString(4);
                    String thumbnail = cursor.getString(5);
                    double price = cursor.getDouble(6);
                    double stars = cursor.getDouble(7);
                    int ratings = cursor.getInt(8);

                    Product product = new Product(id, categoryId, brand, title, description, thumbnail, price, stars, ratings);
                    products.add(product);
                }
                cursor.close();
            }
        } else {
            return null;
        }
        return products;
    }
}
