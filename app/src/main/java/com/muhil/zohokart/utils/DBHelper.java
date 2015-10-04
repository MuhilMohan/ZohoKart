package com.muhil.zohokart.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.muhil.zohokart.models.Account;
import com.muhil.zohokart.models.Category;
import com.muhil.zohokart.models.SubCategory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "zohocart", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DB", "Creating tables");
        db.execSQL("create table categories (_id integer primary key not null, name text)");
        db.execSQL("create table sub_categories (_id integer primary key not null, category_id integer, name text)");
        db.execSQL("create table accounts(name text, email text not null primary key, password text, phone_number text, date_of_birth date)");
        Log.d("DB", "accounts table created");
        db.execSQL("create table wishlist (_id integer not null primary key)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DB", "Dropping tables");
        db.execSQL("drop table if exists categories");
        db.execSQL("drop table if exists sub_categories");
    }

    public int addCategories(List<Category> categories) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        for (Category category : categories) {
            ContentValues contentValue = new ContentValues();
            contentValue.put("_id", category.getId());
            contentValue.put("name", category.getName());
            sqLiteDatabase.insert("categories", null, contentValue);
        }
        sqLiteDatabase = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(sqLiteDatabase, "categories");
    }

    public int addSubCategories(List<SubCategory> subCategories) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        for (SubCategory subCategory : subCategories) {
            ContentValues contentValue = new ContentValues();
            contentValue.put("_id", subCategory.getId());
            contentValue.put("category_id", subCategory.getCategoryId());
            contentValue.put("name", subCategory.getName());
            sqLiteDatabase.insert("sub_categories", null, contentValue);
        }
        sqLiteDatabase = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(sqLiteDatabase, "sub_categories");
    }

    public boolean hasAccount(String email){

        boolean result = false;

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from accounts where email = '"+email+"'", null);
        if(cursor.moveToNext()){
            result = true;
        }
        else {
            result = false;
        }

        return result;
    }

    public boolean addAccount(Account account){

        boolean result = false;
        ContentValues contentValues = new ContentValues();

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        contentValues.put("name", account.getName());
        contentValues.put("email", account.getEmail());
        contentValues.put("password", account.getPassword());
        contentValues.put("phone_number", account.getPhoneNumber());
        contentValues.put("date_of_birth", account.getDateOfBirth());
        sqLiteDatabase.insert("accounts", null, contentValues);

        Cursor cursor = sqLiteDatabase.rawQuery("select * from accounts where email = '" + account.getEmail() +"'", null);
        if(cursor.moveToNext()){
            result = true;
        }
        else {
            result = false;
        }
        cursor.close();
        return result;

    }

    public Account getAccountIfAvailable(String email, String password){

        Account account = new Account();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from accounts where email = '"+email+"' and password = '"+password+"'", null);
        if(cursor.getCount() == 1){

            while (cursor.moveToNext()){
                account.setName(cursor.getString(0));
                account.setEmail(email);
                account.setPassword(password);
                account.setPhoneNumber(cursor.getString(3));
                account.setDateOfBirth(cursor.getString(4));
            }

        }
        else {
            return null;
        }

        cursor.close();
        return account;

    }

    public boolean checkWishlist(int productId){

        boolean result;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from wishlist where _id = "+productId, null);
        if(cursor.getCount() == 1){
            result = true;
        }
        else {
            result = false;
        }
        return result;

    }

    public boolean addToWishlist(int productId){

        ContentValues contentValues = new ContentValues();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        contentValues.put("_id", productId);
        Long rowID = sqLiteDatabase.insert("wishlist", null, contentValues);
        if(rowID != -1){
            return true;
        }
        else {
            return false;
        }

    }

    public boolean removeFromWishList(int productId){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        int rowsAffected = sqLiteDatabase.delete("wishlist", "_id = " + productId, null);
        if(rowsAffected != -1){
            return true;
        }
        else {
            return false;
        }

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
}
