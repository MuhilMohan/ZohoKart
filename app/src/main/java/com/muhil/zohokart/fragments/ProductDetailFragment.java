package com.muhil.zohokart.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.muhil.zohokart.R;
import com.muhil.zohokart.adapters.ProductDetailPagerAdapter;
import com.muhil.zohokart.models.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductDetailFragment extends android.support.v4.app.Fragment {

    View rootview;
    int currentPosition;
    List<Product> products;
    ViewPager productDetailPager;
    ProductDetailPagerAdapter productDetailPagerAdapter;

    public static android.support.v4.app.Fragment getInstance( int currentPosition, List<Product> products){

        ProductDetailFragment productDetailFragment = new ProductDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("current_position", currentPosition);
        bundle.putParcelableArrayList("products", (ArrayList<? extends Parcelable>) products);
        productDetailFragment.setArguments(bundle);
        return productDetailFragment;

    }

    public ProductDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_product_detail, container, false);
        currentPosition = getArguments().getInt("current_position");
        products = getArguments().getParcelableArrayList("products");

        productDetailPager = (ViewPager) rootview.findViewById(R.id.product_view_pager);

        productDetailPagerAdapter = new ProductDetailPagerAdapter(getActivity().getSupportFragmentManager(), getActivity(), products);

        productDetailPager.setAdapter(productDetailPagerAdapter);
        productDetailPager.setCurrentItem(currentPosition);

        return rootview;
    }


}
