package com.muhil.zohokart.view_holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.muhil.zohokart.R;
import com.muhil.zohokart.interfaces.ProductListCommunicator;
import com.muhil.zohokart.models.Product;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by muhil-ga42 on 29/10/15.
 */
public class HorizontalProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    Context context;
    TextView title, price, description;
    ImageView displayImage;
    ProductListCommunicator productListCommunicator;
    List<Product> products;
    DecimalFormat decimalFormat;

    public HorizontalProductViewHolder(Context context, View itemView, ProductListCommunicator productListCommunicator, List<Product> products)
    {
        super(itemView);
        this.context = context;
        this.productListCommunicator = productListCommunicator;
        this.products = products;
        title = (TextView) itemView.findViewById(R.id.title);
        description = (TextView) itemView.findViewById(R.id.description);
        price = (TextView) itemView.findViewById(R.id.price);
        displayImage = (ImageView) itemView.findViewById(R.id.display_image);
        decimalFormat = new DecimalFormat("#.00");
        itemView.setOnClickListener(this);
    }

    public void setDescription(int position)
    {
        this.description.setText(products.get(position).getDescription());
    }

    public void setDisplayImage(int position)
    {
        Picasso.with(context).load(products.get(position).getThumbnail()).into(this.displayImage);
    }

    public void setPrice(int position)
    {
        this.price.setText("Rs. " + decimalFormat.format(products.get(position).getPrice()));
    }

    public void setTitle(int position)
    {
        this.title.setText(products.get(position).getTitle());
    }

    @Override
    public void onClick(View v)
    {
        int position = getLayoutPosition();
        productListCommunicator.showProductDetailFragment(position, products);
    }
}