package com.muhil.zohokart.comparators;

import com.muhil.zohokart.models.Product;

import java.util.Comparator;

/**
 * Created by muhil-ga42 on 13/10/15.
 */
public class PriceLowToHighComparator implements Comparator {
    @Override
    public int compare(Object lhs, Object rhs) {
        Product firstProduct = (Product) lhs;
        Product secondProduct = (Product) rhs;

        if (firstProduct.getPrice() == secondProduct.getPrice())
        {
            return 0;
        }
        else if (firstProduct.getPrice() > secondProduct.getPrice())
        {
            return 1;
        }
        else
        {
            return -1;
        }
    }
}
