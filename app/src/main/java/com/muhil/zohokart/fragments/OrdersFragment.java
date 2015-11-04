package com.muhil.zohokart.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.muhil.zohokart.R;
import com.muhil.zohokart.models.Account;
import com.muhil.zohokart.models.Order;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.utils.ImageLoader;
import com.muhil.zohokart.utils.ZohokartDAO;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersFragment extends android.support.v4.app.Fragment
{

    ZohokartDAO zohokartDAO;
    ImageLoader imageLoader;
    View rootView;
    String email;
    LinearLayout ordersHolder;
    CardView orderItem;
    LayoutInflater layoutInflater;
    DecimalFormat decimalFormat = new DecimalFormat("#.00");
    OrderCommunicator orderCommunicator;

    public static OrdersFragment getInstance(String email)
    {
        OrdersFragment ordersFragment = new OrdersFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Account.EMAIL, email);
        ordersFragment.setArguments(bundle);
        return ordersFragment;
    }

    public OrdersFragment()
    {
        // Required empty public constructor
    }

    public void setCommunicator(OrderCommunicator orderCommunicator)
    {
        this.orderCommunicator = orderCommunicator;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        zohokartDAO = new ZohokartDAO(getActivity());
        imageLoader = new ImageLoader(getActivity());
        email = getArguments().getString(Account.EMAIL);
        layoutInflater = LayoutInflater.from(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_orders, container, false);
        ordersHolder = (LinearLayout) rootView.findViewById(R.id.orders_holder);
        new DisplayingOrdersTask().execute(email);

        return rootView;
    }

    class DisplayingOrdersTask extends AsyncTask<String, Void, Void>
    {

        List<Order> orders;
        DateFormat dateConversionDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        DateFormat dateDisplayFormat = new SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault());
        Date date;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            (rootView.findViewById(R.id.orders_empty)).setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(String... email)
        {
            orders = zohokartDAO.getOrdersForEmail(email[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            ordersHolder.removeAllViews();
            if (orders.size() > 0)
            {
                for (final Order order : orders)
                {
                    orderItem = (CardView) layoutInflater.inflate(R.layout.order_item, ordersHolder, false);
                    try
                    {
                        date = dateConversionDateFormat.parse(order.getOrderedDate());
                        String dateDisplayString = dateDisplayFormat.format(date);
                        ((TextView) orderItem.findViewById(R.id.ordered_date)).setText(dateDisplayString);
                    }
                    catch (ParseException e)
                    {
                        e.printStackTrace();
                    }
                    ((TextView) orderItem.findViewById(R.id.total_products)).setText(String.valueOf(order.getNumberOfProducts()));
                    ((TextView) orderItem.findViewById(R.id.order_total_price)).setText("Rs. " + decimalFormat.format(order.getTotalPrice()));
                    orderItem.setTag(order.getId());
                    ((TextView) orderItem.findViewById(R.id.order_status)).setText(order.getOrderStatus());

                    orderItem.setOnClickListener(
                            new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    String orderId = (String) v.getTag();
                                    orderCommunicator.openOrderLineItemsForOrderId(orderId);
                                }
                            }
                    );

                    ordersHolder.addView(orderItem);
                }
            }
            else
            {
                (rootView.findViewById(R.id.orders_empty)).setVisibility(View.VISIBLE);
            }
        }
    }

    public interface OrderCommunicator
    {
        void openOrderLineItemsForOrderId(String orderId);
        void showMainFragment();
    }

}
