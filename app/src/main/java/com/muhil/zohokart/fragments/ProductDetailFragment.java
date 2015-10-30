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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.muhil.zohokart.R;
import com.muhil.zohokart.adapters.ProductDetailPagerAdapter;
import com.muhil.zohokart.models.Account;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.utils.ZohoKartSharePreferences;
import com.muhil.zohokart.utils.ZohokartDAO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductDetailFragment extends android.support.v4.app.Fragment
{

    ZohokartDAO zohokartDAO;
    View rootview;
    int currentPosition;
    List<Product> products;
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
        currentPosition = getArguments().getInt("current_position");
        products = getArguments().getParcelableArrayList("products");
        productIds = new LinkedList<>();
        sharedPreferences = getActivity().getSharedPreferences(ZohoKartSharePreferences.LOGGED_ACCOUNT, Context.MODE_PRIVATE);
        email = sharedPreferences.getString(Account.EMAIL, "");
        addToRecentlyViewed(currentPosition);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        if (rootview != null)
        {
            return rootview;
        }
        else
        {
            rootview = inflater.inflate(R.layout.fragment_product_detail, container, false);
            productDetailPager = (ViewPager) rootview.findViewById(R.id.product_view_pager);
            productDetailPagerAdapter = new ProductDetailPagerAdapter(getActivity().getSupportFragmentManager(), getActivity(), products, productDetailPageCommunicator);
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
                    addToRecentlyViewed(position);
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
                                getSnackbar("Product added to cart.").show();
                                (rootview.findViewById(R.id.add_to_cart)).setVisibility(View.GONE);
                                (rootview.findViewById(R.id.go_to_cart)).setVisibility(View.VISIBLE);
                            } else
                            {
                                getSnackbar("Error while adding product to cart.").show();
                            }
                        } else
                        {
                            getSnackbar("Product already in cart.").show();
                        }
                    }
                    else
                    {
                        communicator.openLoginPage();
                    }
                }
            });

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
    }


    private void addToRecentlyViewed(int position)
    {
        recentlyUsedPref = getActivity().getSharedPreferences(ZohoKartSharePreferences.RECENTLY_VIEWED_PRODUCTS, Context.MODE_PRIVATE);
        recentlyViewed = recentlyUsedPref.getString(ZohoKartSharePreferences.PRODUCT_LIST, null);

        if (recentlyViewed == null)
        {
            recentlyViewed = String.valueOf(products.get(position).getId()) + ",";
        }
        else
        {
            String[] recentlyViewedProducts = TextUtils.split(recentlyViewed, ",");
            for (String string : recentlyViewedProducts)
            {
                if (!(string.equals("")))
                {
                    productIds.add(Integer.parseInt(string));
                }
            }
            if (!(productIds.contains(products.get(position).getId())))
            {
                if (productIds.size() < 5)
                {
                    productIds.add(products.get(position).getId());
                }
                else
                {
                    productIds.remove(0);
                    productIds.add(products.get(position).getId());
                }
            }
            else
            {
                productIds.remove(productIds.indexOf(products.get(position).getId()));
                productIds.add(products.get(position).getId());
            }
            recentlyViewed = "";
            for (Integer productId : productIds)
            {
                recentlyViewed = recentlyViewed + String.valueOf(productId) + ",";
            }
        }

        editor = recentlyUsedPref.edit();
        editor.putString(ZohoKartSharePreferences.PRODUCT_LIST, recentlyViewed);
        editor.apply();
        communicator.updateRecentlyViewed();
    }


    public Snackbar getSnackbar(String textToDisplay)
    {
        Snackbar snackbar = Snackbar.make(rootview, textToDisplay, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();
        ((TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
        return snackbar;
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
        void openLoginPage();
        void updateRecentlyViewed();
    }

}
