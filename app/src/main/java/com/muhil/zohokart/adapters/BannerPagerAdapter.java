package com.muhil.zohokart.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.muhil.zohokart.fragments.BannerFragment;
import com.muhil.zohokart.models.PromotionBanner;

import java.util.List;

/**
 * Created by muhil-ga42 on 09/10/15.
 */
public class BannerPagerAdapter extends FragmentPagerAdapter
{
    List<PromotionBanner> banners;

    public BannerPagerAdapter(FragmentManager fragmentManager, List<PromotionBanner> banners) {
        super(fragmentManager);
        this.banners = banners;
    }

    @Override
    public Fragment getItem(int position) {
       return BannerFragment.getInstance(banners.get(position));
    }

    @Override
    public int getCount() {
        return banners.size();
    }
}
