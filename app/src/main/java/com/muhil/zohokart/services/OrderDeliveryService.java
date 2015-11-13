package com.muhil.zohokart.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.muhil.zohokart.MainActivity;
import com.muhil.zohokart.R;
import com.muhil.zohokart.activities.ProfileActivity;
import com.muhil.zohokart.models.Order;
import com.muhil.zohokart.utils.ZohokartDAO;

/**
 * Created by muhil-ga42 on 11/11/15.
 */
public class OrderDeliveryService extends Service
{

    Context context;
    NotificationManager notificationManager;
    ZohokartDAO zohokartDAO;

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        zohokartDAO = new ZohokartDAO(getApplicationContext());
        context = this.getApplicationContext();
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId)
    {

        Order order = (Order) intent.getSerializableExtra("order");

        if (order != null)
        {
            if (zohokartDAO.changeOrderDeliveryStatus(order.getId()))
            {
                notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

                Intent orderIntent = new Intent(context, ProfileActivity.class);
                orderIntent.putExtra("orderLineItemsFragment", true);
                orderIntent.putExtra("orderId", order.getId());

                PendingIntent orderService = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), orderIntent, 0);

                Notification notification;

                Notification.Builder deliveryNotificationBuilder = new Notification.Builder(context);
                deliveryNotificationBuilder.setContentTitle("Order delivered");
                deliveryNotificationBuilder.setContentText("Your order of " + order.getNumberOfProducts() + " items with id " + order.getId() + " is delivered.");
                deliveryNotificationBuilder.setSmallIcon(R.mipmap.ion_android_notifications_54_0_ffffff_none);
                deliveryNotificationBuilder.setContentIntent(orderService);

                notification = deliveryNotificationBuilder.build();

                notification.defaults = Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE;
                notification.flags = Notification.FLAG_AUTO_CANCEL;

                notificationManager.notify(0, notification);

            }
        }

        super.onStart(intent, startId);
    }
}
