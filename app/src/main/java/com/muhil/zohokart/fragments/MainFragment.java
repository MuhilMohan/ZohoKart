package com.muhil.zohokart.fragments;

import android.animation.ObjectAnimator;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.muhil.zohokart.R;
import com.muhil.zohokart.adapters.BannerPagerAdapter;
import com.muhil.zohokart.models.PromotionBanner;
import com.muhil.zohokart.utils.ZohokartDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    ZohokartDAO zohokartDAO;
    View rootView;
    ViewPager bannerPager;
    List<PromotionBanner> banners;
    BannerPagerAdapter bannerPagerAdapter;
    List<ImageView> viewPagerIndicatorList;
    LinearLayout viewPagerIndicator;

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

        rootView = getView();

        zohokartDAO = new ZohokartDAO(getActivity());
        bannerPager = (ViewPager) rootView.findViewById(R.id.banner_pager);
        banners = zohokartDAO.getBanners();
        bannerPagerAdapter = new BannerPagerAdapter(getFragmentManager(), banners);
        bannerPager.setAdapter(bannerPagerAdapter);

        addDots();

        bannerPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                

            }

            @Override
            public void onPageSelected(int position) {
                selectIndicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    public void addDots() {
        viewPagerIndicatorList = new ArrayList<>();
        viewPagerIndicator = (LinearLayout) rootView.findViewById(R.id.view_pager_indicator);

        for(int i = 0; i < bannerPagerAdapter.getCount(); i++) {
            ImageView indicator = new ImageView(getActivity());
            indicator.setImageResource(R.mipmap.ic_panorama_fish_eye_black_18dp);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( 14, 14);
            params.setMargins(2, 2, 2, 2);
            viewPagerIndicator.addView(indicator, params);

            viewPagerIndicatorList.add(indicator);
        }

        viewPagerIndicatorList.get(0).setImageResource(R.mipmap.ic_lens_black_18dp);
    }

    public void selectIndicator(int idx) {
        for(int i = 0; i < bannerPagerAdapter.getCount(); i++) {
            int resourceId = (i==idx)?(R.mipmap.ic_lens_black_18dp):(R.mipmap.ic_panorama_fish_eye_black_18dp);
            viewPagerIndicatorList.get(i).setImageResource(resourceId);
        }
    }

}
