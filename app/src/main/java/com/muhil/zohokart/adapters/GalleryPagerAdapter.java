package com.muhil.zohokart.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.muhil.zohokart.R;
import com.muhil.zohokart.activities.ProductGalleryActivity;
import com.muhil.zohokart.fragments.GalleryItemFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by muhil-ga42 on 13/11/15.
 */
public class GalleryPagerAdapter extends FragmentPagerAdapter
{

    LayoutInflater layoutInflater;
    GalleryItemFragment.GalleryCommunicator communicator;
    List<String> imageLinks;

    public GalleryPagerAdapter(FragmentManager fm, ProductGalleryActivity productGalleryActivity, List<String> imageLinks)
    {
        super(fm);
        communicator = productGalleryActivity;
        layoutInflater = LayoutInflater.from(productGalleryActivity);
        this.imageLinks = imageLinks;
    }

    @Override
    public int getCount()
    {
        return imageLinks.size();
    }

    @Override
    public Fragment getItem(int position)
    {
        GalleryItemFragment galleryItemFragment = (GalleryItemFragment) GalleryItemFragment.getInstance(imageLinks.get(position));
        galleryItemFragment.setCommunicator(communicator);
        return galleryItemFragment;
    }
}
