package com.muhil.zohokart.utils;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.muhil.zohokart.models.Account;
import com.muhil.zohokart.models.Cart;
import com.muhil.zohokart.models.Category;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.models.SubCategory;
import com.muhil.zohokart.models.Wishlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by muhil-ga42 on 09/10/15.
 */
public class ZohokartDAO
{

    Context context;

    public ZohokartDAO(Context context)
    {
        this.context = context;
    }

    public int addCategories(List<Category> categories)
    {

        ContentProviderResult[] contentProviderResults = null;
        ContentValues contentValues = new ContentValues();
        ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<>();

        for (Category category : categories)
        {

            contentValues.put(Category._ID, category.getId());
            contentValues.put(Category.NAME, category.getName());
            contentProviderOperations.add(ContentProviderOperation.newInsert(Category.CONTENT_URI).withValues(contentValues).withYieldAllowed(true).build());

        }

        try
        {
            contentProviderResults = context.getContentResolver().applyBatch(ZohokartContentProvider.AUTHORITY, contentProviderOperations);
        }
        catch (RemoteException | OperationApplicationException e)
        {
            e.printStackTrace();
        }

        if (contentProviderResults != null){
            return contentProviderResults.length;
        }
        else {
            return 0;
        }

    }

    public int addSubCategories(List<SubCategory> subCategories)
    {

        ContentProviderResult[] contentProviderResults = null;
        ContentValues contentValues = new ContentValues();
        ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<>();

        for (SubCategory subCategory : subCategories)
        {

            contentValues.put(SubCategory._ID, subCategory.getId());
            contentValues.put(SubCategory.CATEGORY_ID, subCategory.getCategoryId());
            contentValues.put(SubCategory.NAME, subCategory.getName());
            contentProviderOperations.add(ContentProviderOperation.newInsert(SubCategory.CONTENT_URI).withValues(contentValues).withYieldAllowed(true).build());

        }

        try
        {
            contentProviderResults = context.getContentResolver().applyBatch(ZohokartContentProvider.AUTHORITY, contentProviderOperations);
        }
        catch (RemoteException | OperationApplicationException e)
        {
            e.printStackTrace();
        }

        if (contentProviderResults != null){
            return contentProviderResults.length;
        }
        else {
            return 0;
        }

    }

    public int addProducts(List<Product> products)
    {
        ContentProviderResult[] contentProviderResults = null;
        ContentValues contentValues = new ContentValues();
        ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<>();

        for (Product product : products)
        {
            contentValues.put(Product._ID, product.getId());
            contentValues.put(Product.SUB_CATEGORY_ID, product.getSubCategoryId());
            contentValues.put(Product.BRAND, product.getBrand());
            contentValues.put(Product.TITLE, product.getTitle());
            contentValues.put(Product.DESCRIPTION, product.getDescription());
            contentValues.put(Product.THUMBNAIL, product.getThumbnail());
            contentValues.put(Product.PRICE, product.getPrice());
            contentValues.put(Product.STARS, product.getStars());
            contentValues.put(Product.RATINGS, product.getRatings());
            contentProviderOperations.add(ContentProviderOperation.newInsert(Product.CONTENT_URI).withValues(contentValues).withYieldAllowed(true).build());
        }

        try
        {
            contentProviderResults = context.getContentResolver().applyBatch(ZohokartContentProvider.AUTHORITY, contentProviderOperations);
        }
        catch (RemoteException | OperationApplicationException e)
        {
            e.printStackTrace();
        }

        if (contentProviderResults != null){
            return contentProviderResults.length;
        }
        else {
            return 0;
        }
    }

    public List<Category> getCategories()
    {

        int id;
        String name;
        List<Category> categories = new ArrayList<>();
        Category category;
        Cursor cursor = context.getContentResolver().query(Category.CONTENT_URI, Category.PROJECTION, null, null, null);

        while (cursor.moveToNext())
        {
            id = cursor.getInt(cursor.getColumnIndex(Category._ID));
            name = cursor.getString(cursor.getColumnIndex(Category.NAME));
            category = new Category(id, name);
            categories.add(category);
        }
        cursor.close();

        return categories;

    }

    public Map<Integer, List<SubCategory>> getSubCategoriesByCategory()
    {

        int id, categoryId;
        String name;
        SubCategory subCategory;
        List<SubCategory> subCategories;
        Map<Integer, List<SubCategory>> subCategoriesBycategory = new HashMap<>();
        Cursor cursor = context.getContentResolver().query(SubCategory.CONTENT_URI, SubCategory.PROJECTION, null, null, null);

        while (cursor.moveToNext())
        {
            id = cursor.getInt(cursor.getColumnIndex(SubCategory._ID));
            categoryId = cursor.getInt(cursor.getColumnIndex(SubCategory.CATEGORY_ID));
            name = cursor.getString(cursor.getColumnIndex(SubCategory.NAME));
            subCategory = new SubCategory(id, categoryId, name);
            if (subCategoriesBycategory.get(categoryId) != null)
            {
                (subCategoriesBycategory.get(categoryId)).add(subCategory);
            }
            else
            {
                subCategories = new ArrayList<>();
                subCategories.add(subCategory);
                subCategoriesBycategory.put(categoryId, subCategories);
            }
        }
        cursor.close();

        return subCategoriesBycategory;

    }

