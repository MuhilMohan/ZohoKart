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
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.models.PromotionBanner;
import com.muhil.zohokart.models.SubCategory;
import com.muhil.zohokart.models.Wishlist;
import com.muhil.zohokart.models.specification.Specification;
import com.muhil.zohokart.models.specification.SpecificationGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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

    // ***** product method ends *****

    // ***** wishlist methods starts *****

    // ***** returns products from wishlist *****
    public List<Product> getProductsFromWishlist()
    {

        List<Product> products = new ArrayList<>();
        List<Integer> productIdsInWishList = new ArrayList<>();
        try (Cursor cursor = context.getContentResolver().query(
                Wishlist.CONTENT_URI, Wishlist.PROJECTION, null, null, null))
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
            for (Integer productId : productIdsInWishList)
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
                } catch (Exception e)
                {
                    Log.d("DAO", "Error fetching product in wishlist ", e);
                }
            }
        }

        return products;
    }

    // ***** checks wishlist for Product *****
    public boolean checkInWishlist(int productId)
    {

        boolean result = false;
        try (Cursor cursor = context.getContentResolver().query(
                Uri.parse(Wishlist.CONTENT_URI + "/" + productId), Wishlist.PROJECTION, null, null, null))
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
    public boolean addToWishlist(int productId)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Wishlist.PRODUCT_ID, productId);
        Uri insertedUri = context.getContentResolver().insert(Wishlist.CONTENT_URI, contentValues);
        return insertedUri != null;
    }

    // ***** removes product from wishlist *****
    public boolean removeFromWishList(int productId)
    {
        int deleteCount = context.getContentResolver().delete(Uri.parse(Wishlist.CONTENT_URI + "/" + productId), null, null);
        return (deleteCount == 1);
    }

    // ***** wishlist methods ends *****

    // ***** returns products from cart *****
    public List<Product> getProductsFromCart()
    {

        List<Product> products = new ArrayList<>();
        List<Integer> productIdsInCart = new ArrayList<>();
        try (Cursor cursor = context.getContentResolver().query(
                Cart.CONTENT_URI, Cart.PROJECTION, null, null, null))
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

        if (!productIdsInCart.isEmpty())
        {
            for (Integer productId : productIdsInCart)
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

    // ***** updates quantity in product within cart *****
    public boolean updateQuantityOfProductInCart(int quantity, int productId)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Cart.QUANTITY, quantity);
        int updateCount = context.getContentResolver().update(Uri.parse(Cart.CONTENT_URI + "/" + productId), contentValues, null, null);
        return (updateCount == 1);
    }

    // ***** returns quantity of a product from cart *****
    public int getQuantityofProductInCart(int productId)
    {
        int result = 0;
        if (checkInCart(productId))
        {
            try (Cursor cursor = context.getContentResolver().query(
                    Uri.parse(Cart.CONTENT_URI + "/" + productId), Cart.PROJECTION, null, null, null))
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
    public boolean removeFromCart(int productId)
    {
        int deleteCount = context.getContentResolver().delete(Uri.parse(Cart.CONTENT_URI + "/" + productId), null, null);
        return (deleteCount == 1);
    }

    // ***** checks product in cart *****
    public boolean checkInCart(int productId)
    {
        boolean result = false;
        try (Cursor cursor = context.getContentResolver().query(Uri.parse(Cart.CONTENT_URI + "/" + productId), Cart.PROJECTION, null, null, null))
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
    public boolean addToCart(int productId)
    {
        ContentValues contentValues = new ContentValues();
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
        Uri insertedUri = context.getContentResolver().insert(Account.CONTENT_URI, contentValues);
        return insertedUri != null;
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

    // Filtering methods

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
        List<String> selectionArgs, brands = new ArrayList<>();
        String[] selectionArgsAsArray;
        String tempString;
        int subStringIndex, MAX = 0, MIN = 0;

        for (FilterPair filterPair : filterPairs)
        {
            if (filterPair.getSelectionString() != null)
            {
                if (filterPair.getSelectionString().equals(Product.FILTER_BRAND))
                {
                    brands.addAll(filterPair.getSelectionArgs());
                }
                else if (filterPair.getSelectionString().equals(Product.FILTER_PRICE_LESSER_THAN))
                {
                    selectionArgsAsArray = filterPair.getSelectionArgs().toArray(new String[filterPair.getSelectionArgs().size()+1]);
                    selectionArgsAsArray[selectionArgsAsArray.length-1] = String.valueOf(subCategoryId);
                    tempString = Product.FILTER_PRICE_LESSER_THAN + " AND " + Product.SUB_CATEGORY_ID + " = ?";
                    products.addAll(getProductsByPriceAndSubCategory(tempString, selectionArgsAsArray));
                }
                else if (filterPair.getSelectionString().equals(Product.FILTER_PRICE_RANGE))
                {
                    selectionArgsAsArray = filterPair.getSelectionArgs().toArray(new String[filterPair.getSelectionArgs().size()+1]);
                    selectionArgsAsArray[selectionArgsAsArray.length-1] = String.valueOf(subCategoryId);
                    tempString = Product.FILTER_PRICE_RANGE + " AND " + Product.SUB_CATEGORY_ID + " = ?";
                    products.addAll(getProductsByPriceAndSubCategory(tempString, selectionArgsAsArray));
                }
                else if (filterPair.getSelectionString().equals(Product.FILTER_PRICE_GREATER_THAN))
                {
                    selectionArgsAsArray = filterPair.getSelectionArgs().toArray(new String[filterPair.getSelectionArgs().size()+1]);
                    selectionArgsAsArray[selectionArgsAsArray.length-1] = String.valueOf(subCategoryId);
                    tempString = Product.FILTER_PRICE_GREATER_THAN + " AND " + Product.SUB_CATEGORY_ID + " = ?";
                    products.addAll(getProductsByPriceAndSubCategory(tempString, selectionArgsAsArray));
                }
            }
        }

        tempProducts.addAll(products);
        for (Product product : tempProducts)
        {
            if (!brands.contains(product.getBrand()))
            {
                products.remove(product);
            }
            else
            {

            }
        }

        return products;

    }

    public List<Product> getProductsByPriceAndSubCategory(String selectionString, String[] selectionArgs)
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

}