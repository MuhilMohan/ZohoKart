package com.muhil.zohokart.fragments;


import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
public class BannerFragment extends android.support.v4.app.Fragment
{

    View bannerFragment;
    ViewPager bannerPager;
    ZohokartDAO zohokartDAO;
    PromotionBanner currentBanner;

    List<PromotionBanner> banners;

    public static android.support.v4.app.Fragment getInstance(PromotionBanner promotionBanner)
    {
        BannerFragment bannerFragment = new BannerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("banner", promotionBanner);
        bannerFragment.setArguments(bundle);
        return bannerFragment;
    }

    public BannerFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Toast.makeText(getActivity(), "onCreateView of main", Toast.LENGTH_SHORT).show();
        // Inflate the layout for this fragment
        bannerFragment = inflater.inflate(R.layout.fragment_banner, container, false);
        currentBanner = (PromotionBanner) getArguments().getSerializable("banner");
        ImageView bannerImage = (ImageView) bannerFragment.findViewById(R.id.banner_image);
        Picasso.with(getActivity()).load(currentBanner.getBanner()).into(bannerImage);
        bannerImage.setTag(currentBanner.getProductIds());

        return bannerFragment;
    }

}