    public List<Product> getProductsForSubCategory(int subCategoryIdFromCaller)
    {

        int id, subCategoryId, ratings;
        String brand, title, description, thumbnail;
        double price, stars;
        Product product;
        List<Product> products = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(Product.CONTENT_URI, Product.PROJECTION, Product.SUB_CATEGORY_ID + " = ?", new String[]{String.valueOf(subCategoryIdFromCaller)}, null);
        while (cursor.moveToNext())
        {
            id = cursor.getInt(cursor.getColumnIndex(Product._ID));
            subCategoryId = cursor.getInt(cursor.getColumnIndex(Product.SUB_CATEGORY_ID));
            brand = cursor.getString(cursor.getColumnIndex(Product.BRAND));
            title = cursor.getString(cursor.getColumnIndex(Product.TITLE));
            description = cursor.getString(cursor.getColumnIndex(Product.DESCRIPTION));
            thumbnail = cursor.getString(cursor.getColumnIndex(Product.THUMBNAIL));
            price = cursor.getDouble(cursor.getColumnIndex(Product.PRICE));
            stars = cursor.getDouble(cursor.getColumnIndex(Product.STARS));
            ratings = cursor.getInt(cursor.getColumnIndex(Product.RATINGS));
            product = new Product(id, subCategoryId, brand, title, description, thumbnail, price, stars, ratings);
            products.add(product);
        }
        cursor.close();

        return products;

    }

    public List<Product> getProductsFromWishlist()
    {

        int id, subCategoryId, ratings;
        String brand, title, description, thumbnail;
        double price, stars;
        Product product;
        List<Product> products = new ArrayList<>();
        List<Integer> productIdInWishlist = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(Wishlist.CONTENT_URI, Wishlist.PROJECTION, null, null, null);
        Log.d("WISHLIST_CURSOR", String.valueOf(cursor.getCount()));
        while (cursor.moveToNext())
        {
            id = cursor.getInt(cursor.getColumnIndex(Wishlist.PRODUCT_ID));
            productIdInWishlist.add(id);
        }

        if (!productIdInWishlist.isEmpty())
        {
            for (Integer productId : productIdInWishlist) {

                cursor = context.getContentResolver().query(Uri.parse(Product.CONTENT_URI + "/" + productId), Product.PROJECTION, null, null, null);
                Log.d("WISHLIST_CURSOR", String.valueOf(cursor.getCount()));
                while (cursor.moveToNext())
                {
                    id = cursor.getInt(cursor.getColumnIndex(Product._ID));
                    subCategoryId = cursor.getInt(cursor.getColumnIndex(Product.SUB_CATEGORY_ID));
                    brand = cursor.getString(cursor.getColumnIndex(Product.BRAND));
                    title = cursor.getString(cursor.getColumnIndex(Product.TITLE));
                    description = cursor.getString(cursor.getColumnIndex(Product.DESCRIPTION));
                    thumbnail = cursor.getString(cursor.getColumnIndex(Product.THUMBNAIL));
                    price = cursor.getDouble(cursor.getColumnIndex(Product.PRICE));
                    stars = cursor.getDouble(cursor.getColumnIndex(Product.STARS));
                    ratings = cursor.getInt(cursor.getColumnIndex(Product.RATINGS));
                    product = new Product(id, subCategoryId, brand, title, description, thumbnail, price, stars, ratings);
                    products.add(product);
                }

            }
        }
        else
        {
            return null;
        }
        cursor.close();

        return products;

    }

    public List<Product> getProductsFromCart() {

        int id, subCategoryId, ratings;
        String brand, title, description, thumbnail;
        double price, stars;
        Product product;
        List<Product> products = new ArrayList<>();
        List<Integer> productIdInCart = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(Cart.CONTENT_URI, Cart.PROJECTION, null, null, null);
        Log.d("CART_CURSOR", String.valueOf(cursor.getCount()));
        while (cursor.moveToNext())
        {
            id = cursor.getInt(cursor.getColumnIndex(Cart.PRODUCT_ID));
            productIdInCart.add(id);
        }

        if (!productIdInCart.isEmpty())
        {
            for (Integer productId : productIdInCart) {

                cursor = context.getContentResolver().query(Uri.parse(Product.CONTENT_URI + "/" + productId), Product.PROJECTION, null, null, null);
                Log.d("CART_CURSOR", String.valueOf(cursor.getCount()));
                while (cursor.moveToNext())
                {
                    id = cursor.getInt(cursor.getColumnIndex(Product._ID));
                    subCategoryId = cursor.getInt(cursor.getColumnIndex(Product.SUB_CATEGORY_ID));
                    brand = cursor.getString(cursor.getColumnIndex(Product.BRAND));
                    title = cursor.getString(cursor.getColumnIndex(Product.TITLE));
                    description = cursor.getString(cursor.getColumnIndex(Product.DESCRIPTION));
                    thumbnail = cursor.getString(cursor.getColumnIndex(Product.THUMBNAIL));
                    price = cursor.getDouble(cursor.getColumnIndex(Product.PRICE));
                    stars = cursor.getDouble(cursor.getColumnIndex(Product.STARS));
                    ratings = cursor.getInt(cursor.getColumnIndex(Product.RATINGS));
                    product = new Product(id, subCategoryId, brand, title, description, thumbnail, price, stars, ratings);
                    products.add(product);
                }

            }
        }
        else
        {
            return null;
        }
        cursor.close();

        return products;

    }

