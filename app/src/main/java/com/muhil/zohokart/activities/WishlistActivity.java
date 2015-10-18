package com.muhil.zohokart.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.muhil.zohokart.R;
import com.muhil.zohokart.adapters.WishlistAdapter;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.utils.ZohokartDAO;

import java.util.List;

public class WishlistActivity extends AppCompatActivity
{

    Toolbar toolbar;
    ZohokartDAO zohokartDAO;
    List<Product> productsInWishlist;
    RecyclerView wishlistRecyclerView;
    WishlistAdapter wishlistAdapter;
    TextView emptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        zohokartDAO = new ZohokartDAO(this);
        productsInWishlist = zohokartDAO.getProductsFromWishlist();
        wishlistRecyclerView = (RecyclerView) findViewById(R.id.wishlist);
        emptyTextView = (TextView) findViewById(R.id.emptyText);
        if (productsInWishlist != null)
        {
            if (productsInWishlist.size() > 0)
            {
                updateWishlistCount(productsInWishlist.size());
                wishlistAdapter = new WishlistAdapter(this, productsInWishlist, WishlistActivity.this, getSupportFragmentManager());
                wishlistRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                wishlistRecyclerView.setAdapter(wishlistAdapter);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wishlist, menu);
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

        return super.onOptionsItemSelected(item);
    }

    public void updateWishlistCount(int count)
    {
        ((TextView) findViewById(R.id.wishlist_count)).setText("(" + count + ")");
    }

    public void switchViewElement()
    {
        wishlistRecyclerView.setVisibility(View.GONE);
        emptyTextView.setVisibility(View.VISIBLE);
        (findViewById(R.id.wishlist_header)).setVisibility(View.GONE);
    }

}
