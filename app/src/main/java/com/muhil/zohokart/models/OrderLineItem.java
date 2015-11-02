package com.muhil.zohokart.models;

import android.content.ContentResolver;
import android.net.Uri;

import com.muhil.zohokart.utils.ZohokartContentProvider;

/**
 * Created by muhil-ga42 on 31/10/15.
 */
public class OrderLineItem
{
    public static final String TABLE_NAME = "order_line_item";
    public static final String ORDER_ID = "order_id";
    public static final String PRODUCT_ID = "product_id";
    public static final String QUANTITY = "quantity";

    public static final String[] PROJECTION = {ORDER_ID, PRODUCT_ID, QUANTITY};

    public static final Uri CONTENT_URI = Uri.withAppendedPath(ZohokartContentProvider.CONTENT_URI, TABLE_NAME);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/com.muhil.zohokart.models.OrderLineItem";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/com.muhil.zohokart.models.OrderLineItem";

    private String orderId;
    private int productId;
    private int quantity;

    public OrderLineItem()
    {

    }

    public OrderLineItem(String orderId, int productId)
    {
        this.orderId = orderId;
        this.productId = productId;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public int getProductId()
    {
        return productId;
    }

    public void setProductId(int productId)
    {
        this.productId = productId;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }
}