    public boolean checkInWishlist(int productId)
    {

        boolean result;
        Cursor cursor = context.getContentResolver().query(Uri.parse(Wishlist.CONTENT_URI + "/" + productId), Wishlist.PROJECTION, null, null, null);
        result = (cursor.getCount() == 1);
        cursor.close();
        return result;

    }

    public boolean updateQuantityOfProductInCart(int quantity, int productId)
    {

        ContentValues contentValues = new ContentValues();
        contentValues.put(Cart.QUANTITY, quantity);
        int updateCount = context.getContentResolver().update(Uri.parse(Cart.CONTENT_URI + "/" + productId), contentValues, null, null);
        return (updateCount == 1);

    }

    public boolean removeFromCart(int productId)
    {

        int deleteCount = context.getContentResolver().delete(Uri.parse(Cart.CONTENT_URI + "/" + productId), null, null);
        return (deleteCount == 1);

    }

    public boolean addToWishlist(int productId)
    {

        ContentValues contentValues = new ContentValues();
        contentValues.put(Wishlist.PRODUCT_ID, productId);
        Uri insertedUri = context.getContentResolver().insert(Wishlist.CONTENT_URI, contentValues);
        return insertedUri != null;

    }

    public boolean removeFromWishList(int productId)
    {

        int deleteCount = context.getContentResolver().delete(Uri.parse(Wishlist.CONTENT_URI + "/" + productId), null, null);
        return (deleteCount == 1);

    }

    public boolean checkInCart(int productId)
    {

        boolean result;
        Cursor cursor = context.getContentResolver().query(Uri.parse(Cart.CONTENT_URI + "/" + productId), Cart.PROJECTION, null, null, null);
        result = (cursor.getCount() == 1);
        cursor.close();
        return result;

    }

    public boolean addToCart(int productId) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(Cart.PRODUCT_ID, productId);
        Uri insertedUri = context.getContentResolver().insert(Cart.CONTENT_URI, contentValues);
        return insertedUri != null;

    }

    public boolean hasAccount(String emailString)
    {

        boolean result;
        Cursor cursor = context.getContentResolver().query(Uri.parse(Account.CONTENT_URI + "/" + emailString), Account.PROJECTION, null, null, null);
        result = (cursor.getCount() == 1);
        cursor.close();
        return result;

    }

    public Account getAccountIfAvailable(String emailString, String passwordString)
    {
        Account account = new Account();
        Cursor cursor = context.getContentResolver().query(Account.CONTENT_URI, Account.PROJECTION, Account.EMAIL + " = ? AND " + Account.PASSWORD + " = ?", new String[]{emailString, passwordString}, null);
        if (cursor.getCount() == 1)
        {
            while (cursor.moveToNext())
            {
                account.setName(cursor.getString(cursor.getColumnIndex(Account.NAME)));
                account.setEmail(cursor.getString(cursor.getColumnIndex(Account.EMAIL)));
                account.setPassword(cursor.getString(cursor.getColumnIndex(Account.PASSWORD)));
                account.setPhoneNumber(cursor.getString(cursor.getColumnIndex(Account.PHONE_NUMBER)));
                account.setDateOfBirth(cursor.getString(cursor.getColumnIndex(Account.DATE_OF_BIRTH)));
            }
        }
        else
        {
            return null;
        }

        return account;

    }

    public boolean addAccount(Account account)
    {

        ContentValues contentValues = new ContentValues();
        contentValues.put(Account.NAME, account.getName());
        contentValues.put(Account.EMAIL, account.getEmail());
        contentValues.put(Account.PASSWORD, account.getPassword());
        contentValues.put(Account.PHONE_NUMBER, account.getPhoneNumber());
        contentValues.put(Account.DATE_OF_BIRTH, account.getDateOfBirth());

        Uri insertedUri = context.getContentResolver().insert(Account.CONTENT_URI, contentValues);
        return insertedUri != null;

    }
}
