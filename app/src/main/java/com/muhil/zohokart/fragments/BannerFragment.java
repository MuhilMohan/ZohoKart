package com.muhil.zohokart.fragments;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.muhil.zohokart.R;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.models.PromotionBanner;
import com.muhil.zohokart.utils.ZohokartDAO;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class BannerFragment extends android.support.v4.app.Fragment {

    ImageView bannerImage;
    ArrayList<Integer> productsIds;
    List<Product> products;
    ZohokartDAO zohokartDAO;
    Gson gson;

    public BannerFragment() {
        // Required empty public constructor
    }

    public static android.support.v4.app.Fragment getInstance(String banner_url, List<Integer> productIds)
    {
        android.support.v4.app.Fragment bannerFragment = new BannerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PromotionBanner.BANNER_URL, banner_url);
        bundle.putIntegerArrayList(PromotionBanner.PRODUCTS_RELATED, (ArrayList<Integer>) productIds);
        bannerFragment.setArguments(bundle);
        return bannerFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        ViewGroup bannerFragment = (ViewGroup) inflater.inflate(R.layout.fragment_banner, container, false);

        zohokartDAO = new ZohokartDAO(getActivity());
        gson = new Gson();

        final Bundle fragmentArguments = getArguments();
        String banner_url = fragmentArguments.getString(PromotionBanner.BANNER_URL);

        bannerImage = (ImageView) bannerFragment.findViewById(R.id.banner_holder);
        bannerImage.setTag(fragmentArguments.getIntegerArrayList(PromotionBanner.PRODUCTS_RELATED));
        Picasso.with(getActivity()).load(banner_url).into(bannerImage);

        bannerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Object prodcutIdsObject = v.getTag();
                productsIds = (ArrayList<Integer>) prodcutIdsObject;
                products = zohokartDAO.getProductsForProductIds(productsIds);

                if (products.size() > 1) {

                    String productsString = gson.toJson(products);
                    ProductListFragment productListFragment = ProductListFragment.getInstance(productsString);
                    FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_holder, productListFragment, "product_list");
                    fragmentTransaction.commit();

                }

            }
        });

        return bannerFragment;
    }


}
