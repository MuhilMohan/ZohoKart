package com.muhil.zohokart.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.muhil.zohokart.MainActivity;
import com.muhil.zohokart.R;
import com.muhil.zohokart.adapters.ProductDetailPagerAdapter;
import com.muhil.zohokart.models.Account;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.models.ZohoKartFragments;
import com.muhil.zohokart.utils.SnackBarProvider;
import com.muhil.zohokart.utils.ZohoKartSharePreferences;
import com.muhil.zohokart.utils.ZohokartDAO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductDetailFragment extends android.support.v4.app.Fragment
{

    ZohokartDAO zohokartDAO;
    View rootview;
    int currentPosition, pagerPosition;
    List<Product> products, productsInCart;
    List<Integer> productIds;
    String recentlyViewed;
    ViewPager productDetailPager;
    ProductDetailPagerAdapter productDetailPagerAdapter;
    ProductDetailCommunicator communicator;
    ProductDetailPagerFragment.ProductDetailPageCommunicator productDetailPageCommunicator;
    SharedPreferences sharedPreferences;
    SharedPreferences recentlyUsedPref;
    SharedPreferences.Editor editor;
    String email;

    public static ProductDetailFragment getInstance( int currentPosition, List<Product> products)
    {

        ProductDetailFragment productDetailFragment = new ProductDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("current_position", currentPosition);
        bundle.putSerializable("products", (Serializable) products);
        productDetailFragment.setArguments(bundle);
        return productDetailFragment;

    }

    public ProductDetailFragment()
    {
        // Required empty public constructor
    }

    public void setCommunicator(ProductDetailCommunicator communicator, ProductDetailPagerFragment.ProductDetailPageCommunicator productDetailPageCommunicator)
    {
        this.communicator = communicator;
        this.productDetailPageCommunicator = productDetailPageCommunicator;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        zohokartDAO = new ZohokartDAO(getActivity());
        productIds = new ArrayList<>();
        sharedPreferences = getActivity().getSharedPreferences(ZohoKartSharePreferences.LOGGED_ACCOUNT, Context.MODE_PRIVATE);
        email = sharedPreferences.getString(Account.EMAIL, "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        currentPosition = getArguments().getInt("current_position");
        products = getArguments().getParcelableArrayList("products");
        addToRecentlyViewed(this.products.get(currentPosition).getId());
        rootview = inflater.inflate(R.layout.fragment_product_detail, container, false);
        communicator.lockDrawer();

        productDetailPager = (ViewPager) rootview.findViewById(R.id.product_view_pager);
        productDetailPagerAdapter = new ProductDetailPagerAdapter(getActivity().getSupportFragmentManager(), getActivity(), products, productDetailPageCommunicator, this);
        productDetailPager.setAdapter(productDetailPagerAdapter);
        productDetailPager.setCurrentItem(currentPosition);
        checkInCart(currentPosition);

        productDetailPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                checkInCart(position);
                addToRecentlyViewed(ProductDetailFragment.this.products.get(position).getId());
                communicator.tellToMainParameters(position, products);
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });

        (rootview.findViewById(R.id.add_to_cart)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Product product = products.get(productDetailPager.getCurrentItem());
                if (!email.equals(""))
                {
                    if (!zohokartDAO.checkInCart(product.getId(), email))
                    {
                        if (zohokartDAO.addToCart(product.getId(), email))
                        {
                            SnackBarProvider.getSnackbar("Product added to cart.", rootview).show();
                            communicator.invalidateOptions();
                            communicator.setCartNotificationAlarm(email);
                            (rootview.findViewById(R.id.add_to_cart)).setVisibility(View.GONE);
                            (rootview.findViewById(R.id.go_to_cart)).setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            SnackBarProvider.getSnackbar("Error while adding product to cart.", rootview).show();
                        }
                    }
                    else
                    {
                        SnackBarProvider.getSnackbar("Product already in cart.", rootview).show();
                    }
                }
                else
                {
                    communicator.openLoginPage(ZohoKartFragments.PRODUCT_DETAIL_FRAGMENT);
                }
            }
        });

        (rootview.findViewById(R.id.buy_now)).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Product product = products.get(productDetailPager.getCurrentItem());
                        productIds.clear();
                        productIds.add(product.getId());
                        communicator.openCheckout(productIds);
                        productIds.clear();

                    }
                }
        );

        (rootview.findViewById(R.id.go_to_cart)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                communicator.openCart();
            }
        });

        return rootview;
    }

    public void toogleCartAction()
    {
        (rootview.findViewById(R.id.add_to_cart)).setVisibility(View.GONE);
        (rootview.findViewById(R.id.go_to_cart)).setVisibility(View.VISIBLE);
    }

    private void addToRecentlyViewed(int productId)
    {
        recentlyUsedPref = getActivity().getSharedPreferences(ZohoKartSharePreferences.RECENTLY_VIEWED_PRODUCTS, Context.MODE_PRIVATE);
        recentlyViewed = recentlyUsedPref.getString(ZohoKartSharePreferences.PRODUCT_LIST, null);

        if (recentlyViewed == null)
        {
            recentlyViewed = String.valueOf(productId);
        }
        else
        {
            String[] recentlyViewedProducts = TextUtils.split(recentlyViewed, ", ");
            recentlyViewed = "";
            for (String string : recentlyViewedProducts)
            {
                if (!(string.equals("")))
                {
                    productIds.add(Integer.parseInt(string));
                }
            }
            if (!(productIds.contains(productId)))
            {
                if (productIds.size() < 5)
                {
                    productIds.add(productId);
                }
                else
                {
                    productIds.remove(0);
                    productIds.add(productId);
                }
            }
            else
            {
                productIds.remove(productIds.indexOf(productId));
                productIds.add(productId);
            }
            recentlyViewed = productIds.toString().substring(1, productIds.toString().length()-1);
            Log.d("RECENTLY_VIEWED", recentlyViewed);
        }

        editor = recentlyUsedPref.edit();
        editor.putString(ZohoKartSharePreferences.PRODUCT_LIST, recentlyViewed);
        editor.apply();
        productIds.clear();
        communicator.updateRecentlyViewed();
    }

    public void checkInCart(int position)
    {

        if (zohokartDAO.checkInCart(products.get(position).getId(), email))
        {
            (rootview.findViewById(R.id.add_to_cart)).setVisibility(View.GONE);
            (rootview.findViewById(R.id.go_to_cart)).setVisibility(View.VISIBLE);
        }
        else
        {
            (rootview.findViewById(R.id.add_to_cart)).setVisibility(View.VISIBLE);
            (rootview.findViewById(R.id.go_to_cart)).setVisibility(View.GONE);
        }

    }

    public interface ProductDetailCommunicator
    {
        void openCart();
        void openLoginPage(String tag);
        void updateRecentlyViewed();
        void lockDrawer();
        void openCheckout(List<Integer> productIds);
        void tellToMainParameters(int position, List<Product> products);
        void invalidateOptions();
        void setCartNotificationAlarm(String email);
    }

}
