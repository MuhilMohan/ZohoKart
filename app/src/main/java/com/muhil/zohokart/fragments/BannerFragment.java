package com.muhil.zohokart.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.muhil.zohokart.R;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class BannerFragment extends android.support.v4.app.Fragment {

    ImageView bannerImage;

    public BannerFragment() {
        // Required empty public constructor
    }

    public static android.support.v4.app.Fragment getInstance(String banner_url)
    {
        android.support.v4.app.Fragment bannerFragment = new BannerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("banner_url", banner_url);
        bannerFragment.setArguments(bundle);
        return bannerFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        ViewGroup bannerFragment = (ViewGroup) inflater.inflate(R.layout.fragment_banner, container, false);


        Bundle fragmentArguments = getArguments();
        String banner_url = fragmentArguments.getString("banner_url");

        bannerImage = (ImageView) bannerFragment.findViewById(R.id.banner_holder);
        Picasso.with(getActivity()).load(banner_url).into(bannerImage);

        return bannerFragment;
    }


}
