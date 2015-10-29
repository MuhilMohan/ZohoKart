package com.muhil.zohokart.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.muhil.zohokart.R;
import com.muhil.zohokart.fragments.MainFragment;
import com.muhil.zohokart.interfaces.ProductListCommunicator;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.view_holders.HorizontalProductViewHolder;
import com.muhil.zohokart.view_holders.ViewMoreHolder;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by muhil-ga42 on 28/10/15.
 */
public class HorizontalProductListingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    public static final int PRODUCT = 0;
    public static final int VIEW_MORE = 1;

    Context context;
    List<Object> products, productsCopy;
    List<Product> productList;
    ProductListCommunicator productListCommunicator;
    MainFragment.MainCommunicator mainCommunicator;
    LayoutInflater layoutInflater;
    View itemView;
    DecimalFormat decimalFormat;

    public HorizontalProductListingAdapter(List<Object> products, Context context, ProductListCommunicator communicator, MainFragment.MainCommunicator mainCommunicator)
    {
        this.context = context;
        this.products = products;
        this.productListCommunicator = communicator;
        this.mainCommunicator = mainCommunicator;
        decimalFormat = new DecimalFormat("#.00");
        makeProducts();
    }

    private void makeProducts()
    {
        productList = new ArrayList<>();
        for (Object product : products)
        {
            if (product instanceof Product)
            {
                productList.add((Product) product);
            }
        }
        Log.d("TOP_RATED_AS_PRODUCT", " " + productList.size());
    }

    @Override
    public int getItemViewType(int position)
    {
        if (products.get(position) instanceof Product)
        {
            return PRODUCT;
        }
        else if (products.get(position) instanceof String)
        {
            Log.d("VIEW_MORE", "entered view more");
            return VIEW_MORE;
        }
        else
        {
            return -1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType)
        {
            case PRODUCT:
                itemView = layoutInflater.inflate(R.layout.product_item_vert, parent, false);
                viewHolder = new HorizontalProductViewHolder(context, itemView, productListCommunicator, productList);
                break;

            case VIEW_MORE:
                itemView = layoutInflater.inflate(R.layout.view_more_item, parent, false);
                viewHolder = new ViewMoreHolder(itemView, mainCommunicator, productList);
                break;

            default:
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if (holder != null)
        {
            switch (holder.getItemViewType())
            {
                case PRODUCT:
                   HorizontalProductViewHolder productViewHolder = (HorizontalProductViewHolder) holder;
                   productViewHolder.setTitle(position);
                   productViewHolder.setDescription(position);
                   productViewHolder.setPrice(position);
                   productViewHolder.setDisplayImage(position);
                    break;

                case VIEW_MORE:
                    break;

                default:
                    break;
            }
        }

    }

    @Override
    public int getItemCount()
    {
        return products.size();
    }

}
