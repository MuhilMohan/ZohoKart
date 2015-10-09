package com.muhil.zohokart.models;

import android.content.ContentResolver;
import android.net.Uri;

import com.muhil.zohokart.utils.ZohokartContentProvider;

/**
 * Created by muhil-ga42 on 08/10/15.
 */
public class Wishlist {

    public static final String TABLE_NAME = "wishlist";
    public static final String PRODUCT_ID = "product_id";
    public static final String ADDED_ON = "added_on";

    public static final String[] PROJECTION = {PRODUCT_ID, ADDED_ON};

    public static final Uri CONTENT_URI = Uri.withAppendedPath(ZohokartContentProvider.CONTENT_URI, TABLE_NAME);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/com.muhil.zohokart.models.Wishlist";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/com.muhil.zohokart.models.Wishlist";

}
