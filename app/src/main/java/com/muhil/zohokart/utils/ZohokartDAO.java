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
import com.muhil.zohokart.models.FilterPair;
import com.muhil.zohokart.models.Mobile;
import com.muhil.zohokart.models.Order;
import com.muhil.zohokart.models.OrderLineItem;
import com.muhil.zohokart.models.PaymentCard;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.models.PromotionBanner;
import com.muhil.zohokart.models.SubCategory;
import com.muhil.zohokart.models.Wishlist;
import com.muhil.zohokart.models.specification.Specification;
import com.muhil.zohokart.models.specification.SpecificationGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ZohokartDAO
{

    Context context;

    public ZohokartDAO(Context context)
    {
        this.context = context;
    }

    // ***** Data access methods for Categories *****

    // ***** add categories *****
    public int addCategories(List<Category> categories)
    {
        ContentProviderResult[] contentProviderResults = null;
        ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<>();
        for (Category category : categories)
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Category._ID, category.getId());
            contentValues.put(Category.NAME, category.getName());
            contentProviderOperations.add(ContentProviderOperation.newInsert(Category.CONTENT_URI).withValues(contentValues).withYieldAllowed(true).build());
        }
        try
        {
            contentProviderResults = context.getContentResolver().applyBatch(ZohokartContentProvider.AUTHORITY, contentProviderOperations);
        } catch (RemoteException | OperationApplicationException e)
        {
            Log.e("DAO", "Error adding categories ", e);
        }
        return contentProviderResults != null ? contentProviderResults.length : 0;
    }

    // ***** returns categories *****
    public List<Category> getCategories()
    {
        List<Category> categories = new ArrayList<>();
        try (Cursor cursor = context.getContentResolver().query(
                Category.CONTENT_URI, Category.PROJECTION, null, null, null))
        {
            if (cursor != null)
            {
                while (cursor.moveToNext())
                {
                    int id = cursor.getInt(cursor.getColumnIndex(Category._ID));
                    String name = cursor.getString(cursor.getColumnIndex(Category.NAME));
                    Category category = new Category(id, name);
                    categories.add(category);
                }
            }
        } catch (Exception e)
        {
            Log.e("DAO", "Error fetching categories ", e);
        }
        return categories;
    }

    // ***** categories methods ends *****

    // ***** subCategories methods starts *****

    // ***** Data access methods for SubCategories *****
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
        } catch (RemoteException | OperationApplicationException e)
        {
            Log.e("DAO", "Error adding sub categories ", e);
        }

        return contentProviderResults != null ? contentProviderResults.length : 0;
    }

    //***** get SubCategories by categories *****
    public Map<Integer, List<SubCategory>> getSubCategoriesByCategory()
    {
        Map<Integer, List<SubCategory>> subCategoriesBycategory = new HashMap<>();
        try (Cursor cursor = context.getContentResolver().query(
                SubCategory.CONTENT_URI, SubCategory.PROJECTION, null, null, null))
        {
            if (cursor != null)
            {
                while (cursor.moveToNext())
                {
                    int id = cursor.getInt(cursor.getColumnIndex(SubCategory._ID));
                    int categoryId = cursor.getInt(cursor.getColumnIndex(SubCategory.CATEGORY_ID));
                    String name = cursor.getString(cursor.getColumnIndex(SubCategory.NAME));
                    SubCategory subCategory = new SubCategory(id, categoryId, name);
                    if (subCategoriesBycategory.get(categoryId) != null)
                    {
                        (subCategoriesBycategory.get(categoryId)).add(subCategory);
                    } else
                    {
                        List<SubCategory> subCategories = new ArrayList<>();
                        subCategories.add(subCategory);
                        subCategoriesBycategory.put(categoryId, subCategories);
                    }
                }
            }
        } catch (Exception e)
        {
            Log.e("DAO", "Error fetching subcategories by categories ", e);
        }
        return subCategoriesBycategory;
    }

    // ***** SubCategories methods ends *****

    // ***** Product access methods *****

    // ***** adds products to db *****
    public int addProducts(List<Product> products)
    {
        ContentProviderResult[] contentProviderResults = null;
        ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<>();

        for (Product product : products)
        {
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
            contentValues.put(Product.WARRANTY, product.getWarranty());
            contentProviderOperations.add(ContentProviderOperation.newInsert(Product.CONTENT_URI).withValues(contentValues).withYieldAllowed(true).build());
        }

        try
        {
            contentProviderResults = context.getContentResolver().applyBatch(ZohokartContentProvider.AUTHORITY, contentProviderOperations);
        } catch (RemoteException | OperationApplicationException e)
        {
            Log.e("DAO", "Error adding products ", e);
        }

        return contentProviderResults != null ? contentProviderResults.length : 0;
    }

    // ***** returns a product object from cursor *****
    private Product getProductFromCursor(Cursor cursor)
    {
        int id = cursor.getInt(cursor.getColumnIndex(Product._ID));
        int subCategoryId = cursor.getInt(cursor.getColumnIndex(Product.SUB_CATEGORY_ID));
        String brand = cursor.getString(cursor.getColumnIndex(Product.BRAND));
        String title = cursor.getString(cursor.getColumnIndex(Product.TITLE));
        String description = cursor.getString(cursor.getColumnIndex(Product.DESCRIPTION));
        String thumbnail = cursor.getString(cursor.getColumnIndex(Product.THUMBNAIL));
        double price = cursor.getDouble(cursor.getColumnIndex(Product.PRICE));
        double stars = cursor.getDouble(cursor.getColumnIndex(Product.STARS));
        int ratings = cursor.getInt(cursor.getColumnIndex(Product.RATINGS));
        String warranty = cursor.getString(cursor.getColumnIndex(Product.WARRANTY));
        return new Product(id, subCategoryId, brand, title, description, thumbnail, price, stars, ratings, warranty);
    }

    // ***** retrieves products based on subCategory *****
    public List<Product> getProductsForSubCategory(int subCategoryIdFromCaller)
    {
        List<Product> products = new ArrayList<>();
        try (Cursor cursor = context.getContentResolver().query(
                Product.CONTENT_URI, Product.PROJECTION, Product.SUB_CATEGORY_ID + " = ?",
                new String[]{String.valueOf(subCategoryIdFromCaller)}, null))
        {
            if (cursor != null)
            {
                while (cursor.moveToNext())
                {
                    products.add(getProductFromCursor(cursor));
                }
            }

        } catch (Exception e)
        {
            Log.e("DAO", "Error getting products for a sub category");
        }
        return products;
    }

    // ***** returns products based on product Id *****
    public List<Product> getProductsForProductIds(List<Integer> productIds)
    {
        List<Product> products = new ArrayList<>();
        if (productIds != null && !productIds.isEmpty())
        {
            for (Integer productId : productIds)
            {
                try (Cursor cursor = context.getContentResolver().query(
                        Uri.parse(Product.CONTENT_URI + "/" + productId), Product.PROJECTION, null, null, null))
                {
                    if (cursor != null)
                    {
                        while (cursor.moveToNext())
                        {
                            products.add(getProductFromCursor(cursor));
                        }
                    }
                }
            }
        }
        return products;
    }

    public List<Product> getTopRatedProducts()
    {
        List<Product> products = new ArrayList<>();
        try (Cursor cursor = context.getContentResolver().query(Product.CONTENT_URI, Product.PROJECTION, Product.STARS + " > ? ", new String[]{String.valueOf(4)}, Product.STARS + " DESC"))
        {
            if (cursor != null)
            {
                while (cursor.moveToNext())
                {
                    products.add(getProductFromCursor(cursor));
                }
            }
        }
        return products;
    }

    public List<Product> getTopRatedProductsWithLimit()
    {
        List<Product> products = new ArrayList<>();
        try (Cursor cursor = context.getContentResolver().query(Product.CONTENT_URI, Product.PROJECTION, Product.STARS + " > ?", new String[]{String.valueOf(4)}, Product.STARS + " DESC LIMIT 10"))
        {
            if (cursor != null)
            {
                while (cursor.moveToNext())
                {
                    products.add(getProductFromCursor(cursor));
                }
            }
        }
        return products;
    }

    // ***** product methods ends *****

    // ***** wishlist methods starts *****

    // ***** returns products from wishlist *****
    public List<Product> getProductsFromWishlist(String email)
    {

        List<Product> products = new ArrayList<>();
        List<Integer> productIdsInWishList = new ArrayList<>();
        try (Cursor cursor = context.getContentResolver().query(
                Wishlist.CONTENT_URI, Wishlist.PROJECTION, Wishlist.EMAIL + " = ?", new String[]{email}, null))
        {
            if (cursor != null)
            {
                while (cursor.moveToNext())
                {
                    int id = cursor.getInt(cursor.getColumnIndex(Wishlist.PRODUCT_ID));
                    productIdsInWishList.add(id);
                }
            }
        } catch (Exception e)
        {
            Log.d("DAO", "Error fetching product ids from wishlist ", e);
        }


        if (!productIdsInWishList.isEmpty())
        {
            products = getProductsForProductIds(productIdsInWishList);
        }

        return products;
    }

    // ***** checks wishlist for Product *****
    public boolean checkInWishlist(int productId, String email)
    {
        boolean result = false;
        try (Cursor cursor = context.getContentResolver().query(
                Uri.parse(Wishlist.CONTENT_URI + "/" + productId), Wishlist.PROJECTION, Wishlist.EMAIL + " = ?", new String[]{email}, null))
        {
            if (cursor != null)
            {
                result = (cursor.getCount() == 1);
            }
        } catch (Exception e)
        {
            Log.e("DAO", "Error checking for a product in wishlist ", e);
        }
        return result;
    }

    // ***** adds product to wishlist *****
    public boolean addToWishlist(int productId, String email)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Wishlist.EMAIL, email);
        contentValues.put(Wishlist.PRODUCT_ID, productId);
        Uri insertedUri = context.getContentResolver().insert(Wishlist.CONTENT_URI, contentValues);
        return insertedUri != null;
    }

    // ***** removes product from wishlist *****
    public boolean removeFromWishList(int productId, String email)
    {
        int deleteCount = context.getContentResolver().delete(Uri.parse(Wishlist.CONTENT_URI + "/" + productId), Wishlist.EMAIL + " = ?", new String[]{email});
        return (deleteCount == 1);
    }

    // ***** wishlist methods ends *****

    // ***** Cart methods starts *****

    // ***** returns products from cart *****
    public List<Product> getProductsFromCart(String email)
    {

        List<Product> products = new ArrayList<>();
        List<Integer> productIdsInCart = new ArrayList<>();
        try (Cursor cursor = context.getContentResolver().query(
                Cart.CONTENT_URI, Cart.PROJECTION, Cart.EMAIL + " = ?", new String[]{email}, null))
        {
            if (cursor != null)
            {
                while (cursor.moveToNext())
                {
                    int id = cursor.getInt(cursor.getColumnIndex(Cart.PRODUCT_ID));
                    productIdsInCart.add(id);
                }
            }
        }
        catch (Exception e)
        {

        }

        if (!productIdsInCart.isEmpty())
        {
            products = getProductsForProductIds(productIdsInCart);
        }
        return products;
    }

    // ***** updates quantity in product within cart *****
    public boolean updateQuantityOfProductInCart(int quantity, int productId, String email)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Cart.QUANTITY, quantity);
        int updateCount = context.getContentResolver().update(Uri.parse(Cart.CONTENT_URI + "/" + productId), contentValues, Cart.EMAIL + " = ?", new String[]{email});
        return (updateCount == 1);
    }

    // ***** returns quantity of a product from cart *****
    public int getQuantityofProductInCart(int productId, String email)
    {
        int result = 0;
        if (checkInCart(productId, email))
        {
            try (Cursor cursor = context.getContentResolver().query(
                    Uri.parse(Cart.CONTENT_URI + "/" + productId), Cart.PROJECTION, Cart.EMAIL + " = ?", new String[]{email}, null))
            {
                if (cursor != null && (cursor.getCount() == 1))
                {
                    while (cursor.moveToNext())
                    {
                        result = cursor.getInt(cursor.getColumnIndex(Cart.QUANTITY));
                    }
                }
            }
        }
        return result;
    }

    // ***** removes product from cart *****
    public boolean removeFromCart(int productId, String email)
    {
        int deleteCount = context.getContentResolver().delete(Uri.parse(Cart.CONTENT_URI + "/" + productId), Cart.EMAIL + " = ?", new String[]{email});
        return (deleteCount == 1);
    }

    // ***** checks product in cart *****
    public boolean checkInCart(int productId, String email)
    {
        boolean result = false;
        try (Cursor cursor = context.getContentResolver().query(Uri.parse(Cart.CONTENT_URI + "/" + productId), Cart.PROJECTION, Cart.EMAIL + " = ?", new String[]{email}, null))
        {
            if (cursor != null)
            {
                result = (cursor.getCount() == 1);
            }
        } catch (Exception e)
        {
            Log.e("DAO", "Error checking product in cart", e);
        }
        return result;
    }

    // ***** adds product to cart *****
    public boolean addToCart(int productId, String email)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Cart.EMAIL, email);
        contentValues.put(Cart.PRODUCT_ID, productId);
        Uri insertedUri = context.getContentResolver().insert(Cart.CONTENT_URI, contentValues);
        return insertedUri != null;
    }

    // ***** cart methods ends *****

    // ***** Account methods starts *****

    // ***** checks for account *****
    public boolean hasAccount(String emailString)
    {
        boolean result = false;
        try (Cursor cursor = context.getContentResolver().query(
                Uri.parse(Account.CONTENT_URI + "/" + emailString), Account.PROJECTION, null, null, null))
        {
            if (cursor != null)
            {
                result = (cursor.getCount() == 1);
            }
        } catch (Exception e)
        {
            Log.e("DAO", "Error checking has account", e);
        }
        return result;
    }

    // ***** returns account if available *****
    public Account getAccountIfAvailable(String emailString, String passwordString)
    {
        Account account = new Account();
        try (Cursor cursor = context.getContentResolver().query(
                Account.CONTENT_URI, Account.PROJECTION, Account.EMAIL + " = ? AND " + Account.PASSWORD + " = ?", new String[]{emailString, passwordString}, null))
        {
            if (cursor != null && cursor.getCount() == 1)
            {
                while (cursor.moveToNext())
                {
                    account.setName(cursor.getString(cursor.getColumnIndex(Account.NAME)));
                    account.setEmail(cursor.getString(cursor.getColumnIndex(Account.EMAIL)));
                    account.setPassword(cursor.getString(cursor.getColumnIndex(Account.PASSWORD)));
                    account.setPhoneNumber(cursor.getString(cursor.getColumnIndex(Account.PHONE_NUMBER)));
                    account.setDateOfBirth(cursor.getString(cursor.getColumnIndex(Account.DATE_OF_BIRTH)));
                    account.setDeliveryAddress(cursor.getString(cursor.getColumnIndex(Account.DELIVERY_ADDRESS)));
                }
            } else
            {
                account = null;
            }
        } catch (Exception e)
        {
            Log.e("DAO", "Error fetching account", e);
        }
        return account;
    }

    // ***** adds account *****
    public boolean addAccount(Account account)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Account.NAME, account.getName());
        contentValues.put(Account.EMAIL, account.getEmail());
        contentValues.put(Account.PASSWORD, account.getPassword());
        contentValues.put(Account.PHONE_NUMBER, account.getPhoneNumber());
        contentValues.put(Account.DATE_OF_BIRTH, account.getDateOfBirth());
        contentValues.put(Account.DELIVERY_ADDRESS, "");
        Uri insertedUri = context.getContentResolver().insert(Account.CONTENT_URI, contentValues);
        return insertedUri != null;
    }

    public boolean updateAddressForAccount(String address, String email)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Account.DELIVERY_ADDRESS, address);
        int updateCount = context.getContentResolver().update(Uri.parse(Account.CONTENT_URI + "/" + email), contentValues, null, null);
        return (updateCount == 1);
    }

    // ***** account methods ends *****

    // ***** banner methods starts *****

    // ***** adds banners *****
    public int addBanners(List<PromotionBanner> banners)
    {
        Gson gson = new Gson();
        ContentProviderResult[] contentProviderResults = null;
        ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<>();

        for (PromotionBanner banner : banners)
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(PromotionBanner._ID, banner.getId());
            contentValues.put(PromotionBanner.BANNER_URL, banner.getBanner());
            String productIdListString = gson.toJson(banner.getProductIds());
            contentValues.put(PromotionBanner.PRODUCTS_RELATED, productIdListString);
            contentProviderOperations.add(ContentProviderOperation.newInsert(PromotionBanner.CONTENT_URI).withValues(contentValues).withYieldAllowed(true).build());
        }

        try
        {
            contentProviderResults = context.getContentResolver().applyBatch(ZohokartContentProvider.AUTHORITY, contentProviderOperations);
        } catch (RemoteException | OperationApplicationException e)
        {
            Log.e("DAO", "Error adding categories ", e);
        }

        return contentProviderResults != null ? contentProviderResults.length : 0;
    }

    // ***** returns banners *****
    public List<PromotionBanner> getBanners()
    {
        Gson gson = new Gson();
        List<PromotionBanner> banners = new ArrayList<>();
        try (Cursor cursor = context.getContentResolver().query(
                PromotionBanner.CONTENT_URI, PromotionBanner.PROJECTION, null, null, null))
        {
            if (cursor != null)
            {
                while (cursor.moveToNext())
                {
                    int id = cursor.getInt(cursor.getColumnIndex(PromotionBanner._ID));
                    String banner_url = cursor.getString(cursor.getColumnIndex(PromotionBanner.BANNER_URL));
                    String productsIdsJsonString = cursor.getString(cursor.getColumnIndex(PromotionBanner.PRODUCTS_RELATED));
                    List<Integer> productIds = gson.fromJson(productsIdsJsonString, new TypeToken<List<Integer>>()
                    {
                    }.getType());
                    PromotionBanner banner = new PromotionBanner(id, banner_url, productIds);
                    banners.add(banner);
                }
            }
        } catch (Exception e)
        {
            Log.e("DAO", "Error fetching banners", e);
        }
        return banners;
    }

    // ***** banner methods ends *****

    // ***** specification methods starts *****

    // ***** adds specifications *****
    public int addSpecifications(Map<String, List<SpecificationGroup>> specifications)
    {

        Gson gson = new Gson();
        ContentProviderResult[] contentProviderResults = null;
        ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<>();
        ContentValues contentValues = new ContentValues();
        String specificationString;

        for (Map.Entry<String, List<SpecificationGroup>> specificationEntry : specifications.entrySet())
        {

            contentValues.put(SpecificationGroup.PRODUCT_ID, Integer.parseInt(specificationEntry.getKey().trim()));

            for (SpecificationGroup specificationGroup : specificationEntry.getValue())
            {

                contentValues.put(SpecificationGroup.GROUP_NAME, specificationGroup.getName());
                specificationString = gson.toJson(specificationGroup.getSpecifications());
                contentValues.put(SpecificationGroup.SPECIFICATIONS, specificationString);
                contentProviderOperations.add(ContentProviderOperation.newInsert(SpecificationGroup.CONTENT_URI).withValues(contentValues).withYieldAllowed(true).build());

            }

        }
        try
        {
            contentProviderResults = context.getContentResolver().applyBatch(ZohokartContentProvider.AUTHORITY, contentProviderOperations);
        } catch (RemoteException | OperationApplicationException e)
        {
            Log.e("DAO", "Error adding categories ", e);
        }

        return contentProviderResults != null ? contentProviderResults.length : 0;

    }

    // ***** returns specifications for a product *****
    public Map<String, List<Specification>> getSpecificationsByProductId(int productId)
    {

        Map<String, List<Specification>> specificationMap = new LinkedHashMap<>();
        Gson gson = new Gson();
        List<Specification> specifications;
        try (Cursor cursor = context.getContentResolver().query(
                Uri.parse(SpecificationGroup.CONTENT_URI + "/" + productId), SpecificationGroup.PROJECTION, null, null, null))
        {

            while (cursor.moveToNext())
            {
                if (cursor.getInt(cursor.getColumnIndex(SpecificationGroup.PRODUCT_ID)) == productId)
                {
                    specifications = gson.fromJson(cursor.getString(cursor.getColumnIndex(SpecificationGroup.SPECIFICATIONS)), new TypeToken<List<Specification>>()
                    {
                    }.getType());
                    specificationMap.put(cursor.getString(cursor.getColumnIndex(SpecificationGroup.GROUP_NAME)), specifications);
                }
            }

            Log.d("Cursor", String.valueOf(cursor.getCount()));

        } catch (Exception e)
        {
            Log.e("DAO", "Error fetching specifications", e);
        }

        return specificationMap;
    }

    // ***** specification methods ends *****

    // ***** method to get products by searchString *****
    public List<Product> getProductsBySearchString(String queryString)
    {
        List<Product> products = new ArrayList<>();
        try (Cursor cursor = context.getContentResolver().query(
                Product.CONTENT_URI, Product.PROJECTION, Product.TITLE + " LIKE ?",
                new String[]{String.valueOf("%" + queryString + "%")}, null))
        {
            if (cursor != null)
            {
                while (cursor.moveToNext())
                {
                    products.add(getProductFromCursor(cursor));
                }
            }

        } catch (Exception e)
        {
            Log.e("DAO", "Error getting products for a sub category");
        }
        return products;
    }
    // *** searchString method ends ***

    // ***** Filtering methods *****

    public Map<String, Map<String, FilterPair>> getBrandsForFilter(int subCategoryId)
    {
        Map<String, Map<String, FilterPair>> resultMap = new HashMap<>();
        Map<String, FilterPair> brands = new HashMap<>();
        List<String> args;

        try (Cursor cursor = context.getContentResolver().query(Product.CONTENT_URI, new String[]{"DISTINCT " + Product.BRAND}, Product.SUB_CATEGORY_ID + " = ?", new String[]{String.valueOf(subCategoryId)}, null))
        {
            if (cursor != null)
            {
                while (cursor.moveToNext())
                {
                    args = new ArrayList<>();
                    args.add(cursor.getString(cursor.getColumnIndex(Product.BRAND)));
                    brands.put(cursor.getString(cursor.getColumnIndex(Product.BRAND)), new FilterPair(Product.FILTER_BRAND, args));
                }
            }
        }
        catch (Exception e)
        {
            Log.e("DAO", "Error getting products for a sub category");
        }
        resultMap.put("Brand", brands);
        return resultMap;
    }

    public List<Product> getFilteredProducts(List<FilterPair> filterPairs, int subCategoryId)
    {
        List<Product> products = new ArrayList<>(), tempProducts = new ArrayList<>();
        List<String> brands = new ArrayList<>();
        String[] selectionArgsAsArray;
        String tempString, paramString = " ?", comma = ",";
        boolean priceValidated = false;

        if (filterPairs.size() > 0)
        {
            for (FilterPair filterPair : filterPairs)
            {
                if (filterPair.getSelectionString() != null)
                {
                    switch (filterPair.getSelectionString())
                    {
                        case Product.FILTER_BRAND:
                            brands.addAll(filterPair.getSelectionArgs());
                            break;
                        case Product.FILTER_PRICE_LESSER_THAN:
                            selectionArgsAsArray = filterPair.getSelectionArgs().toArray(new String[filterPair.getSelectionArgs().size() + 1]);
                            selectionArgsAsArray[selectionArgsAsArray.length - 1] = String.valueOf(subCategoryId);
                            tempString = Product.FILTER_PRICE_LESSER_THAN + " AND " + Product.SUB_CATEGORY_ID + " = ?";
                            products.addAll(getProductsByPriceOrBrand(tempString, selectionArgsAsArray));
                            priceValidated = true;
                            break;
                        case Product.FILTER_PRICE_RANGE:
                            selectionArgsAsArray = filterPair.getSelectionArgs().toArray(new String[filterPair.getSelectionArgs().size() + 1]);
                            selectionArgsAsArray[selectionArgsAsArray.length - 1] = String.valueOf(subCategoryId);
                            tempString = Product.FILTER_PRICE_RANGE + " AND " + Product.SUB_CATEGORY_ID + " = ?";
                            products.addAll(getProductsByPriceOrBrand(tempString, selectionArgsAsArray));
                            priceValidated = true;
                            break;
                        case Product.FILTER_PRICE_GREATER_THAN:
                            selectionArgsAsArray = filterPair.getSelectionArgs().toArray(new String[filterPair.getSelectionArgs().size() + 1]);
                            selectionArgsAsArray[selectionArgsAsArray.length - 1] = String.valueOf(subCategoryId);
                            tempString = Product.FILTER_PRICE_GREATER_THAN + " AND " + Product.SUB_CATEGORY_ID + " = ?";
                            products.addAll(getProductsByPriceOrBrand(tempString, selectionArgsAsArray));
                            priceValidated = true;
                            break;
                    }
                }
            }
        }
        else
        {
            products = getProductsForSubCategory(subCategoryId);
        }

        if (!priceValidated)
        {
            if (products.size() == 0)
            {
                tempString = Product.BRAND + " IN (";
                for (String brand : brands)
                {
                    if (brands.indexOf(brand) == 0)
                    {
                        tempString = tempString + paramString;
                        Log.d("BRAND_IN", tempString);
                    }
                    else
                    {
                        tempString = tempString + comma + paramString;
                        Log.d("BRAND_IN", tempString);
                    }
                }
                tempString = tempString + ")";
                Log.d("BRAND_IN", tempString);

                selectionArgsAsArray = brands.toArray(new String[brands.size()+1]);
                selectionArgsAsArray[selectionArgsAsArray.length-1] = String.valueOf(subCategoryId);
                products.addAll(getProductsByPriceOrBrand(tempString + " AND " + Product.SUB_CATEGORY_ID + " = ?", selectionArgsAsArray));
            }
        }

        if (brands.size() > 0)
        {
            tempProducts.addAll(products);
            for (Product product : tempProducts)
            {
                if (!brands.contains(product.getBrand()))
                {
                    products.remove(product);
                }
            }
        }

        return products;
    }

    public List<Product> getProductsByPriceOrBrand(String selectionString, String[] selectionArgs)
    {
        List<Product> products = new ArrayList<>();

        try (Cursor cursor = context.getContentResolver().query(
                Product.CONTENT_URI, Product.PROJECTION, selectionString, selectionArgs, null))
        {
            if (cursor != null)
            {
                Log.d("PRODUCTS", String.valueOf(cursor.getCount()));
                while (cursor.moveToNext())
                {
                    products.add(getProductFromCursor(cursor));
                }
            }
        }
        catch (Exception e)
        {
            Log.e("DAO", "Error getting products for price lesser than.");
        }
        return products;
    }

    // ***** methods for filter ends *****

    // ***** methods for payment cards starts *****

    // *** to add a new card for a email id ***
    public boolean addCard(PaymentCard paymentCard, String email)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PaymentCard.EMAIL, email);
        contentValues.put(PaymentCard.CARD_NUMBER, paymentCard.getCardNumber());
        contentValues.put(PaymentCard.CARD_TYPE, paymentCard.getCardType());
        contentValues.put(PaymentCard.NAME_ON_CARD, paymentCard.getNameOnCard());
        contentValues.put(PaymentCard.EXPIRY, paymentCard.getExpiryDate());
        Uri insertedUri = context.getContentResolver().insert(PaymentCard.CONTENT_URI, contentValues);
        return insertedUri != null;
    }

    public List<PaymentCard> getCards(String email)
    {
        List<PaymentCard> cards = new ArrayList<>();
        PaymentCard paymentCard;
        try (Cursor cursor = context.getContentResolver().query(
                PaymentCard.CONTENT_URI, PaymentCard.PROJECTION, PaymentCard.EMAIL + " = ?", new String[]{email}, null))
        {
            if (cursor != null)
            {
                while (cursor.moveToNext())
                {
                    paymentCard = new PaymentCard();
                    paymentCard.setEmail(email);
                    paymentCard.setCardNumber(cursor.getString(cursor.getColumnIndex(PaymentCard.CARD_NUMBER)));
                    paymentCard.setCardType(cursor.getString(cursor.getColumnIndex(PaymentCard.CARD_TYPE)));
                    paymentCard.setNameOnCard(cursor.getString(cursor.getColumnIndex(PaymentCard.NAME_ON_CARD)));
                    paymentCard.setExpiryDate(cursor.getString(cursor.getColumnIndex(PaymentCard.EXPIRY)));
                    cards.add(paymentCard);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return cards;
    }

    public boolean removeFromPaymentCards(String cardNumber, String email)
    {
        int deleteCount = context.getContentResolver().delete(Uri.parse(PaymentCard.CONTENT_URI + "/" + email), PaymentCard.CARD_NUMBER + " = ?", new String[]{cardNumber});
        return (deleteCount == 1);
    }

    public int addOrderLineItems(List<OrderLineItem> orderLineItems)
    {
        ContentProviderResult[] contentProviderResults = null;
        ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<>();
        for (OrderLineItem orderLineItem : orderLineItems)
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(OrderLineItem.ORDER_ID, orderLineItem.getOrderId());
            contentValues.put(OrderLineItem.PRODUCT_ID, orderLineItem.getProductId());
            contentValues.put(OrderLineItem.QUANTITY, orderLineItem.getQuantity());
            contentProviderOperations.add(ContentProviderOperation.newInsert(OrderLineItem.CONTENT_URI).withValues(contentValues).withYieldAllowed(true).build());
        }
        try
        {
            contentProviderResults = context.getContentResolver().applyBatch(ZohokartContentProvider.AUTHORITY, contentProviderOperations);
        }
        catch (RemoteException | OperationApplicationException e)
        {
            Log.e("DAO", "Error adding order line items", e);
        }
        return contentProviderResults != null ? contentProviderResults.length : 0;
    }

    public List<OrderLineItem> getOrderLineItemsForOrderId(String orderId)
    {
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        OrderLineItem orderLineItem;
        try (Cursor cursor = context.getContentResolver().query(
                Uri.parse(OrderLineItem.CONTENT_URI + "/" + orderId), OrderLineItem.PROJECTION, null, null, null))
        {
            if (cursor != null)
            {
                while (cursor.moveToNext())
                {
                    orderLineItem = new OrderLineItem();
                    orderLineItem.setOrderId(cursor.getString(cursor.getColumnIndex(OrderLineItem.ORDER_ID)));
                    orderLineItem.setProductId(cursor.getInt(cursor.getColumnIndex(OrderLineItem.PRODUCT_ID)));
                    orderLineItem.setQuantity(cursor.getInt(cursor.getColumnIndex(OrderLineItem.QUANTITY)));
                    orderLineItems.add(orderLineItem);
                }

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return orderLineItems;
    }

    public boolean addOrder(Order order, String email)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Order._ID, order.getId());
        contentValues.put(Order.EMAIL, email);
        contentValues.put(Order.NUMBER_OF_PRODUCTS, order.getNumberOfProducts());
        contentValues.put(Order.TOTAL_PRICE, order.getTotalPrice());
        contentValues.put(Order.ORDER_STATUS, order.getOrderStatus());
        Uri insertedUri = context.getContentResolver().insert(Order.CONTENT_URI, contentValues);
        return insertedUri != null;
    }

    public Order getOrderForOrderId(String orderId)
    {
        Order order = new Order();
        try (Cursor cursor = context.getContentResolver().query(
                Order.CONTENT_URI, Order.PROJECTION, Order._ID + " = ?", new String[]{orderId}, null))
        {
            if (cursor != null)
            {
                while (cursor.moveToNext())
                {
                    order.setId(cursor.getString(cursor.getColumnIndex(Order._ID)));
                    order.setEmail(cursor.getString(cursor.getColumnIndex(Order.EMAIL)));
                    order.setDate(cursor.getString(cursor.getColumnIndex(Order.ADDED_ON)));
                    order.setNumberOfProducts(cursor.getInt(cursor.getColumnIndex(Order.NUMBER_OF_PRODUCTS)));
                    order.setTotalPrice(cursor.getDouble(cursor.getColumnIndex(Order.TOTAL_PRICE)));
                    order.setOrderStatus(cursor.getString(cursor.getColumnIndex(Order.ORDER_STATUS)));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return order;
    }

    public List<Order> getOrdersForEmail(String email)
    {
        List<Order> orders = new ArrayList<>();
        Order order;
        try (Cursor cursor = context.getContentResolver().query(
                Uri.parse(Order.CONTENT_URI + "/" + email), Order.PROJECTION, null, null, null))
        {
            if (cursor != null)
            {
                while (cursor.moveToNext())
                {
                    order = new Order();
                    order.setId(cursor.getString(cursor.getColumnIndex(Order._ID)));
                    order.setEmail(email);
                    order.setDate(cursor.getString(cursor.getColumnIndex(Order.ADDED_ON)));
                    order.setNumberOfProducts(cursor.getInt(cursor.getColumnIndex(Order.NUMBER_OF_PRODUCTS)));
                    order.setTotalPrice(cursor.getDouble(cursor.getColumnIndex(Order.TOTAL_PRICE)));
                    order.setOrderStatus(cursor.getString(cursor.getColumnIndex(Order.ORDER_STATUS)));
                    orders.add(order);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return orders;
    }

    public boolean removeProductsInCart(String email)
    {
        int deleteCount = context.getContentResolver().delete(Cart.CONTENT_URI, Cart.EMAIL + " = ?", new String[]{email});
        return (deleteCount > 0);
    }

    public boolean cancelOrder(String orderId)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Order.ORDER_STATUS, Order.ORDER_CANCELLED);
        int updateCount = context.getContentResolver().update(Uri.parse(Order.CONTENT_URI + "/" + orderId), contentValues, null, null);
        return (updateCount == 1);
    }
}