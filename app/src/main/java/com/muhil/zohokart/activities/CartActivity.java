package com.muhil.zohokart.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class CartActivity extends AppCompatActivity
{

    Toolbar toolbar;
    ZohokartDAO zohokartDAO;
    List<Product> productsInCart;
    LinearLayout emptyCartHolder, productsInCartContent;
    ScrollView cartContent;
    CardView cardView;
    android.support.v4.app.FragmentTransaction fragmentTransaction;
    double quantity;
    double grandTotal = 0;
    DecimalFormat decimalFormat = new DecimalFormat("#.00");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        zohokartDAO = new ZohokartDAO(this);
        productsInCart = zohokartDAO.getProductsFromCart();
        emptyCartHolder = (LinearLayout) findViewById(R.id.empty_cart_holder);
        cartContent = (ScrollView) findViewById(R.id.cart_content);
        productsInCartContent = (LinearLayout) findViewById(R.id.cart_list);
        productsInCartContent.removeAllViews();

        populateProductList();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }
        else if (id == android.R.id.home)
        {
            super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void populateProductList()
    {

        if (productsInCart != null)
        {

            if (productsInCart.size() > 0)
            {

                ((TextView) findViewById(R.id.cart_list_count)).setText("("+productsInCart.size()+")");

                for (final Product product : productsInCart)
                {

                    LayoutInflater layoutInflater = LayoutInflater.from(this);
                    cardView = (CardView) layoutInflater.inflate(R.layout.cart_item_row, productsInCartContent, false);
                    cardView.setId(product.getId());
                    ((TextView) cardView.findViewById(R.id.title)).setText(product.getTitle());
                    ((TextView) cardView.findViewById(R.id.description)).setText(product.getDescription());
                    ((TextView) cardView.findViewById(R.id.price)).setText(decimalFormat.format(product.getPrice()));
                    ((EditText) cardView.findViewById(R.id.quantity)).setText(String.valueOf(zohokartDAO.getQuantityofProductInCart(product.getId())));
                    (cardView.findViewById(R.id.quantity)).setTag(product);
                    ((TextView) cardView.findViewById(R.id.total_price)).setText(decimalFormat.format(product.getPrice()));
                    Picasso.with(this).load(product.getThumbnail()).into((ImageView) cardView.findViewById(R.id.display_image));
                    if (zohokartDAO.checkInWishlist(product.getId()))
                    {
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
                                            Toast.makeText(CartActivity.this, "Quantity updated.", Toast.LENGTH_SHORT).show();
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

                    (cardView).setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            (v.findViewById(R.id.quantity)).clearFocus();
                            InputMethodManager inputMethodManager = (InputMethodManager) CartActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(CartActivity.this.getCurrentFocus().getWindowToken(), 0);
                        }
                    });

                    (cardView.findViewById(R.id.remove_from_cart)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CartActivity.this);
                            alertDialogBuilder.setTitle("");
                            alertDialogBuilder.setMessage("Are you sure?");

                            alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (zohokartDAO.removeFromCart(product.getId()))
                                    {
                                        Toast.makeText(CartActivity.this, "Product removed from cart.", Toast.LENGTH_SHORT).show();
                                        int position = productsInCart.indexOf(product);
                                        productsInCart.remove(position);

                                        ((TextView) findViewById(R.id.cart_list_count)).setText("(" + productsInCart.size() + ")");
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
                                        Toast.makeText(CartActivity.this, "error while removing from cart.", Toast.LENGTH_SHORT).show();
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

                            if (zohokartDAO.addToWishlist(product.getId()))
                            {
                                if (zohokartDAO.removeFromCart(product.getId()))
                                {
                                    Toast.makeText(CartActivity.this, "Product added to wishlist.", Toast.LENGTH_SHORT).show();
                                    (v).setVisibility(View.GONE);
                                    ((productsInCartContent.findViewById(product.getId())).findViewById(R.id.go_to_wishlist)).setVisibility(View.VISIBLE);
                                }
                            }
                            else
                            {
                                Toast.makeText(CartActivity.this, "Product already exists in wishlist.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                    (cardView.findViewById(R.id.go_to_wishlist)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(CartActivity.this, WishlistActivity.class));

                        }
                    });

                    (findViewById(R.id.continue_shopping_button)).setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {

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

        ((TextView) findViewById(R.id.grand_total)).setText(String.valueOf(decimalFormat.format(grandTotal)));

    }

}
