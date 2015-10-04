package com.muhil.zohokart.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.muhil.zohokart.R;
import com.muhil.zohokart.utils.DBHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class WishlistFragment extends Fragment {

    RecyclerView wishlistRecyclerView;
    DBHelper dbHelper;


    public WishlistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View wishlistFragment = inflater.inflate(R.layout.fragment_wishlist, container, false);



        return wishlistFragment;
    }


}
