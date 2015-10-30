package com.muhil.zohokart.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.muhil.zohokart.R;
import com.muhil.zohokart.adapters.BannerPagerAdapter;
import com.muhil.zohokart.adapters.HorizontalProductListingAdapter;
import com.muhil.zohokart.interfaces.ProductListCommunicator;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.models.PromotionBanner;
import com.muhil.zohokart.utils.DpToPxConverter;
import com.muhil.zohokart.utils.ZohoKartSharePreferences;
import com.muhil.zohokart.utils.ZohokartDAO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment
{

    String viewMore;
    ZohokartDAO zohokartDAO;
    View rootView;
    ViewPager bannerViewPager;
    BannerPagerAdapter bannerPagerAdapter;
    List<PromotionBanner> promotionBanners;
    List<ImageView> pageIndicators;
    List<Product> products;
    List<Integer> productIds;
    String recentlyViewed;
    BannerFragment.BannerCommunicator bannerCommunicator;
    ProductListCommunicator productListCommunicator;
    MainCommunicator mainCommunicator;
    RecyclerView topRatedRecyclerView, recentlyViewedRecyclerView;
    HorizontalProductListingAdapter productListingAdapter;
    SharedPreferences recentlyViewedPref;

    public static MainFragment getInstance(List<PromotionBanner> promotionBanners)
    {
        MainFragment mainFragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("promotion_banners", (Serializable) promotionBanners);
        mainFragment.setArguments(bundle);
        return mainFragment;
    }

    public MainFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        zohokartDAO = new ZohokartDAO(getActivity());
        recentlyViewedPref = getActivity().getSharedPreferences(ZohoKartSharePreferences.RECENTLY_VIEWED_PRODUCTS, Context.MODE_PRIVATE);
        viewMore = "view more";
    }

    public void setCommunicator(BannerFragment.BannerCommunicator bannerCommunicator, ProductListCommunicator productListCommunicator,
                                MainCommunicator mainCommunicator)
    {
        this.bannerCommunicator = bannerCommunicator;
        this.productListCommunicator = productListCommunicator;
        this.mainCommunicator = mainCommunicator;
    }

    public void resetRecentlyUsed()
    {
        new RecentlyUsedTask().execute();
    }

    public void setLoadingVisible()
    {
        (rootView.findViewById(R.id.loading)).setVisibility(View.VISIBLE);
    }

    public void setLoadingGone()
    {
        (rootView.findViewById(R.id.loading)).setVisibility(View.GONE);
    }

    public void showTopRatedProducts()
    {
        new TopRatedViewTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        if (rootView != null)
        {
            return rootView;
        }
        else
        {
            rootView = inflater.inflate(R.layout.fragment_main, container, false);
            promotionBanners = (List<PromotionBanner>) getArguments().getSerializable("promotion_banners");
            bannerViewPager = (ViewPager) rootView.findViewById(R.id.banner_viewpager);
            bannerPagerAdapter = new BannerPagerAdapter(getActivity().getSupportFragmentManager(), promotionBanners, bannerCommunicator);
            bannerViewPager.setAdapter(bannerPagerAdapter);
            addDots();
            bannerViewPager.addOnPageChangeListener(
                    new ViewPager.OnPageChangeListener()
                    {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
                        {
                        }

                        @Override
                        public void onPageSelected(int position)
                        {
                            selectDot(position);
                        }

                        @Override
                        public void onPageScrollStateChanged(int state)
                        {
                        }
                    }
            );

            topRatedRecyclerView = (RecyclerView) rootView.findViewById(R.id.top_rated);
            topRatedRecyclerView.setHasFixedSize(true);
            new TopRatedTask().execute();

            recentlyViewedRecyclerView = (RecyclerView) rootView.findViewById(R.id.recently_viewed);
            recentlyViewedRecyclerView.setHasFixedSize(true);
            new RecentlyUsedTask().execute();

            return rootView;
        }
    }

    public void addDots()
    {
        pageIndicators = new ArrayList<>();
        LinearLayout pageIndicatorHolder = (LinearLayout) rootView.findViewById(R.id.pageIndicator_holder);

        for (int i = 0; i < promotionBanners.size(); i++)
        {
            ImageView pageIndicator = new ImageView(getActivity());
            pageIndicator.setImageResource(R.mipmap.fa_circle_o_256_0_ffffff_none);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DpToPxConverter.dpToPx(9), DpToPxConverter.dpToPx(9));
            params.setMargins(2, 2, 2, 2);
            pageIndicatorHolder.addView(pageIndicator, params);
            pageIndicators.add(pageIndicator);
        }
        pageIndicators.get(0).setImageResource(R.mipmap.fa_circle_256_0_ff9800_none);
    }

    public void selectDot(int selectedPage) {
        for(int i = 0; i < promotionBanners.size(); i++) {
            int drawableId = (i==selectedPage)?(R.mipmap.fa_circle_256_0_ff9800_none):(R.mipmap.fa_circle_o_256_0_ffffff_none);
            pageIndicators.get(i).setImageResource(drawableId);
        }
    }

    class TopRatedTask extends AsyncTask<Void, Void, List<Product>>
    {

        @Override
        protected List<Product> doInBackground(Void... params)
        {
            return zohokartDAO.getTopRatedProductsWithLimit();
        }

        @Override
        protected void onPostExecute(final List<Product> products)
        {
            super.onPostExecute(products);
            Log.d("TOP_RATED", " " + products.size());
            if (products != null)
            {
                List<Object> productList = new ArrayList<>();
                for (Product product : products)
                {
                    productList.add(product);
                }
                productList.add("view more");
                Log.d("TOP_RATED_AS_OBJECT", " " + productList.size());

                topRatedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                productListingAdapter = new HorizontalProductListingAdapter(productList, getActivity(), productListCommunicator, mainCommunicator);
                topRatedRecyclerView.setAdapter(productListingAdapter);
                (rootView.findViewById(R.id.top_rated_view_all)).setOnClickListener(
                        new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {

                                new TopRatedViewTask().execute();
                            }
                        }
                );
            }
        }
    }

    class RecentlyUsedTask extends AsyncTask<Void, Void, List<Product>>
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            (rootView.findViewById(R.id.recently_viewed_empty)).setVisibility(View.GONE);
        }

        @Override
        protected List<Product> doInBackground(Void... params)
        {
            recentlyViewed = recentlyViewedPref.getString(ZohoKartSharePreferences.PRODUCT_LIST, null);
            if (recentlyViewed != null)
            {
                productIds = new ArrayList<>();
                products = new ArrayList<>();
                String[] recentlyViewedProducts = TextUtils.split(recentlyViewed, ",");
                for (String string : recentlyViewedProducts)
                {
                    if (!(string.equals("")))
                    {
                        productIds.add(Integer.parseInt(string));
                    }
                }
                Collections.reverse(productIds);
                products = zohokartDAO.getProductsForProductIds(productIds);
                return products;
            }
            else
            {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final List<Product> products)
        {
            super.onPostExecute(products);
            if (products != null)
            {
                List<Object> productList = new ArrayList<>();
                for (Product product : products)
                {
                    productList.add(product);
                }

                Toast.makeText(getActivity(), ""+products.size()+"", Toast.LENGTH_SHORT).show();
                recentlyViewedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                productListingAdapter = new HorizontalProductListingAdapter(productList, getActivity(), productListCommunicator, mainCommunicator);
                recentlyViewedRecyclerView.setAdapter(productListingAdapter);
                (rootView.findViewById(R.id.recently_viewed_view_all)).setOnClickListener(
                        new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                mainCommunicator.openProductList(products);
                            }
                        }
                );
            }
            else
            {
                (rootView.findViewById(R.id.recently_viewed_empty)).setVisibility(View.VISIBLE);
            }
        }
    }

    class TopRatedViewTask extends AsyncTask<Void, Void, List<Product>>
    {

        @Override
        protected List<Product> doInBackground(Void... params)
        {
            return zohokartDAO.getTopRatedProducts();
        }

        @Override
        protected void onPostExecute(final List<Product> products)
        {
            super.onPostExecute(products);
            Log.d("TOP_RATED", " " + products.size());
            mainCommunicator.openProductList(products);
        }
    }

    public interface MainCommunicator
    {
        void openProductList(List<Product> products);
        void showAllTopRatedProducts();
    }

}
