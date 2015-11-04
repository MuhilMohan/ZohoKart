package com.muhil.zohokart.models;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import com.muhil.zohokart.utils.ZohokartContentProvider;

import java.util.Date;

/**
 * Created by muhil-ga42 on 31/10/15.
 */
public class Order
{

    public static final String ORDER_KEY = "Order_000";
    public static int OLD_ORDER_ID = 1;

    public static final String ORDER_PROCESSING = "PROCESSING";
    public static final String ORDER_CANCELLED = "CANCELLED";
    public static final String ORDER_DELIVERED = "DELIVERED";

    public static final String TABLE_NAME = "orders";
    public static final String _ID = "_id";
    public static final String EMAIL = "email";
    public static final String ADDED_ON = "added_on";
    public static final String EXPECTED_DELIVERY_DATE = "expected_delivery_date";
    public static final String NUMBER_OF_PRODUCTS = "number_of_products";
    public static final String TOTAL_PRICE = "total_price";
    public static final String ORDER_STATUS = "order_status";

    public static final String[] PROJECTION = {_ID, EMAIL, NUMBER_OF_PRODUCTS, TOTAL_PRICE, ORDER_STATUS, ADDED_ON, EXPECTED_DELIVERY_DATE};

    public static final Uri CONTENT_URI = Uri.withAppendedPath(ZohokartContentProvider.CONTENT_URI, TABLE_NAME);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/com.muhil.zohokart.models.Order";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/com.muhil.zohokart.models.Order";

    private String id;
    private String email;
    private String orderedDate;
    private String expectedDeliveryDate;
    private int numberOfProducts;
    private double totalPrice;
    private String orderStatus;

    public Order()
    {}

    public Order(String id, String email, String orderedDate, String expectedDeliveryDate, int numberOfProducts, double totalPrice, String orderStatus)
    {
        this.id = id;
        this.email = email;
        this.orderedDate = orderedDate;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.numberOfProducts = numberOfProducts;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
    }

    public String getOrderedDate()
    {
        return orderedDate;
    }

    public void setOrderedDate(String orderedDate)
    {
        this.orderedDate = orderedDate;
    }

    public String getExpectedDeliveryDate()
    {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(String expectedDeliveryDate)
    {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public int getNumberOfProducts()
    {
        return numberOfProducts;
    }

    public void setNumberOfProducts(int numberOfProducts)
    {
        this.numberOfProducts = numberOfProducts;
    }

    public String getOrderStatus()
    {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus)
    {
        this.orderStatus = orderStatus;
    }

    public double getTotalPrice()
    {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice)
    {
        this.totalPrice = totalPrice;
    }

    public static void incrementOrderId()
    {
        OLD_ORDER_ID++;
        Log.d("ORDER_ID", "" + OLD_ORDER_ID);
    }

}
