package com.muhil.zohokart.utils;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.muhil.zohokart.models.Account;
import com.muhil.zohokart.models.Cart;
import com.muhil.zohokart.models.Category;
import com.muhil.zohokart.models.PaymentCard;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.models.PromotionBanner;
import com.muhil.zohokart.models.SubCategory;
import com.muhil.zohokart.models.Wishlist;
import com.muhil.zohokart.models.specification.SpecificationGroup;

/**
 * Created by muhil-ga42 on 28/10/15.
 */
public class ZohokartContentProvider extends ContentProvider
{

    public static final String AUTHORITY = "com.muhil.zohokart.utils";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    private static final int CATEGORIES = 1;
    private static final int CATEGORIES_ID = 2;
    private static final int SUB_CATEGORIES = 3;
    private static final int SUB_CATEGORIES_ID = 4;
    private static final int PRODUCTS = 5;
    private static final int PRODUCTS_ID = 6;
    private static final int ACCOUNTS = 7;
    private static final int ACCOUNTS_EMAIL = 8;
    private static final int WISHLIST = 9;
    private static final int WISHLIST_ID = 10;
    private static final int CART = 11;
    private static final int CART_ID = 12;
    private static final int SPECIFICATION = 13;
    private static final int SPECIFICATION_ID = 14;
    private static final int PROMOTION_BANNERS = 15;
    private static final int PROMOTION_BANNERS_ID = 16;
    private static final int PAYMENT_CARDS = 17;
    private static final int PAYMENT_CARDS_EMAIL = 18;
    private static final UriMatcher uriMatcher;

