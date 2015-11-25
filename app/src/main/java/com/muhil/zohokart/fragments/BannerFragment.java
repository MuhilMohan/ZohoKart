package com.muhil.zohokart.fragments;


import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.muhil.zohokart.R;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.models.PromotionBanner;
import com.muhil.zohokart.utils.VolleySingleton;
import com.muhil.zohokart.utils.ZohokartDAO;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class BannerFragment extends android.support.v4.app.Fragment
{

    View bannerFragment;
    ZohokartDAO zohokartDAO;
    ImageLoader imageLoader;
    PromotionBanner currentBanner, clickedBanner;
    BannerCommunicator communicator;
    List<Integer> productIds;
    List<Product> products;
    NetworkImageView bannerImageView;

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
        imageLoader = VolleySingleton.getInstance(getActivity()).getImageLoader();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        bannerFragment = inflater.inflate(R.layout.fragment_banner, container, false);
        currentBanner = (PromotionBanner) getArguments().getSerializable("banner");
        bannerImageView = (NetworkImageView) bannerFragment.findViewById(R.id.banner_image);

        bannerImageView.setImageUrl(currentBanner.getBanner(), imageLoader);

        bannerImageView.setTag(currentBanner);

        bannerImageView.setOnClickListener(
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
            communicator.setIfFromPager(true);
            communicator.openProductListPage(products);
        }
    }

    public interface BannerCommunicator
    {
        void openProductListPage(List<Product> products);
        void setIfFromPager(boolean status);
    }

}
