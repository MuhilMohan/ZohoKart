package com.muhil.zohokart.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.muhil.zohokart.R;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.utils.DBHelper;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by muhil-ga42 on 04/10/15.
 */
public class ProductListingAdapter extends RecyclerView.Adapter<ProductListingAdapter.ProductViewHolder> {

    List<Product> productList;
    Context context;
    DBHelper dbHelper;
    DecimalFormat decimalFormat = new DecimalFormat("#.00");

    public ProductListingAdapter(List<Product> productList, Context context) {

        this.productList = productList;
        this.context = context;
        dbHelper = new DBHelper(context);

    }

    @Override
    public ProductListingAdapter.ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.product_item_row, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductListingAdapter.ProductViewHolder holder, final int position) {

        holder.productListItemView.setTag(productList.get(position));
        holder.title.setText(productList.get(position).getTitle());
        holder.description.setText(productList.get(position).getDescription());
        holder.wishListButton.setChecked(false);
        holder.price.setText("Rs. " + String.valueOf(decimalFormat.format(productList.get(position).getPrice())));
        Picasso.with(context).load(productList.get(position).getThumbnail()).into(holder.displayImage);
        if (dbHelper.checkWishlist(productList.get(position).getId())) {
            holder.wishListButton.setChecked(true);
        } else {
            holder.wishListButton.setChecked(false);
        }

        holder.wishListButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if(dbHelper.addToWishlist(productList.get(position).getId())){
                        Toast.makeText(context, "Product added to wishlist", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(context, "error while adding to wishlist.", Toast.LENGTH_SHORT).show();
                        buttonView.setChecked(false);
                    }
                }
                else {
                    if(dbHelper.removeFromWishList(productList.get(position).getId())){
                        Toast.makeText(context, "Product removed from wishlist", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(context, "error while removing from wishlist.", Toast.LENGTH_SHORT).show();
                        buttonView.setChecked(true);
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView title, price, description;
        ImageView displayImage;
        View productListItemView;
        ToggleButton wishListButton;

        public ProductViewHolder(View itemView) {
            super(itemView);

            productListItemView = itemView;
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            price = (TextView) itemView.findViewById(R.id.price);
            displayImage = (ImageView) itemView.findViewById(R.id.displayImage);
            wishListButton = (ToggleButton) itemView.findViewById(R.id.wishListToggle);

        }
    }

}
