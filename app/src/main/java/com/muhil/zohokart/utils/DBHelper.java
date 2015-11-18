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
import com.muhil.zohokart.models.Order;
import com.muhil.zohokart.models.OrderLineItem;
import com.muhil.zohokart.models.PaymentCard;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.models.ProductGallery;
import com.muhil.zohokart.models.PromotionBanner;
import com.muhil.zohokart.models.SubCategory;
import com.muhil.zohokart.models.Wishlist;
import com.muhil.zohokart.models.specification.Specification;
import com.muhil.zohokart.models.specification.SpecificationGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "zohokart";
    public static int DATABASE_VERSION = 1;

    public static String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + Category.TABLE_NAME + " ( " + Category._ID + " INTEGER PRIMARY KEY NOT NULL, " + Category.NAME + " TEXT )";
    public static String CREATE_SUB_CATEGORIES_TABLE = "CREATE TABLE " + SubCategory.TABLE_NAME + " ( " + SubCategory._ID + " INTEGER PRIMARY KEY NOT NULL, " + SubCategory.CATEGORY_ID + " INTEGER, " + SubCategory.NAME + " TEXT )";
    public static String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + Product.TABLE_NAME + " ( " + Product._ID + " INTEGER, " + Product.SUB_CATEGORY_ID + " INTEGER, " + Product.BRAND + " TEXT, " + Product.TITLE + " TEXT, " + Product.DESCRIPTION + " TEXT, " + Product.THUMBNAIL + " TEXT, " + Product.PRICE + " REAL, " + Product.STARS + " REAL, " + Product.RATINGS + " INTEGER, " + Product.WARRANTY +" TEXT, PRIMARY KEY ( " + Product._ID + " , " + Product.SUB_CATEGORY_ID + " ))";
    public static String CREATE_ACCOUNTS_TABLE = "CREATE TABLE " + Account.TABLE_NAME + " ( " + Account.NAME + " TEXT, " + Account.EMAIL + " TEXT PRIMARY KEY, " + Account.PASSWORD + " TEXT, " + Account.PHONE_NUMBER + " TEXT, " + Account.DATE_OF_BIRTH + " DATE, " + Account.DELIVERY_ADDRESS + " TEXT )";
    public static String CREATE_WISHLIST_TABLE = "CREATE TABLE " + Wishlist.TABLE_NAME + " ( " + Wishlist.EMAIL + " TEXT, " + Wishlist.PRODUCT_ID + " INTEGER, " + Wishlist.ADDED_ON + " DATETIME DEFAULT CURRENT_TIMESTAMP )";
    public static String CREATE_CART_TABLE = "CREATE TABLE " + Cart.TABLE_NAME + " ( " + Cart.EMAIL + " TEXT, " + Cart.PRODUCT_ID + " INTEGER, " + Cart.QUANTITY + " INTEGER DEFAULT 1, " + Cart.ADDED_ON + " DATETIME DEFAULT CURRENT_TIMESTAMP )";
    public static String CREATE_SPECIFICATION_TABLE = "CREATE TABLE " + SpecificationGroup.TABLE_NAME + " ( " + SpecificationGroup.PRODUCT_ID + " INTEGER, " + SpecificationGroup.GROUP_NAME + " TEXT, " + SpecificationGroup.SPECIFICATIONS + " TEXT )";
    public static String CREATE_PROMOTION_BANNER_TABLE = "CREATE TABLE " + PromotionBanner.TABLE_NAME + " ( " + PromotionBanner._ID + " INTEGER, " + PromotionBanner.BANNER_URL + " TEXT, " + PromotionBanner.PRODUCTS_RELATED + " TEXT )";
    public static String CREATE_PAYMENT_CARDS_TABLE = "CREATE TABLE " + PaymentCard.TABLE_NAME + " ( " + PaymentCard.EMAIL + " TEXT, " + PaymentCard.CARD_NUMBER + " TEXT, " + PaymentCard.CARD_TYPE + " TEXT, " + PaymentCard.NAME_ON_CARD + " TEXT, " + PaymentCard.EXPIRY + " TEXT )";
    public static String CREATE_ORDERS_TABLE = "CREATE TABLE " + Order.TABLE_NAME + " ( " + Order._ID + " TEXT, " + Order.EMAIL + " TEXT, " + Order.ADDED_ON + " DATETIME DEFAULT CURRENT_TIMESTAMP, " + Order.EXPECTED_DELIVERY_DATE + " TEXT, " + Order.NUMBER_OF_PRODUCTS + " INTEGER, " + Order.TOTAL_PRICE + " REAL, " + Order.ORDER_STATUS + " TEXT )";
    public static String CREATE_ORDER_LINE_ITEM_TABLE = "CREATE TABLE " + OrderLineItem.TABLE_NAME + " ( " + OrderLineItem.ORDER_ID + " TEXT, " + OrderLineItem.PRODUCT_ID + " INTEGER, " + OrderLineItem.QUANTITY + " INTEGER )";
    public static String CREATE_PRODUCT_GALLERIES_TABLE = "CREATE TABLE " + ProductGallery.TABLE_NAME + " ( " + ProductGallery.PRODUCT_ID + " INTEGER, " + ProductGallery.IMAGE_LINKS + " TEXT)";

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
        db.execSQL(CREATE_WISHLIST_TABLE);
        db.execSQL(CREATE_CART_TABLE);
        db.execSQL(CREATE_SPECIFICATION_TABLE);
        db.execSQL(CREATE_PROMOTION_BANNER_TABLE);
        db.execSQL(CREATE_PAYMENT_CARDS_TABLE);
        db.execSQL(CREATE_ORDERS_TABLE);
        db.execSQL(CREATE_ORDER_LINE_ITEM_TABLE);
        db.execSQL(CREATE_PRODUCT_GALLERIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    }

    public int numberOfRows() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(sqLiteDatabase, "categories");
    }
}
