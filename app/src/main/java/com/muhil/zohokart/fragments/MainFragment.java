package com.muhil.zohokart.fragments;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.muhil.zohokart.R;
import com.muhil.zohokart.adapters.BannerPagerAdapter;
import com.muhil.zohokart.models.PromotionBanner;
import com.muhil.zohokart.utils.ZohokartDAO;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    ZohokartDAO zohokartDAO;

    ViewPager bannerPager;
    List<PromotionBanner> banners;
    BannerPagerAdapter bannerPagerAdapter;
    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);

        zohokartDAO = new ZohokartDAO(getActivity());
        bannerPager = (ViewPager) rootView.findViewById(R.id.banner_pager);
        banners = zohokartDAO.getBanners();
        bannerPagerAdapter = new BannerPagerAdapter(getFragmentManager(), banners);
        bannerPager.setAdapter(bannerPagerAdapter);

        return rootView;
    }


}
