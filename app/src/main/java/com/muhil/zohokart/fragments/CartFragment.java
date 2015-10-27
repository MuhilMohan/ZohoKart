package com.muhil.zohokart.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.muhil.zohokart.R;
import com.muhil.zohokart.activities.CheckoutActivity;
import com.muhil.zohokart.models.Account;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.utils.ZohoKartSharePreferences;
import com.muhil.zohokart.utils.ZohokartDAO;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends android.support.v4.app.Fragment
{

    ZohokartDAO zohokartDAO;
    View cartFragment;
    List<Product> productsInCart;
    List<Integer> productIds;
    LinearLayout emptyCartHolder, productsInCartContent;
    ScrollView cartContent;
    CardView cardView;
    double quantity;
    double grandTotal = 0;
    DecimalFormat decimalFormat = new DecimalFormat("#.00");
    CartCommunicator communicator;
    LayoutInflater layoutInflater;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String email;

    public CartFragment()
    {
        // Required empty public constructor
    }

    public void setCommunicator(CartCommunicator communicator)
    {
        this.communicator = communicator;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        zohokartDAO = new ZohokartDAO(getActivity());
        sharedPreferences = getActivity().getSharedPreferences(ZohoKartSharePreferences.LOGGED_ACCOUNT, Context.MODE_PRIVATE);
        productIds = new ArrayList<>();
        layoutInflater = LayoutInflater.from(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        cartFragment = inflater.inflate(R.layout.fragment_cart, container, false);

        emptyCartHolder = (LinearLayout) cartFragment.findViewById(R.id.empty_cart_holder);
        cartContent = (ScrollView) cartFragment.findViewById(R.id.cart_content);
        productsInCartContent = (LinearLayout) cartFragment.findViewById(R.id.cart_list);
        productsInCartContent.removeAllViews();

        new CartAsyncTask().execute();

        (cartFragment.findViewById(R.id.checkout_action)).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (productsInCart.size() > 0)
                        {
                            productIds.clear();
                            for (Product product : productsInCart)
                            {
                                productIds.add(product.getId());
                            }
                            communicator.openCheckout(productIds);
                        }
                    }
                }
        );

        return cartFragment;
    }

    private void switchViewElement()
    {
        cartContent.setVisibility(View.GONE);
        emptyCartHolder.setVisibility(View.VISIBLE);

    }

    private void updateGrandTotal()
    {
        grandTotal = 0;
        for (Product product : productsInCart)
        {
            View view = productsInCartContent.findViewById(product.getId());
            grandTotal = grandTotal + (Double.parseDouble(((TextView) view.findViewById(R.id.total_price)).getText().toString()));
        }

        ((TextView) cartFragment.findViewById(R.id.grand_total)).setText(String.valueOf(decimalFormat.format(grandTotal)));

    }

    class CartAsyncTask extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            (cartFragment.findViewById(R.id.cart_loading)).setVisibility(View.VISIBLE);
            (cartFragment.findViewById(R.id.cart_content)).setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            email = sharedPreferences.getString(Account.EMAIL, "default");
            productsInCart = zohokartDAO.getProductsFromCart(email);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);

            if (productsInCart != null)
            {

                if (productsInCart.size() > 0)
                {

                    ((TextView) cartFragment.findViewById(R.id.cart_list_count)).setText("(" + productsInCart.size() + ")");

                    for (final Product product : productsInCart)
                    {
                        cardView = (CardView) layoutInflater.inflate(R.layout.cart_item_row, productsInCartContent, false);
                        cardView.setId(product.getId());
                        ((TextView) cardView.findViewById(R.id.title)).setText(product.getTitle());
                        ((TextView) cardView.findViewById(R.id.description)).setText(product.getDescription());
                        ((TextView) cardView.findViewById(R.id.price)).setText(decimalFormat.format(product.getPrice()));
                        ((EditText) cardView.findViewById(R.id.quantity)).setText(String.valueOf(zohokartDAO.getQuantityofProductInCart(product.getId(), email)));
                        (cardView.findViewById(R.id.quantity)).setTag(product);
                        ((TextView) cardView.findViewById(R.id.total_price)).setText(decimalFormat.format(product.getPrice()));
                        Picasso.with(getActivity()).load(product.getThumbnail()).into((ImageView) cardView.findViewById(R.id.display_image));
                        if (zohokartDAO.checkInWishlist(product.getId(), email))
                        {
                            (cardView.findViewById(R.id.move_to_wishlist)).setVisibility(View.GONE);
                        }
                        (cardView.findViewById(R.id.quantity)).setOnFocusChangeListener(new View.OnFocusChangeListener()
                        {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus)
                            {

                                if (!hasFocus)
                                {
                                    try
                                    {
                                        if (!(((EditText) v).getText().toString().equals("")))
                                        {
                                            quantity = Integer.parseInt(((EditText) v).getText().toString());
                                            ((TextView) (productsInCartContent.findViewById(product.getId())).findViewById(R.id.total_price)).setText(String.valueOf(decimalFormat.format(((Product) v.getTag()).getPrice() * quantity)));
                                            if (zohokartDAO.updateQuantityOfProductInCart(Integer.parseInt(((EditText) v).getText().toString()), ((Product) v.getTag()).getId(), email))
                                            {
                                                updateGrandTotal();
                                                Toast.makeText(getActivity(), "Quantity updated.", Toast.LENGTH_SHORT).show();
                                            }
                                        } else
                                        {
                                            ((EditText) v).setText(String.valueOf(1));
                                        }

                                    } catch (NumberFormatException numberFormatException)
                                    {
                                        numberFormatException.printStackTrace();
                                    }

                                }

                            }
                        });

                        (cardView).setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                (v.findViewById(R.id.quantity)).clearFocus();
                                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                            }
                        });

                        (cardView.findViewById(R.id.remove_from_cart)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                                alertDialogBuilder.setTitle("");
                                alertDialogBuilder.setMessage("Do you want to delete?");

                                alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (zohokartDAO.removeFromCart(product.getId(), email))
                                        {
                                            Toast.makeText(getActivity(), "Product removed from cart.", Toast.LENGTH_SHORT).show();
                                            int position = productsInCart.indexOf(product);
                                            productsInCart.remove(position);

                                            ((TextView) cartFragment.findViewById(R.id.cart_list_count)).setText("(" + productsInCart.size() + ")");
                                            productsInCartContent.removeView(productsInCartContent.findViewById(product.getId()));
                                            updateGrandTotal();

                                            if (productsInCart.size() == 0)
                                            {
                                                switchViewElement();
                                            }
                                        }
                                        else
                                        {
                                            dialog.dismiss();
                                            Toast.makeText(getActivity(), "error while removing from cart.", Toast.LENGTH_SHORT).show();
                                        }

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
                        });

                        (cardView.findViewById(R.id.move_to_wishlist)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                                alertDialogBuilder.setTitle("");
                                alertDialogBuilder.setMessage("Do you want to move to wishlist?");

                                alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (!zohokartDAO.checkInWishlist(product.getId(), email))
                                        {
                                            if (zohokartDAO.addToWishlist(product.getId(), email))
                                            {
                                                if (zohokartDAO.removeFromCart(product.getId(), email))
                                                {
                                                    Toast.makeText(getActivity(), "Product added to wishlist.", Toast.LENGTH_SHORT).show();
                                                    int position = productsInCart.indexOf(product);
                                                    productsInCart.remove(position);

                                                    ((TextView) cartFragment.findViewById(R.id.cart_list_count)).setText("(" + productsInCart.size() + ")");
                                                    productsInCartContent.removeView(productsInCartContent.findViewById(product.getId()));
                                                    updateGrandTotal();

                                                    if (productsInCart.size() == 0)
                                                    {
                                                        switchViewElement();
                                                    }
                                                }
                                            }
                                            else
                                            {
                                                Toast.makeText(getActivity(), "error while adding to wishlist.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else
                                        {
                                            Toast.makeText(getActivity(), "Product already exists in wishlist.", Toast.LENGTH_SHORT).show();
                                        }
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
                        });

                        (cartFragment.findViewById(R.id.continue_shopping_button)).setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {

                            }
                        });

                        productsInCartContent.addView(cardView);
                    }
                    updateGrandTotal();
                    (cartFragment.findViewById(R.id.cart_loading)).setVisibility(View.GONE);
                    (cartFragment.findViewById(R.id.cart_content)).setVisibility(View.VISIBLE);
                }
                else
                {
                    switchViewElement();
                }

            }
            else
            {
                switchViewElement();
            }

        }
    }

    public interface CartCommunicator
    {
        void openWishlist();
        void openCheckout(List<Integer> productIds);
    }

}
