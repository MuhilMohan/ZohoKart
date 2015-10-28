package com.muhil.zohokart.fragments;


import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
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
    ZohokartDAO zohokartDAO;
    PromotionBanner currentBanner, clickedBanner;
    BannerCommunicator communicator;
    List<Integer> productIds;
    List<Product> products;

    public static BannerFragment getInstance(PromotionBanner promotionBanner)
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

    public void setCommunicator(BannerCommunicator communicator)
    {
        this.communicator = communicator;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        zohokartDAO = new ZohokartDAO(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        bannerFragment = inflater.inflate(R.layout.fragment_banner, container, false);
        currentBanner = (PromotionBanner) getArguments().getSerializable("banner");
        ImageView bannerImage = (ImageView) bannerFragment.findViewById(R.id.banner_image);
        Picasso.with(getActivity()).load(currentBanner.getBanner()).into(bannerImage);
        bannerImage.setTag(currentBanner);

        bannerImage.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        clickedBanner = (PromotionBanner) v.getTag();
                        new BannerProductsAsyncTask().execute(clickedBanner);
                    }
                }
        );

        return bannerFragment;
    }

    class BannerProductsAsyncTask extends AsyncTask<PromotionBanner, Void, List<Product>>
    {

        @Override
        protected List<Product> doInBackground(PromotionBanner... params)
        {
            productIds = params[0].getProductIds();
            products = zohokartDAO.getProductsForProductIds(productIds);
            return products;
        }

        @Override
        protected void onPostExecute(List<Product> products)
        {
            super.onPostExecute(products);
            communicator.openProductListPage(products);
        }
    }

    public interface BannerCommunicator
    {
        void openProductListPage(List<Product> products);
    }

}
