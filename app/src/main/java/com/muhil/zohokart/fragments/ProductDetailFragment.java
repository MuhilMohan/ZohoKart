package com.muhil.zohokart.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.muhil.zohokart.R;
import com.muhil.zohokart.adapters.ProductDetailPagerAdapter;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.utils.ZohokartDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductDetailFragment extends android.support.v4.app.Fragment
{

    ZohokartDAO zohokartDAO;
    View rootview;
    int currentPosition;
    List<Product> products;
    ViewPager productDetailPager;
    ProductDetailPagerAdapter productDetailPagerAdapter;

    public static android.support.v4.app.Fragment getInstance( int currentPosition, List<Product> products)
    {

        ProductDetailFragment productDetailFragment = new ProductDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("current_position", currentPosition);
        bundle.putParcelableArrayList("products", (ArrayList<? extends Parcelable>) products);
        productDetailFragment.setArguments(bundle);
        return productDetailFragment;

    }

    public ProductDetailFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        zohokartDAO = new ZohokartDAO(getActivity());
        currentPosition = getArguments().getInt("current_position");
        products = getArguments().getParcelableArrayList("products");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_product_detail, container, false);
        productDetailPager = (ViewPager) rootview.findViewById(R.id.product_view_pager);
        productDetailPagerAdapter = new ProductDetailPagerAdapter(getActivity().getSupportFragmentManager(), getActivity(), products);

        productDetailPager.setAdapter(productDetailPagerAdapter);
        productDetailPager.setCurrentItem(currentPosition);
        productDetailPager.setOffscreenPageLimit(3);

        checkInCart(currentPosition);

        productDetailPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                checkInCart(position);
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
                if (!zohokartDAO.checkInCart(product.getId()))
                {
                    if (zohokartDAO.addToCart(product.getId()))
                    {
                        getSnackbar("Product added to cart.").show();
                        (rootview.findViewById(R.id.add_to_cart)).setVisibility(View.GONE);
                        (rootview.findViewById(R.id.go_to_cart)).setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        getSnackbar("Error while adding product to cart.").show();
                    }
                }
                else
                {
                    getSnackbar("Product already in cart.").show();
                }
            }
        });

        (rootview.findViewById(R.id.go_to_cart)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CartFragment cartFragment = new CartFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_holder, cartFragment, "cart");
                fragmentTransaction.addToBackStack("cart_fragment_from_detail");
                fragmentTransaction.commit();
            }
        });

        return rootview;
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

        if (zohokartDAO.checkInCart(products.get(position).getId()))
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

}
