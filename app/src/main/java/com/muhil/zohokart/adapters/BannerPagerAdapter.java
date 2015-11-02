package com.muhil.zohokart.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.muhil.zohokart.fragments.BannerFragment;
import com.muhil.zohokart.models.PromotionBanner;

import java.util.List;

/**
 * Created by muhil-ga42 on 09/10/15.
 */
public class BannerPagerAdapter extends FragmentStatePagerAdapter
{
    List<PromotionBanner> banners;
    BannerFragment.BannerCommunicator bannerCommunicator;
    int currentPosition = 0;

    public BannerPagerAdapter(FragmentManager fragmentManager, List<PromotionBanner> banners, BannerFragment.BannerCommunicator bannerCommunicator)
    {
        super(fragmentManager);
        this.banners = banners;
        this.bannerCommunicator = bannerCommunicator;
    }

    @Override
    public Fragment getItem(int position)
    {
        BannerFragment bannerFragment = BannerFragment.getInstance(banners.get(currentPosition));
        bannerFragment.setCommunicator(bannerCommunicator);

        if (currentPosition >= banners.size() - 1)
        {
            currentPosition = 0;
        }
        else
        {
            ++currentPosition;
        }

        return bannerFragment;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }
}
