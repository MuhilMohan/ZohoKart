package com.muhil.zohokart.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muhil.zohokart.R;
import com.muhil.zohokart.models.Product;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductDetailPagerFragment extends android.support.v4.app.Fragment {

    Gson gson;
    View rootView;
    Product product;
    ImageView imageView;


    public static ProductDetailPagerFragment getInstance(Product product)
    {
        Gson gson = new Gson();
        ProductDetailPagerFragment productDetailPagerFragment = new ProductDetailPagerFragment();
        String productString = gson.toJson(product);
        Bundle bundle = new Bundle();
        bundle.putString("product", productString);
        productDetailPagerFragment.setArguments(bundle);
        return productDetailPagerFragment;
    }

    public ProductDetailPagerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_product_detail_pager, container, false);
        gson = new Gson();
        product = gson.fromJson(getArguments().getString("product"), new TypeToken<Product>() {
        }.getType());
        ((TextView) rootView.findViewById(R.id.title)).setText(product.getTitle());
        ((TextView) rootView.findViewById(R.id.description)).setText(product.getDescription());
        ((TextView) rootView.findViewById(R.id.price)).setText(String.valueOf(product.getPrice()));
        ((TextView) rootView.findViewById(R.id.rating)).setText(String.valueOf(product.getRatings()));
        imageView = (ImageView) rootView.findViewById(R.id.product_thumbnail);
        Picasso.with(getActivity()).load(product.getThumbnail()).into(imageView);

        return rootView;
    }


}
