package com.muhil.zohokart.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

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
    ProductDetailPagerFragment.ProductDetailPageCommunicator productDetailPageCommunicator;

    public ProductDetailPagerAdapter(FragmentManager fm, Context context, List<Product> products, ProductDetailPagerFragment.ProductDetailPageCommunicator productDetailPageCommunicator)
    {
        super(fm);
        this.context = context;
        this.products = products;
        this.productDetailPageCommunicator = productDetailPageCommunicator;
    }


    @Override
    public Fragment getItem(int position)
    {
        ProductDetailPagerFragment productDetailPagerFragment = ProductDetailPagerFragment.getInstance(products.get(position));
        productDetailPagerFragment.setCommunicator(productDetailPageCommunicator);
        return productDetailPagerFragment;
    }

    @Override
    public int getCount()
    {
        return products.size();
    }

}
