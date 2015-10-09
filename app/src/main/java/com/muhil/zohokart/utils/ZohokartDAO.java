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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

public class ZohokartDAO {

    Context context;

    public ZohokartDAO(Context context) {
        this.context = context;
    }

    public int addCategories(List<Category> categories) {

        ContentProviderResult[] contentProviderResults = null;
        ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<>();

        for (Category category : categories) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Category._ID, category.getId());
            contentValues.put(Category.NAME, category.getName());
            contentProviderOperations.add(ContentProviderOperation.newInsert(Category.CONTENT_URI).withValues(contentValues).withYieldAllowed(true).build());

        }

        try {
            contentProviderResults = context.getContentResolver().applyBatch(ZohokartContentProvider.AUTHORITY, contentProviderOperations);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e("DAO", "Error adding categories ", e);
        }

        return contentProviderResults != null ? contentProviderResults.length : 0;
    }

    public int addSubCategories(List<SubCategory> subCategories) {

        ContentProviderResult[] contentProviderResults = null;
        ContentValues contentValues = new ContentValues();
        ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<>();

        for (SubCategory subCategory : subCategories) {
            contentValues.put(SubCategory._ID, subCategory.getId());
            contentValues.put(SubCategory.CATEGORY_ID, subCategory.getCategoryId());
            contentValues.put(SubCategory.NAME, subCategory.getName());
            contentProviderOperations.add(ContentProviderOperation.newInsert(SubCategory.CONTENT_URI).withValues(contentValues).withYieldAllowed(true).build());
        }

        try {
            contentProviderResults = context.getContentResolver().applyBatch(ZohokartContentProvider.AUTHORITY, contentProviderOperations);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e("DAO", "Error adding sub categories ", e);
        }

        return contentProviderResults != null ? contentProviderResults.length : 0;
    }

    public int addProducts(List<Product> products) {
        ContentProviderResult[] contentProviderResults = null;
        ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<>();

        for (Product product : products) {
            ContentValues contentValues = new ContentValues();
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

        try {
            contentProviderResults = context.getContentResolver().applyBatch(ZohokartContentProvider.AUTHORITY, contentProviderOperations);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e("DAO", "Error adding products ", e);
        }

        return contentProviderResults != null ? contentProviderResults.length : 0;
    }

    public List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();
        try (Cursor cursor = context.getContentResolver().query(
                Category.CONTENT_URI, Category.PROJECTION, null, null, null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex(Category._ID));
                    String name = cursor.getString(cursor.getColumnIndex(Category.NAME));
                    Category category = new Category(id, name);
                    categories.add(category);
                }
            }
        } catch (Exception e) {
            Log.e("DAO", "Error fetching categories ", e);
        }
        return categories;
    }

    public Map<Integer, List<SubCategory>> getSubCategoriesByCategory() {
        Map<Integer, List<SubCategory>> subCategoriesBycategory = new HashMap<>();
        try (Cursor cursor = context.getContentResolver().query(
                SubCategory.CONTENT_URI, SubCategory.PROJECTION, null, null, null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex(SubCategory._ID));
                    int categoryId = cursor.getInt(cursor.getColumnIndex(SubCategory.CATEGORY_ID));
                    String name = cursor.getString(cursor.getColumnIndex(SubCategory.NAME));
                    SubCategory subCategory = new SubCategory(id, categoryId, name);
                    if (subCategoriesBycategory.get(categoryId) != null) {
                        (subCategoriesBycategory.get(categoryId)).add(subCategory);
                    } else {
                        List<SubCategory> subCategories = new ArrayList<>();
                        subCategories.add(subCategory);
                        subCategoriesBycategory.put(categoryId, subCategories);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("DAO", "Error fetching subcategories by categories ", e);
        }
        return subCategoriesBycategory;
    }

    private Product getProductFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(Product._ID));
        int subCategoryId = cursor.getInt(cursor.getColumnIndex(Product.SUB_CATEGORY_ID));
        String brand = cursor.getString(cursor.getColumnIndex(Product.BRAND));
        String title = cursor.getString(cursor.getColumnIndex(Product.TITLE));
        String description = cursor.getString(cursor.getColumnIndex(Product.DESCRIPTION));
        String thumbnail = cursor.getString(cursor.getColumnIndex(Product.THUMBNAIL));
        double price = cursor.getDouble(cursor.getColumnIndex(Product.PRICE));
        double stars = cursor.getDouble(cursor.getColumnIndex(Product.STARS));
        int ratings = cursor.getInt(cursor.getColumnIndex(Product.RATINGS));
        return new Product(id, subCategoryId, brand, title, description, thumbnail, price, stars, ratings);
    }

    public List<Product> getProductsForSubCategory(int subCategoryIdFromCaller) {
        List<Product> products = new ArrayList<>();
        try (Cursor cursor = context.getContentResolver().query(
                Product.CONTENT_URI, Product.PROJECTION, Product.SUB_CATEGORY_ID + " = ?",
                new String[]{String.valueOf(subCategoryIdFromCaller)}, null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    products.add(getProductFromCursor(cursor));
                }
            }

        } catch (Exception e) {
            Log.e("DAO", "Error getting products for a sub category");
        }
        return products;
    }

    public List<Product> getProductsFromWishlist() {

        List<Product> products = new ArrayList<>();
        List<Integer> productIdsInWishList = new ArrayList<>();
        try (Cursor cursor = context.getContentResolver().query(
                Wishlist.CONTENT_URI, Wishlist.PROJECTION, null, null, null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex(Wishlist.PRODUCT_ID));
                    productIdsInWishList.add(id);
                }
            }
        } catch (Exception e) {
            Log.d("DAO", "Error fetching product ids from wishlist ", e);
        }


        if (!productIdsInWishList.isEmpty()) {
            for (Integer productId : productIdsInWishList) {
                try (Cursor cursor = context.getContentResolver().query(
                        Uri.parse(Product.CONTENT_URI + "/" + productId), Product.PROJECTION, null, null, null)) {
                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            products.add(getProductFromCursor(cursor));
                        }
                    }
                } catch (Exception e) {
                    Log.d("DAO", "Error fetching product in wishlist ", e);
                }
            }
        }

        return products;
    }

    public List<Product> getProductsFromCart() {

        List<Product> products = new ArrayList<>();
        List<Integer> productIdsInCart = new ArrayList<>();
        try (Cursor cursor = context.getContentResolver().query(
                Cart.CONTENT_URI, Cart.PROJECTION, null, null, null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex(Cart.PRODUCT_ID));
                    productIdsInCart.add(id);
                }
            }
        }

        if (!productIdsInCart.isEmpty()) {
            for (Integer productId : productIdsInCart) {
                try (Cursor cursor = context.getContentResolver().query(
                        Uri.parse(Product.CONTENT_URI + "/" + productId), Product.PROJECTION, null, null, null)) {
                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            products.add(getProductFromCursor(cursor));
                        }
                    }
                }
            }
        }
        return products;
    }


    public List<Product> getProductsForProductIds(List<Integer> productIds) {
        List<Product> products = new ArrayList<>();
        if (productIds != null && !productIds.isEmpty()) {
            for (Integer productId : productIds) {
                try (Cursor cursor = context.getContentResolver().query(
                        Uri.parse(Product.CONTENT_URI + "/" + productId), Product.PROJECTION, null, null, null)) {
                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            products.add(getProductFromCursor(cursor));
                        }
                    }
                }
            }
        }
        return products;
    }

    public boolean checkInWishlist(int productId) {

        boolean result = false;
        try (Cursor cursor = context.getContentResolver().query(
                Uri.parse(Wishlist.CONTENT_URI + "/" + productId), Wishlist.PROJECTION, null, null, null)) {
            if (cursor != null) {
                result = (cursor.getCount() == 1);
            }
        } catch (Exception e) {
            Log.e("DAO", "Error checking for a product in wishlist ", e);
        }
        return result;

    }

    public boolean updateQuantityOfProductInCart(int quantity, int productId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Cart.QUANTITY, quantity);
        int updateCount = context.getContentResolver().update(Uri.parse(Cart.CONTENT_URI + "/" + productId), contentValues, null, null);
        return (updateCount == 1);
    }

    public boolean removeFromCart(int productId) {
        int deleteCount = context.getContentResolver().delete(Uri.parse(Cart.CONTENT_URI + "/" + productId), null, null);
        return (deleteCount == 1);
    }

    public boolean addToWishlist(int productId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Wishlist.PRODUCT_ID, productId);
        Uri insertedUri = context.getContentResolver().insert(Wishlist.CONTENT_URI, contentValues);
        return insertedUri != null;
    }

    public boolean removeFromWishList(int productId) {
        int deleteCount = context.getContentResolver().delete(Uri.parse(Wishlist.CONTENT_URI + "/" + productId), null, null);
        return (deleteCount == 1);
    }

    public boolean checkInCart(int productId) {
        boolean result = false;
        try (Cursor cursor = context.getContentResolver().query(Uri.parse(Cart.CONTENT_URI + "/" + productId), Cart.PROJECTION, null, null, null)) {
            if (cursor != null) {
                result = (cursor.getCount() == 1);
            }
        } catch (Exception e) {
            Log.e("DAO", "Error checking product in cart", e);
        }
        return result;
    }

    public boolean addToCart(int productId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Cart.PRODUCT_ID, productId);
        Uri insertedUri = context.getContentResolver().insert(Cart.CONTENT_URI, contentValues);
        return insertedUri != null;
    }

    public boolean hasAccount(String emailString) {
        boolean result = false;
        try (Cursor cursor = context.getContentResolver().query(Uri.parse(Account.CONTENT_URI + "/" + emailString), Account.PROJECTION, null, null, null)) {
            if (cursor != null) {
                result = (cursor.getCount() == 1);
            }
        } catch (Exception e) {
            Log.e("DAO", "Error checking has account", e);
        }
        return result;
    }

    public Account getAccountIfAvailable(String emailString, String passwordString) {
        Account account = new Account();
        try (Cursor cursor = context.getContentResolver().query(Account.CONTENT_URI, Account.PROJECTION, Account.EMAIL + " = ? AND " + Account.PASSWORD + " = ?", new String[]{emailString, passwordString}, null)) {
            if (cursor != null && cursor.getCount() == 1) {
                while (cursor.moveToNext()) {
                    account.setName(cursor.getString(cursor.getColumnIndex(Account.NAME)));
                    account.setEmail(cursor.getString(cursor.getColumnIndex(Account.EMAIL)));
                    account.setPassword(cursor.getString(cursor.getColumnIndex(Account.PASSWORD)));
                    account.setPhoneNumber(cursor.getString(cursor.getColumnIndex(Account.PHONE_NUMBER)));
                    account.setDateOfBirth(cursor.getString(cursor.getColumnIndex(Account.DATE_OF_BIRTH)));
                }
            } else {
                account = null;
            }
        } catch (Exception e) {
            Log.e("DAO", "Error fetching account", e);
        }
        return account;
    }

    public boolean addAccount(Account account) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Account.NAME, account.getName());
        contentValues.put(Account.EMAIL, account.getEmail());
        contentValues.put(Account.PASSWORD, account.getPassword());
        contentValues.put(Account.PHONE_NUMBER, account.getPhoneNumber());
        contentValues.put(Account.DATE_OF_BIRTH, account.getDateOfBirth());
        Uri insertedUri = context.getContentResolver().insert(Account.CONTENT_URI, contentValues);
        return insertedUri != null;
    }

    public int addBanners(List<PromotionBanner> banners)
    {
        Gson gson = new Gson();
        ContentProviderResult[] contentProviderResults = null;
        ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<>();

        for (PromotionBanner banner : banners) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(PromotionBanner._ID, banner.getId());
            contentValues.put(PromotionBanner.BANNER_URL, banner.getBanner());
            String productIdListString = gson.toJson(banner.getProductIds());
            contentValues.put(PromotionBanner.PRODUCTS_RELATED, productIdListString);
            contentProviderOperations.add(ContentProviderOperation.newInsert(PromotionBanner.CONTENT_URI).withValues(contentValues).withYieldAllowed(true).build());
        }

        try {
            contentProviderResults = context.getContentResolver().applyBatch(ZohokartContentProvider.AUTHORITY, contentProviderOperations);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e("DAO", "Error adding categories ", e);
        }

        return contentProviderResults != null ? contentProviderResults.length : 0;
    }

    public List<PromotionBanner> getBanners()
    {
        Gson gson = new Gson();
        List<PromotionBanner> banners = new ArrayList<>();
        try (Cursor cursor = context.getContentResolver().query(
                PromotionBanner.CONTENT_URI, PromotionBanner.PROJECTION, null, null, null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex(PromotionBanner._ID));
                    String banner_url = cursor.getString(cursor.getColumnIndex(PromotionBanner.BANNER_URL));
                    String productsIdsJsonString = cursor.getString(cursor.getColumnIndex(PromotionBanner.PRODUCTS_RELATED));
                    List<Integer> productIds = gson.fromJson(productsIdsJsonString, new TypeToken<List<Integer>>() {
                    }.getType());
                    PromotionBanner banner = new PromotionBanner(id, banner_url, productIds);
                    banners.add(banner);
                }
            }
        } catch (Exception e) {
            Log.e("DAO", "Error fetching banners", e);
        }
        return banners;
    }

}