package com.muhil.zohokart.fragments;


import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import com.muhil.zohokart.models.OrderLineItem;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.utils.ImageLoader;
import com.muhil.zohokart.utils.ZohokartDAO;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderLineItemsFragment extends android.support.v4.app.Fragment
{

    ZohokartDAO zohokartDAO;
    ImageLoader imageLoader;
    String orderId;
    View rootView;
    LinearLayout orderedProductsHolder;
    LayoutInflater layoutInflater;
    CardView cardView;
    DecimalFormat decimalFormat = new DecimalFormat("#.00");

    public static OrderLineItemsFragment getInstance(String orderId)
    {
        OrderLineItemsFragment orderLineItemsFragment = new OrderLineItemsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Order._ID, orderId);
        orderLineItemsFragment.setArguments(bundle);
        return orderLineItemsFragment;
    }

    public OrderLineItemsFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        orderId = getArguments().getString(Order._ID);
        zohokartDAO = new ZohokartDAO(getActivity());
        imageLoader = new ImageLoader(getActivity());
        layoutInflater = LayoutInflater.from(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_order_line_items, container, false);
        orderedProductsHolder = (LinearLayout) rootView.findViewById(R.id.ordered_product_holder);
        orderedProductsHolder.removeAllViews();
        new OrderLineItemsAddingTask().execute(orderId);
        rootView.findViewById(R.id.cancel_order).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                        alertDialogBuilder.setTitle("");
                        alertDialogBuilder.setMessage("Do you want to cancel this order?");

                        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                zohokartDAO.cancelOrder(orderId);
                                (rootView.findViewById(R.id.expected_delivery_date_header)).setVisibility(View.GONE);
                                (rootView.findViewById(R.id.expected_delivery_date)).setVisibility(View.GONE);
                                new OrderLineItemsAddingTask().execute(orderId);
                            }
                        });
                        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        });

                        alertDialogBuilder.show();
                    }
                }
        );

        return rootView;
    }

    class OrderLineItemsAddingTask extends AsyncTask<String, Void, Void>
    {

        Order order;
        List<OrderLineItem> orderLineItems;
        List<Integer> productIds = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();
        List<Product> products;
        DateFormat dateConversionDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        DateFormat dateDisplayFormat = new SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault());
        Date date;

        @Override
        protected Void doInBackground(String... orderId)
        {
            order = zohokartDAO.getOrderForOrderId(orderId[0]);
            orderLineItems = zohokartDAO.getOrderLineItemsForOrderId(orderId[0]);
            Log.d("ORDER", "" + orderLineItems.size());
            for (OrderLineItem orderLineItem : orderLineItems)
            {
                productIds.add(orderLineItem.getProductId());
                quantities.add(orderLineItem.getQuantity());
            }
            products = zohokartDAO.getProductsForProductIds(productIds);
            Log.d("QUANTITY", "" + quantities.get(0));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            ((TextView) rootView.findViewById(R.id.order_id)).setText(order.getId());

            try
            {
                date = dateConversionDateFormat.parse(order.getOrderedDate());
                String dateDisplayString = dateDisplayFormat.format(date);
                ((TextView) rootView.findViewById(R.id.ordered_date)).setText(dateDisplayString);
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }

            ((TextView) rootView.findViewById(R.id.order_id)).setText(order.getId());
            ((TextView) rootView.findViewById(R.id.total_price)).setText("Rs. " + decimalFormat.format(order.getTotalPrice()) );
            ((TextView) rootView.findViewById(R.id.order_status)).setText(order.getOrderStatus());
            ((TextView) rootView.findViewById(R.id.expected_delivery_date)).setText(order.getExpectedDeliveryDate());
            if (order.getOrderStatus().equals(Order.ORDER_CANCELLED))
            {
                rootView.findViewById(R.id.cancel_order).setVisibility(View.GONE);
            }
            else
            {
                rootView.findViewById(R.id.cancel_order).setVisibility(View.VISIBLE);
            }

            for (Product product : products)
            {
                cardView = (CardView) layoutInflater.inflate(R.layout.ordered_product_item_row, orderedProductsHolder, false);
                ((TextView) cardView.findViewById(R.id.title)).setText(product.getTitle());
                ((TextView) cardView.findViewById(R.id.description)).setText(product.getDescription());
                ((TextView) cardView.findViewById(R.id.price)).setText(decimalFormat.format((product.getPrice() * quantities.get(products.indexOf(product)))));
                imageLoader.displayImage(product.getThumbnail(), (ImageView) cardView.findViewById(R.id.display_image));
                ((TextView) cardView.findViewById(R.id.quantity)).setText(String.valueOf(quantities.get(products.indexOf(product))));
                orderedProductsHolder.addView(cardView);
            }

            productIds.clear();
            quantities.clear();

        }
    }

}
