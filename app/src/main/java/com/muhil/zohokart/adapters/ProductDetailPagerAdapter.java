package com.muhil.zohokart.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.muhil.zohokart.fragments.ProductDetailPagerFragment;
import com.muhil.zohokart.models.Product;

import java.util.List;

/**
 * Created by muhil-ga42 on 13/10/15.
 */
public class ProductDetailPagerAdapter extends FragmentStatePagerAdapter
{

    Context context;
    List<Product> products;

    public ProductDetailPagerAdapter(FragmentManager fm, Context context, List<Product> products)
    {
        super(fm);
        this.context = context;
        this.products = products;
    }


    @Override
    public Fragment getItem(int position)
    {
        return ProductDetailPagerFragment.getInstance(products.get(position));
    }

    @Override
    public int getCount()
    {
        return products.size();
    }
}
