package com.muhil.zohokart.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.muhil.zohokart.fragments.MainFragment;
import com.muhil.zohokart.models.Product;

import java.util.List;


/**
 * Created by muhil-ga42 on 29/10/15.
 */
public class ViewMoreHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{

    MainFragment.MainCommunicator mainCommunicator;
    List<Product> products;

    public ViewMoreHolder(View itemView, MainFragment.MainCommunicator mainCommunicator, List<Product> products)
    {
        super(itemView);
        itemView.setOnClickListener(this);
        this.mainCommunicator = mainCommunicator;
        this.products = products;
    }

    @Override
    public void onClick(View v)
    {
        mainCommunicator.showAllTopRatedProducts();
    }
}
