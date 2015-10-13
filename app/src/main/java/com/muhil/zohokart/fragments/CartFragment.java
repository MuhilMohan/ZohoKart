package com.muhil.zohokart.fragments;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
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
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.utils.ZohokartDAO;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends android.support.v4.app.Fragment
{

    ZohokartDAO zohokartDAO;
    List<Product> productsInCart;
    LinearLayout emptyCartHolder, productsInCartContent;
    ScrollView cartContent;
    CardView cardView;
    View cartFragmentLayout;

    double quantity;
    double grandTotal = 0;

    DecimalFormat decimalFormat = new DecimalFormat("#.00");

    public CartFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        // Inflate the layout for this fragment
        cartFragmentLayout =  inflater.inflate(R.layout.fragment_cart, container, false);
        zohokartDAO = new ZohokartDAO(getActivity());
        productsInCart = zohokartDAO.getProductsFromCart();
        emptyCartHolder = (LinearLayout) cartFragmentLayout.findViewById(R.id.empty_cart_holder);
        cartContent = (ScrollView) cartFragmentLayout.findViewById(R.id.cart_content);
        productsInCartContent = (LinearLayout) cartFragmentLayout.findViewById(R.id.cart_list);
        productsInCartContent.removeAllViews();

        populateProductList();

        return cartFragmentLayout;

    }

    @Override
    public void onResume() {

        if (productsInCart != null && productsInCart.size() > 0)
        {
            updateGrandTotal();
        }

        super.onResume();
    }

    private void switchViewElement()
    {

        cartContent.setVisibility(View.GONE);
        emptyCartHolder.setVisibility(View.VISIBLE);

    }

    private void populateProductList()
    {

        if (productsInCart != null)
        {

            if (productsInCart.size() > 0)
            {

                ((TextView) cartFragmentLayout.findViewById(R.id.cart_list_count)).setText("("+productsInCart.size()+")");

                for (final Product product : productsInCart)
                {

                    LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                    cardView = (CardView) layoutInflater.inflate(R.layout.cart_item_row, productsInCartContent, false);
                    cardView.setId(product.getId());
                    ((TextView) cardView.findViewById(R.id.title)).setText(product.getTitle());
                    ((TextView) cardView.findViewById(R.id.description)).setText(product.getDescription());
                    ((TextView) cardView.findViewById(R.id.price)).setText(decimalFormat.format(product.getPrice()));
                    ((EditText) cardView.findViewById(R.id.quantity)).setText(String.valueOf(1));
                    (cardView.findViewById(R.id.quantity)).setTag(product);
                    ((TextView) cardView.findViewById(R.id.total_price)).setText(decimalFormat.format(product.getPrice()));
                    Picasso.with(getActivity()).load(product.getThumbnail()).into((ImageView) cardView.findViewById(R.id.display_image));
                    if (zohokartDAO.checkInWishlist(product.getId())){
                        (cardView.findViewById(R.id.move_to_wishlist)).setVisibility(View.GONE);
                        (cardView.findViewById(R.id.go_to_wishlist)).setVisibility(View.VISIBLE);
                    }


                    (cardView.findViewById(R.id.quantity)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {

                            if (!hasFocus) {

                                try {

                                    if (!(((EditText) v).getText().toString().equals(""))) {
                                        quantity = Integer.parseInt(((EditText) v).getText().toString());
                                        ((TextView) (productsInCartContent.findViewById(product.getId())).findViewById(R.id.total_price)).setText(String.valueOf(decimalFormat.format(((Product) v.getTag()).getPrice() * quantity)));
                                        if (zohokartDAO.updateQuantityOfProductInCart(Integer.parseInt(((EditText) v).getText().toString()), ((Product) v.getTag()).getId())){
                                            updateGrandTotal();
                                            Toast.makeText(getActivity(), "Quantity updated.", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        ((EditText) v).setText(String.valueOf(1));
                                    }

                                } catch (NumberFormatException numberFormatException) {
                                    numberFormatException.printStackTrace();
                                }

                            }

                        }
                    });

                    (cardView).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
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
                            alertDialogBuilder.setMessage("Are you sure?");

                            alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (zohokartDAO.removeFromCart(product.getId())) {
                                        Toast.makeText(getActivity(), "Product removed from cart.", Toast.LENGTH_SHORT).show();
                                        int position = productsInCart.indexOf(product);
                                        productsInCart.remove(position);

                                        ((TextView) cartFragmentLayout.findViewById(R.id.cart_list_count)).setText("(" + productsInCart.size() + ")");
                                        productsInCartContent.removeView(productsInCartContent.findViewById(product.getId()));
                                        updateGrandTotal();

                                        if (productsInCart.size() == 0) {
                                            switchViewElement();
                                        }
                                    } else {
                                        dialog.dismiss();
                                        Toast.makeText(getActivity(), "error while removing from wishlist.", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            alertDialogBuilder.show();
                        }
                    });

                    (cardView.findViewById(R.id.move_to_wishlist)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (zohokartDAO.addToWishlist(product.getId())){
                                if (zohokartDAO.removeFromCart(product.getId())){
                                    Toast.makeText(getActivity(), "Product added to wishlist.", Toast.LENGTH_SHORT).show();
                                    (v).setVisibility(View.GONE);
                                    ((productsInCartContent.findViewById(product.getId())).findViewById(R.id.go_to_wishlist)).setVisibility(View.VISIBLE);
                                }
                            }
                            else {
                                Toast.makeText(getActivity(), "Product already exists in wishlist.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                    (cardView.findViewById(R.id.go_to_wishlist)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            WishlistFragment wishlistFragment = new WishlistFragment();
                            android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_holder, wishlistFragment, "wishlist");
                            fragmentTransaction.addToBackStack("wishlist_fragment");
                            fragmentTransaction.commit();

                        }
                    });

                    productsInCartContent.addView(cardView);

                }

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

    private void updateGrandTotal()
    {

        grandTotal = 0;
        for (Product product : productsInCart)
        {
            View view = productsInCartContent.findViewById(product.getId());
            grandTotal = grandTotal + (Double.parseDouble(((TextView) view.findViewById(R.id.total_price)).getText().toString()));
        }

        ((TextView) getView().findViewById(R.id.grand_total)).setText(String.valueOf(decimalFormat.format(grandTotal)));

    }

}
