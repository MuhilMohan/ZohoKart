package com.muhil.zohokart.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.muhil.zohokart.R;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.utils.ZohokartDAO;

import java.util.List;

/**
 * Created by muhil-ga42 on 12/11/15.
 */
public class CartNotifyService extends Service
{
    static Context context;
    NotificationManager notificationManager;
    ZohokartDAO zohokartDAO;
    String email;
    List<Product> productsFromCart;

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        context = this.getApplicationContext();
        zohokartDAO = new ZohokartDAO(context);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        email = intent.getStringExtra("email");
        productsFromCart = zohokartDAO.getProductsFromCart(email);

        if (productsFromCart.size() > 0)
        {
            Log.d("CART_NOTIFY", "" + productsFromCart.size());
            Notification notification;

            Notification.Builder deliveryNotificationBuilder = new Notification.Builder(context);
            deliveryNotificationBuilder.setContentTitle("Cart intimation");
            deliveryNotificationBuilder.setSmallIcon(R.mipmap.ion_android_notifications_54_0_ffffff_none);

            if (productsFromCart.size() == 1)
            {
                deliveryNotificationBuilder.setContentText(productsFromCart.get(0).getTitle()
                        + " is waiting for you in the cart");
            }
            else if (productsFromCart.size() > 1)
            {
                deliveryNotificationBuilder.setContentText(productsFromCart.get(0).getTitle() + " and "
                        + (productsFromCart.size()-1) + " more products is waiting for you in the cart");
            }

            notification = deliveryNotificationBuilder.build();

            notification.defaults = Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE;
            notification.flags = Notification.FLAG_AUTO_CANCEL;

            notificationManager.notify(0, notification);
        }

        return super.onStartCommand(intent, flags, startId);
    }
}
