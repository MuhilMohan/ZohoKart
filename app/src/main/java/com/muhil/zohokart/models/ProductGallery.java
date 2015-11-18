package com.muhil.zohokart.models;

import android.content.ContentResolver;
import android.net.Uri;

import com.google.gson.annotations.SerializedName;
import com.muhil.zohokart.utils.ZohokartContentProvider;

import java.util.List;

/**
 * Created by muhil-ga42 on 13/11/15.
 */
public class ProductGallery
{

    public static final String TABLE_NAME = "product_galleries";
    public static final String PRODUCT_ID = "product_id";
    public static final String IMAGE_LINKS = "imageLinks";

    public static final String[] PROJECTION = {PRODUCT_ID, IMAGE_LINKS};

    public static final Uri CONTENT_URI = Uri.withAppendedPath(ZohokartContentProvider.CONTENT_URI, TABLE_NAME);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/com.muhil.zohokart.models.ProductGallery";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/com.muhil.zohokart.models.ProductGallery";

    @SerializedName("product_id")
    private int productId;
    @SerializedName("image_links")
    private List<String> imageLinks;

    public ProductGallery() {
    }

    public ProductGallery(int productId, List<String> imageLinks) {
        this.productId = productId;
        this.imageLinks = imageLinks;
    }

    public int getProductId()
    {
        return productId;
    }

    public void setProductId(int productId)
    {
        this.productId = productId;
    }

    public List<String> getImageLinks()
    {
        return imageLinks;
    }

    public void setImageLinks(List<String> imageLinks)
    {
        this.imageLinks = imageLinks;
    }
}
