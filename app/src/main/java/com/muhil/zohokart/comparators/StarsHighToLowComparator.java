package com.muhil.zohokart.comparators;

import com.muhil.zohokart.models.Product;

import java.util.Comparator;

/**
 * Created by muhil-ga42 on 13/10/15.
 */
public class StarsHighToLowComparator implements Comparator
{
    @Override
    public int compare(Object lhs, Object rhs)
    {
        Product firstProduct = (Product) lhs;
        Product secondProduct = (Product) rhs;

        if (firstProduct.getStars() == secondProduct.getStars())
        {
            return 0;
        }
        else if (firstProduct.getStars() < secondProduct.getStars())
        {
            return 1;
        }
        else
        {
            return -1;
        }
    }
}
