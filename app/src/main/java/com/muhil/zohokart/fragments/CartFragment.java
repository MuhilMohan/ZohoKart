package com.muhil.zohokart.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.muhil.zohokart.R;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.utils.DBHelper;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment
{

    DBHelper dbHelper;
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
        dbHelper = new DBHelper(getActivity());
        productsInCart = dbHelper.getProductsFromCart();
        emptyCartHolder = (LinearLayout) cartFragmentLayout.findViewById(R.id.empty_cart_holder);
        cartContent = (ScrollView) cartFragmentLayout.findViewById(R.id.cart_content);
        productsInCartContent = (LinearLayout) cartFragmentLayout.findViewById(R.id.cart_list);
        productsInCartContent.removeAllViews();

        populateProductList();

        return cartFragmentLayout;

    }

    @Override
    public void onResume() {

        updateGrandTotal();

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
                    ((TextView) cardView.findViewById(R.id.price)).setText("Rs. "+decimalFormat.format(product.getPrice()));
                    ((EditText) cardView.findViewById(R.id.quantity)).setText(String.valueOf(1));
                    (cardView.findViewById(R.id.quantity)).setTag(product);
                    ((TextView) cardView.findViewById(R.id.total_price)).setText("Rs. "+decimalFormat.format(product.getPrice()));
                    Picasso.with(getActivity()).load(product.getThumbnail()).into((ImageView) cardView.findViewById(R.id.display_image));

                    (cardView.findViewById(R.id.quantity)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {

                            if (!v.hasFocus()){

                                try
                                {

                                    if (!(((EditText) v).getText().toString().equals("")))
                                    {
                                        quantity = Integer.parseInt(((EditText) v).getText().toString());
                                        ((TextView) (cardView).findViewById(R.id.total_price)).setText("Rs. "+decimalFormat.format(Double.parseDouble((((TextView) cardView.findViewById(R.id.price)).getText().toString())) * quantity));
                                        dbHelper.updateQuantityOfProductInCart(Integer.parseInt(((EditText) v).getText().toString()), ((Product) v.getTag()).getId());
                                        updateGrandTotal();

                                    }
                                    else
                                    {
                                        ((EditText) v).setText(String.valueOf(1));
                                    }

                                }
                                catch (NumberFormatException numberFormatException)
                                {
                                }

                            }

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
            grandTotal = grandTotal + (product.getPrice() * Double.parseDouble(((EditText) view.findViewById(R.id.quantity)).getText().toString()));
        }

        ((TextView) getView().findViewById(R.id.grand_total)).setText(String.valueOf(decimalFormat.format(grandTotal)));

    }

}
