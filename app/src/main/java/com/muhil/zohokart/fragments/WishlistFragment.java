package com.muhil.zohokart.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.muhil.zohokart.R;
import com.muhil.zohokart.adapters.WishlistAdapter;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.utils.DBHelper;
import com.muhil.zohokart.utils.ZohokartDAO;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class WishlistFragment extends android.support.v4.app.Fragment
{

    RecyclerView wishlistRecyclerView;
    List<Product> productsInWishlist;
    WishlistAdapter wishlistAdapter;
    TextView emptyTextView;
    View wishlistFragment;

    ZohokartDAO zohokartDAO;

    public WishlistFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        wishlistFragment = inflater.inflate(R.layout.fragment_wishlist, container, false);
        zohokartDAO = new ZohokartDAO(getActivity());
        productsInWishlist = zohokartDAO.getProductsFromWishlist();
        wishlistRecyclerView = (RecyclerView) wishlistFragment.findViewById(R.id.wishlist);
        emptyTextView = (TextView) wishlistFragment.findViewById(R.id.emptyText);
        if (productsInWishlist != null)
        {
            if (productsInWishlist.size() > 0)
            {
                updateWishlistCount(productsInWishlist.size());
                wishlistAdapter = new WishlistAdapter(getActivity(), productsInWishlist, WishlistFragment.this, getActivity().getSupportFragmentManager());
                wishlistRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
        return wishlistFragment;
    }

    public void switchViewElement()
    {
        wishlistRecyclerView.setVisibility(View.GONE);
        emptyTextView.setVisibility(View.VISIBLE);
        (wishlistFragment.findViewById(R.id.wishlist_header)).setVisibility(View.GONE);
    }

    public void updateWishlistCount(int count)
    {
        ((TextView) wishlistFragment.findViewById(R.id.wishlist_count)).setText("(" + count + ")");
    }

}