    static
    {

        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, Category.TABLE_NAME, CATEGORIES);
        uriMatcher.addURI(AUTHORITY, Category.TABLE_NAME + "/#", CATEGORIES_ID);
        uriMatcher.addURI(AUTHORITY, SubCategory.TABLE_NAME, SUB_CATEGORIES);
        uriMatcher.addURI(AUTHORITY, SubCategory.TABLE_NAME + "/#", SUB_CATEGORIES_ID);
        uriMatcher.addURI(AUTHORITY, Product.TABLE_NAME, PRODUCTS);
        uriMatcher.addURI(AUTHORITY, Product.TABLE_NAME + "/#", PRODUCTS_ID);
        uriMatcher.addURI(AUTHORITY, Account.TABLE_NAME, ACCOUNTS);
        uriMatcher.addURI(AUTHORITY, Account.TABLE_NAME + "/*", ACCOUNTS_EMAIL);
        uriMatcher.addURI(AUTHORITY, Wishlist.TABLE_NAME, WISHLIST);
        uriMatcher.addURI(AUTHORITY, Wishlist.TABLE_NAME + "/#", WISHLIST_ID);
        uriMatcher.addURI(AUTHORITY, Cart.TABLE_NAME, CART);
        uriMatcher.addURI(AUTHORITY, Cart.TABLE_NAME + "/#", CART_ID);
        uriMatcher.addURI(AUTHORITY, SpecificationGroup.TABLE_NAME, SPECIFICATION);
        uriMatcher.addURI(AUTHORITY, SpecificationGroup.TABLE_NAME + "/#", SPECIFICATION_ID);
        uriMatcher.addURI(AUTHORITY, PromotionBanner.TABLE_NAME, PROMOTION_BANNERS);
        uriMatcher.addURI(AUTHORITY, PromotionBanner.TABLE_NAME + "/#", PROMOTION_BANNERS_ID);
        uriMatcher.addURI(AUTHORITY, PaymentCard.TABLE_NAME, PAYMENT_CARDS);
        uriMatcher.addURI(AUTHORITY, PaymentCard.TABLE_NAME + "/*", PAYMENT_CARDS_EMAIL);

    }

    DBHelper dbHelper = null;

    @Override
    public boolean onCreate()
    {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {

        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        boolean useAuthorityUri = false;

        switch (uriMatcher.match(uri))
        {

            case CATEGORIES:
                sqLiteQueryBuilder.setTables(Category.TABLE_NAME);
                break;
            case CATEGORIES_ID:
                sqLiteQueryBuilder.setTables(Category.TABLE_NAME);
                sqLiteQueryBuilder.appendWhere(Category._ID + " = " + uri.getLastPathSegment());
                break;

            case SUB_CATEGORIES:
                sqLiteQueryBuilder.setTables(SubCategory.TABLE_NAME);
                break;
            case SUB_CATEGORIES_ID:
                sqLiteQueryBuilder.setTables(SubCategory.TABLE_NAME);
                sqLiteQueryBuilder.appendWhere(SubCategory._ID + " = " + uri.getLastPathSegment());
                break;

            case PRODUCTS:
                sqLiteQueryBuilder.setTables(Product.TABLE_NAME);
                break;
            case PRODUCTS_ID:
                sqLiteQueryBuilder.setTables(Product.TABLE_NAME);
                sqLiteQueryBuilder.appendWhere(Product._ID + " = " + uri.getLastPathSegment());
                break;

            case ACCOUNTS:
                sqLiteQueryBuilder.setTables(Account.TABLE_NAME);
                break;
            case ACCOUNTS_EMAIL:
                sqLiteQueryBuilder.setTables(Account.TABLE_NAME);
                sqLiteQueryBuilder.appendWhere(Account.EMAIL + " = '" + uri.getLastPathSegment() + "'");
                break;

            case WISHLIST:
                sqLiteQueryBuilder.setTables(Wishlist.TABLE_NAME);
                break;
            case WISHLIST_ID:
                sqLiteQueryBuilder.setTables(Wishlist.TABLE_NAME);
                sqLiteQueryBuilder.appendWhere(Wishlist.PRODUCT_ID + " = " + uri.getLastPathSegment());
                break;

            case CART:
                sqLiteQueryBuilder.setTables(Cart.TABLE_NAME);
                break;
            case CART_ID:
                sqLiteQueryBuilder.setTables(Cart.TABLE_NAME);
                sqLiteQueryBuilder.appendWhere(Cart.PRODUCT_ID + " = " + uri.getLastPathSegment());
                break;

            case SPECIFICATION:
                sqLiteQueryBuilder.setTables(SpecificationGroup.TABLE_NAME);
                break;
            case SPECIFICATION_ID:
                sqLiteQueryBuilder.setTables(SpecificationGroup.TABLE_NAME);
                sqLiteQueryBuilder.appendWhere(SpecificationGroup.PRODUCT_ID + " = " + uri.getLastPathSegment());
                break;

            case PROMOTION_BANNERS:
                sqLiteQueryBuilder.setTables(PromotionBanner.TABLE_NAME);
                break;
            case PROMOTION_BANNERS_ID:
                sqLiteQueryBuilder.setTables(PromotionBanner.TABLE_NAME);
                sqLiteQueryBuilder.appendWhere(PromotionBanner._ID + " = " + uri.getLastPathSegment());
                break;

            case PAYMENT_CARDS:
                sqLiteQueryBuilder.setTables(PaymentCard.TABLE_NAME);
                break;
            case PAYMENT_CARDS_EMAIL:
                sqLiteQueryBuilder.setTables(PaymentCard.TABLE_NAME);
                sqLiteQueryBuilder.appendWhere(PaymentCard.EMAIL + " = " + uri.getLastPathSegment());
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI : " + uri);

        }

        Cursor cursor = sqLiteQueryBuilder.query(sqLiteDatabase, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri)
    {

        switch (uriMatcher.match(uri))
        {

            case CATEGORIES:
                return Category.CONTENT_TYPE;

            case CATEGORIES_ID:
                return Category.CONTENT_ITEM_TYPE;

            case SUB_CATEGORIES:
                return SubCategory.CONTENT_TYPE;

            case SUB_CATEGORIES_ID:
                return SubCategory.CONTENT_ITEM_TYPE;

            case PRODUCTS:
                return Product.CONTENT_TYPE;

            case PRODUCTS_ID:
                return Product.CONTENT_ITEM_TYPE;

            case ACCOUNTS:
                return Account.CONTENT_TYPE;

            case ACCOUNTS_EMAIL:
                return Account.CONTENT_ITEM_TYPE;

            case WISHLIST:
                return Wishlist.CONTENT_TYPE;

            case WISHLIST_ID:
                return Wishlist.CONTENT_ITEM_TYPE;

            case CART:
                return Cart.CONTENT_TYPE;

            case CART_ID:
                return Cart.CONTENT_ITEM_TYPE;

            case SPECIFICATION:
                return SpecificationGroup.CONTENT_TYPE;

            case SPECIFICATION_ID:
                return SpecificationGroup.CONTENT_ITEM_TYPE;

            case PROMOTION_BANNERS:
                return PromotionBanner.CONTENT_TYPE;

            case PROMOTION_BANNERS_ID:
                return PromotionBanner.CONTENT_ITEM_TYPE;

            case PAYMENT_CARDS:
                return PaymentCard.CONTENT_TYPE;

            case PAYMENT_CARDS_EMAIL:
                return PaymentCard.CONTENT_ITEM_TYPE;

            default:
                return null;

        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues values)
    {

        if ((uriMatcher.match(uri) != CATEGORIES_ID) && (uriMatcher.match(uri) != SUB_CATEGORIES_ID) && (uriMatcher.match(uri) != PRODUCTS_ID) && (uriMatcher.match(uri) != ACCOUNTS_EMAIL)
                && (uriMatcher.match(uri) != WISHLIST_ID) && (uriMatcher.match(uri) != CART_ID) && (uriMatcher.match(uri) != SPECIFICATION_ID) && (uriMatcher.match(uri) != PROMOTION_BANNERS_ID)
                && (uriMatcher.match(uri) != PAYMENT_CARDS_EMAIL))
        {

            long result;
            SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
            switch (uriMatcher.match(uri))
            {

                case CATEGORIES:
                    result = sqLiteDatabase.insert(Category.TABLE_NAME, null, values);
                    break;

                case SUB_CATEGORIES:
                    result = sqLiteDatabase.insert(SubCategory.TABLE_NAME, null, values);
                    break;

                case PRODUCTS:
                    result = sqLiteDatabase.insert(Product.TABLE_NAME, null, values);
                    break;

                case ACCOUNTS:
                    result = sqLiteDatabase.insert(Account.TABLE_NAME, null, values);
                    break;

                case WISHLIST:
                    result = sqLiteDatabase.insert(Wishlist.TABLE_NAME, null, values);
                    break;

                case CART:
                    result = sqLiteDatabase.insert(Cart.TABLE_NAME, null, values);
                    break;

                case SPECIFICATION:
                    result = sqLiteDatabase.insert(SpecificationGroup.TABLE_NAME, null, values);
                    break;

                case PROMOTION_BANNERS:
                    result = sqLiteDatabase.insert(PromotionBanner.TABLE_NAME, null, values);
                    break;

                case PAYMENT_CARDS:
                    result = sqLiteDatabase.insert(PaymentCard.TABLE_NAME, null, values);
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported Uri : " + uri );

            }

            if (result > 0)
            {
                Uri resultUri = ContentUris.withAppendedId(uri, result);
                getContext().getContentResolver().notifyChange(resultUri, null);
                return resultUri;
            }
            else
            {
                throw new SQLException("Problem while inserting into uri: " + uri);
            }

        }
        else
        {
            throw new IllegalArgumentException("Unsupported Uri : " + uri );
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        int deleteCount = 0;
        String where;

        switch (uriMatcher.match(uri))
        {

            case CATEGORIES:
                deleteCount = sqLiteDatabase.delete(Category.TABLE_NAME, selection, selectionArgs);
                break;
            case CATEGORIES_ID:
                String categoryId = uri.getLastPathSegment();
                where = Category._ID + " = " + categoryId;
                if (!TextUtils.isEmpty(selection))
                {
                    where += " AND " + selection;
                }
                deleteCount = sqLiteDatabase.delete(Category.TABLE_NAME, where, selectionArgs);
                break;

            case SUB_CATEGORIES:
                deleteCount = sqLiteDatabase.delete(SubCategory.TABLE_NAME, selection, selectionArgs);
                break;
            case SUB_CATEGORIES_ID:
                String subCategoryId = uri.getLastPathSegment();
                where = SubCategory._ID + " = " + subCategoryId;
                if (!TextUtils.isEmpty(selection))
                {
                    where += " AND " + selection;
                }
                deleteCount = sqLiteDatabase.delete(SubCategory.TABLE_NAME, where, selectionArgs);
                break;

            case PRODUCTS:
                deleteCount = sqLiteDatabase.delete(Product.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCTS_ID:
                String productId = uri.getLastPathSegment();
                where = Product._ID + " = " + productId;
                if (!TextUtils.isEmpty(selection))
                {
                    where += " AND " + selection;
                }
                deleteCount = sqLiteDatabase.delete(Product.TABLE_NAME, where, selectionArgs);
                break;

            case ACCOUNTS:
                deleteCount = sqLiteDatabase.delete(Account.TABLE_NAME, selection, selectionArgs);
                break;
            case ACCOUNTS_EMAIL:
                String accountEmail = uri.getLastPathSegment();
                where = Account.EMAIL + " = " + accountEmail;
                if (!TextUtils.isEmpty(selection))
                {
                    where += " AND " + selection;
                }
                deleteCount = sqLiteDatabase.delete(Account.TABLE_NAME, where, selectionArgs);
                break;

            case WISHLIST:
                deleteCount = sqLiteDatabase.delete(Wishlist.TABLE_NAME, selection, selectionArgs);
                break;
            case WISHLIST_ID:
                String wishlistProductId = uri.getLastPathSegment();
                where = Wishlist.PRODUCT_ID + " = " + wishlistProductId;
                if (!TextUtils.isEmpty(selection))
                {
                    where += " AND " + selection;
                }
                deleteCount = sqLiteDatabase.delete(Wishlist.TABLE_NAME, where, selectionArgs);
                break;

            case CART:
                deleteCount = sqLiteDatabase.delete(Cart.TABLE_NAME, selection, selectionArgs);
                break;
            case CART_ID:
                String cartProductId = uri.getLastPathSegment();
                where = Cart.PRODUCT_ID + " = " + cartProductId;
                if (!TextUtils.isEmpty(selection))
                {
                    where += " AND " + selection;
                }
                deleteCount = sqLiteDatabase.delete(Cart.TABLE_NAME, where, selectionArgs);
                break;

            case PROMOTION_BANNERS:
                deleteCount = sqLiteDatabase.delete(PromotionBanner.TABLE_NAME, selection, selectionArgs);
                break;
            case PROMOTION_BANNERS_ID:
                String promotionBannerId = uri.getLastPathSegment();
                where = PromotionBanner._ID + " = " + promotionBannerId;
                if (!TextUtils.isEmpty(selection))
                {
                    where += " AND " + selection;
                }
                deleteCount = sqLiteDatabase.delete(PromotionBanner.TABLE_NAME, where, selectionArgs);
                break;

            case PAYMENT_CARDS:
                deleteCount = sqLiteDatabase.delete(PaymentCard.TABLE_NAME, selection, selectionArgs);
                break;
            case PAYMENT_CARDS_EMAIL:
                String paymentCardEmail = uri.getLastPathSegment();
                where = PaymentCard.EMAIL + " = '" + paymentCardEmail + "'";
                if (!TextUtils.isEmpty(selection))
                {
                    where += " AND " + selection;
                }
                deleteCount = sqLiteDatabase.delete(PaymentCard.TABLE_NAME, where, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI : " + uri);

        }

        if (deleteCount > 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {

        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        int updateCount;
        String where;

        switch (uriMatcher.match(uri))
        {

            case CATEGORIES:
                updateCount = sqLiteDatabase.update(Category.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CATEGORIES_ID:
                String categoryId = uri.getLastPathSegment();
                where = Category._ID + " = " + categoryId;
                if (!TextUtils.isEmpty(selection))
                {
                    where += " AND " + selection;
                }
                updateCount = sqLiteDatabase.update(Category.TABLE_NAME, values, where, selectionArgs);
                break;

            case SUB_CATEGORIES:
                updateCount = sqLiteDatabase.update(SubCategory.TABLE_NAME, values, selection, selectionArgs);
                break;
            case SUB_CATEGORIES_ID:
                String subCategoryId = uri.getLastPathSegment();
                where = SubCategory._ID + " = " + subCategoryId;
                if (!TextUtils.isEmpty(selection))
                {
                    where += " AND " + selection;
                }
                updateCount = sqLiteDatabase.update(SubCategory.TABLE_NAME, values, where, selectionArgs);
                break;

            case PRODUCTS:
                updateCount = sqLiteDatabase.update(Product.TABLE_NAME, values, selection, selectionArgs);
                break;
            case PRODUCTS_ID:
                String productId = uri.getLastPathSegment();
                where = Product._ID + " = " + productId;
                if (!TextUtils.isEmpty(selection))
                {
                    where += " AND " + selection;
                }
                updateCount = sqLiteDatabase.update(Product.TABLE_NAME, values, where, selectionArgs);
                break;

            case ACCOUNTS:
                updateCount = sqLiteDatabase.update(Account.TABLE_NAME, values, selection, selectionArgs);
                break;
            case ACCOUNTS_EMAIL:
                String accountEmail = uri.getLastPathSegment();
                where = Account.EMAIL + " = '" + accountEmail + "'";
                if (!TextUtils.isEmpty(selection))
                {
                    where += " AND " + selection;
                }
                updateCount = sqLiteDatabase.update(Account.TABLE_NAME, values, where, selectionArgs);
                break;

            case WISHLIST:
                updateCount = sqLiteDatabase.update(Wishlist.TABLE_NAME, values, selection, selectionArgs);
                break;
            case WISHLIST_ID:
                String wishlistProductId = uri.getLastPathSegment();
                where = Wishlist.PRODUCT_ID + " = " + wishlistProductId;
                if (!TextUtils.isEmpty(selection))
                {
                    where += " AND " + selection;
                }
                updateCount = sqLiteDatabase.update(Wishlist.TABLE_NAME, values, where, selectionArgs);
                break;

            case CART:
                updateCount = sqLiteDatabase.update(Cart.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CART_ID:
                String cartProductId = uri.getLastPathSegment();
                where = Cart.PRODUCT_ID + " = " + cartProductId;
                if (!TextUtils.isEmpty(selection))
                {
                    where += " AND " + selection;
                }
                updateCount = sqLiteDatabase.update(Cart.TABLE_NAME, values, where, selectionArgs);
                break;

            case PROMOTION_BANNERS:
                updateCount = sqLiteDatabase.update(PromotionBanner.TABLE_NAME, values, selection, selectionArgs);
                break;
            case PROMOTION_BANNERS_ID:
                String promotionBannerId = uri.getLastPathSegment();
                where = PromotionBanner._ID + " = " + promotionBannerId;
                if (!TextUtils.isEmpty(selection))
                {
                    where += " AND " + selection;
                }
                updateCount = sqLiteDatabase.update(PromotionBanner.TABLE_NAME, values, where, selectionArgs);
                break;

            case PAYMENT_CARDS:
                updateCount = sqLiteDatabase.update(PaymentCard.TABLE_NAME, values, selection, selectionArgs);
                break;
            case PAYMENT_CARDS_EMAIL:
                String paymentCardEmail = uri.getLastPathSegment();
                where = PaymentCard.EMAIL + " = " + paymentCardEmail;
                if (!TextUtils.isEmpty(selection))
                {
                    where += " AND " + selection;
                }
                updateCount = sqLiteDatabase.update(PaymentCard.TABLE_NAME, values, where, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unspported URI : " + uri);

        }

        if (updateCount > 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return updateCount;

    }

}
