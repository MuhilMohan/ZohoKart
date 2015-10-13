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
    List<PromotionBanner> banners;
    ViewPager bannerPager;
    BannerPagerAdapter bannerPagerAdapter;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View rootView = getView();
        zohokartDAO = new ZohokartDAO(getActivity());
        banners = zohokartDAO.getBanners();

        bannerPager = (ViewPager) rootView.findViewById(R.id.banner_pager);
        bannerPagerAdapter = new BannerPagerAdapter(getActivity().getSupportFragmentManager(), banners);
        bannerPager.setAdapter(bannerPagerAdapter);

    }

}
