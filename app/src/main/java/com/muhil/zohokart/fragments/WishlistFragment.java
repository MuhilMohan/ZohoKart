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
import com.muhil.zohokart.decorators.DividerItemDecoration;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.utils.DBHelper;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class WishlistFragment extends Fragment {

    RecyclerView wishlistRecyclerView;
    DBHelper dbHelper;
    List<Product> wishlist;
    WishlistAdapter wishlistAdapter;
    TextView emptyTextView;

    public WishlistFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View wishlistFragment = inflater.inflate(R.layout.fragment_wishlist, container, false);
        dbHelper = new DBHelper(getActivity());
        wishlist = dbHelper.getProductsFromWishList();
        wishlistRecyclerView = (RecyclerView) wishlistFragment.findViewById(R.id.wishlist);
        emptyTextView = (TextView) wishlistFragment.findViewById(R.id.emptyText);
        if (wishlist != null){

            if (wishlist.size() > 0){
                wishlistAdapter = new WishlistAdapter(getActivity(), wishlist, WishlistFragment.this);
                wishlistRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                wishlistRecyclerView.setAdapter(wishlistAdapter);
            }

        }
        else {

            switchViewElement();

        }


        return wishlistFragment;
    }

    public void switchViewElement(){

        wishlistRecyclerView.setVisibility(View.GONE);
        emptyTextView.setVisibility(View.VISIBLE);

    }

}
