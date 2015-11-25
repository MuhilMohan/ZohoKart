package com.muhil.zohokart.fragments;


import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.muhil.zohokart.R;
import com.muhil.zohokart.models.Account;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.utils.ImageLoader;
import com.muhil.zohokart.utils.ZohokartDAO;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderConfirmationFragment extends android.support.v4.app.Fragment
{

    ZohokartDAO zohokartDAO;
    ImageLoader imageLoader;
    OrderConfirmationCommunicator communicator;
    Account account;
    List<Product> products;
    List<Integer> productIds, quantities;
    View orderConfirmationFragment;
    TextView accountName, accountPhoneNumber, accountAddress, totalPrice;
    LinearLayout orderedProductsHolder;
    String email, password;
    double grandTotal;
    LayoutInflater layoutInflater;
    CardView cardView;
    DecimalFormat decimalFormat = new DecimalFormat("#.00");

    public static OrderConfirmationFragment getInstance(String email, String password, List<Integer> productIds)
    {
        OrderConfirmationFragment orderConfirmationFragment = new OrderConfirmationFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Account.EMAIL, email);
        bundle.putString(Account.PASSWORD, password);
        bundle.putIntegerArrayList("product_ids", (ArrayList<Integer>) productIds);
        orderConfirmationFragment.setArguments(bundle);
        return orderConfirmationFragment;
    }

    public void setCommunicator(OrderConfirmationCommunicator communicator)
    {
        this.communicator = communicator;
    }

    public OrderConfirmationFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        zohokartDAO = new ZohokartDAO(getActivity());
        imageLoader = new ImageLoader(getActivity());
        layoutInflater = LayoutInflater.from(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        orderConfirmationFragment = inflater.inflate(R.layout.fragment_order_confirmation, container, false);

        accountName = (TextView) orderConfirmationFragment.findViewById(R.id.account_name);
        accountPhoneNumber = (TextView) orderConfirmationFragment.findViewById(R.id.account_phone_number);
        accountAddress = (TextView) orderConfirmationFragment.findViewById(R.id.delivery_address);
        totalPrice = (TextView) orderConfirmationFragment.findViewById(R.id.total_price);
        orderedProductsHolder = (LinearLayout) orderConfirmationFragment.findViewById(R.id.ordered_products_holder);
        orderedProductsHolder.removeAllViews();

        email = getArguments().getString(Account.EMAIL);
        password = getArguments().getString(Account.PASSWORD);
        productIds = getArguments().getIntegerArrayList("product_ids");

        grandTotal = 0;
        new OrderConfirmationAsyncTask().execute(email, password);

        (orderConfirmationFragment.findViewById(R.id.payment_action)).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (accountAddress.getText().toString().equals(""))
                        {
                            LayoutInflater inflater = LayoutInflater.from(getActivity());
                            final View addressDialog = inflater.inflate(R.layout.addres_dialog, null);
                            final AlertDialog.Builder addressDiaBuilder = new AlertDialog.Builder(getActivity());
                            addressDiaBuilder.setMessage("Update your address");
                            addressDiaBuilder.setView(addressDialog);
                            addressDiaBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    EditText address = (EditText) addressDialog.findViewById(R.id.address_text);
                                    if (address.getText().toString().length() > 0)
                                    {
                                        accountAddress.setText(address.getText().toString());
                                        zohokartDAO.updateAddressForAccount(address.getText().toString(), email);
                                        communicator.proceedToPayment(email, productIds, quantities);
                                    }
                                }
                            });
                            addressDiaBuilder.show();
                        }
                        else
                        {
                            communicator.proceedToPayment(email, productIds, quantities);
                        }
                    }
                }
        );

        return orderConfirmationFragment;
    }

    class OrderConfirmationAsyncTask extends AsyncTask<String, Void, Void>
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            quantities = new ArrayList<>();
            (orderConfirmationFragment.findViewById(R.id.order_loading)).setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... params)
        {
            account = zohokartDAO.getAccountIfAvailable(params[0], params[1]);
            if (productIds != null)
            {
                products = zohokartDAO.getProductsForProductIds(productIds);
                quantities.clear();
                for (Product product : products)
                {
                    if (zohokartDAO.checkInCart(product.getId(), params[0]))
                    {
                        quantities.add(zohokartDAO.getQuantityofProductInCart(product.getId(), params[0]));
                    }
                    else
                    {
                        quantities.add(1);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);

            accountName.setText(account.getName());
            accountPhoneNumber.setText(account.getPhoneNumber());
            accountAddress.setText(account.getDeliveryAddress());

            for (Product product : products)
            {
                cardView = (CardView) layoutInflater.inflate(R.layout.ordered_product_item_row, orderedProductsHolder, false);
                ((TextView) cardView.findViewById(R.id.title)).setText(product.getTitle());
                ((TextView) cardView.findViewById(R.id.description)).setText(product.getDescription());
                ((TextView) cardView.findViewById(R.id.price)).setText(decimalFormat.format((product.getPrice() * quantities.get(products.indexOf(product)))));
                setGrandTotal(product.getPrice());
                //imageLoader.displayImage(product.getThumbnail(), (ImageView) cardView.findViewById(R.id.display_image));
                ((TextView) cardView.findViewById(R.id.quantity)).setText(String.valueOf(quantities.get(products.indexOf(product))));
                orderedProductsHolder.addView(cardView);
            }

            ((TextView) orderConfirmationFragment.findViewById(R.id.total_products)).setText("(" + products.size() + ")");
            ((TextView) orderConfirmationFragment.findViewById(R.id.total_price)).setText(decimalFormat.format(grandTotal));
            (orderConfirmationFragment.findViewById(R.id.order_loading)).setVisibility(View.GONE);

        }
    }

    private void setGrandTotal(double price)
    {
        grandTotal += price;
    }

    public interface OrderConfirmationCommunicator
    {
        void proceedToPayment(String email, List<Integer> productIds, List<Integer> quantities);
    }

}
