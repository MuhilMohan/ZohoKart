package com.muhil.zohokart.adapters;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.muhil.zohokart.R;
import com.muhil.zohokart.fragments.ProductDetailFragment;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.utils.ZohokartDAO;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by muhil-ga42 on 04/10/15.
 */
public class ProductListingAdapter extends RecyclerView.Adapter<ProductListingAdapter.ProductViewHolder>
{

    ZohokartDAO zohokartDAO;

    List<Product> products;
    Context context;
    android.support.v4.app.FragmentManager fragmentManager;
    DecimalFormat decimalFormat = new DecimalFormat("#.00");
    double stars;
    ImageView fullStar, halfStar, emptyStar;

    android.support.v4.app.FragmentTransaction fragmentTransaction;

    View parentView;

    public ProductListingAdapter(List<Product> products, Context context, android.support.v4.app.FragmentManager fragmentManager, View parentView)
    {
        this.products = products;
        this.context = context;
        zohokartDAO = new ZohokartDAO(context);
        this.fragmentManager = fragmentManager;
        this.parentView = parentView;
    }

    @Override
    public ProductListingAdapter.ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.product_item_row, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductListingAdapter.ProductViewHolder holder, final int position)
    {
        holder.wishListButton.setTag(products.get(position));
        holder.title.setText(products.get(position).getTitle());
        holder.description.setText(products.get(position).getDescription());
        holder.price.setText("Rs. " + String.valueOf(decimalFormat.format(products.get(position).getPrice())));
        Picasso.with(context).load(products.get(position).getThumbnail()).into(holder.displayImage);
        holder.productRating.setText(String.valueOf(products.get(position).getRatings()) + " Ratings");
        stars = products.get(position).getStars();

        holder.productStars.removeAllViews();
        for (int i = 0; i < 5; i++)
        {
            if (stars >= 1)
            {
                fullStar = new ImageView(context);
                fullStar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                fullStar.setImageResource(R.mipmap.ic_star_black_18dp);
                holder.productStars.addView(fullStar);
                stars = stars-1;
            }
            else if (stars > 0)
            {
                halfStar = new ImageView(context);
                halfStar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                halfStar.setImageResource(R.mipmap.ic_star_half_black_18dp);
                holder.productStars.addView(halfStar);
                stars = stars-0.5;
            }
            else
            {
                emptyStar = new ImageView(context);
                emptyStar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                emptyStar.setImageResource(R.mipmap.ic_star_border_black_18dp);
                holder.productStars.addView(emptyStar);
            }
        }
        if (zohokartDAO.checkInWishlist(products.get(position).getId()))
        {
            holder.wishListButton.setChecked(true);
        }
        else
        {
            holder.wishListButton.setChecked(false);
        }
        holder.wishListButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                ToggleButton toggleButton = (ToggleButton) v;
                Product product = (Product) toggleButton.getTag();

                if (toggleButton.isChecked())
                {
                    if (zohokartDAO.addToWishlist(product.getId()))
                    {
                        getSnackbar("Added to wishlist").show();
                    }
                    else
                    {
                        getSnackbar("Error while adding to wishlist").show();
                        toggleButton.setChecked(false);
                    }
                }
                else
                {
                    if (zohokartDAO.removeFromWishList(product.getId()))
                    {
                        getSnackbar("Removed from wishlist").show();
                    }
                    else
                    {
                       getSnackbar("Error while removing from wishlist").show();
                        toggleButton.setChecked(true);
                    }
                }

            }
        });



    }

    public Snackbar getSnackbar(String textToDisplay)
    {
        Snackbar snackbar = Snackbar.make(parentView, textToDisplay, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();
        ((TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
        return snackbar;
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateDataSet(List<Product> newProductDataSet)
    {
        products = newProductDataSet;
        this.notifyDataSetChanged();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView title, price, description, productRating;
        ImageView displayImage;
        ToggleButton wishListButton;
        LinearLayout productStars;

        public ProductViewHolder(View itemView)
        {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            price = (TextView) itemView.findViewById(R.id.price);
            displayImage = (ImageView) itemView.findViewById(R.id.display_image);
            wishListButton = (ToggleButton) itemView.findViewById(R.id.wishlist_toggle);
            productStars = (LinearLayout) itemView.findViewById(R.id.product_stars);
            productRating = (TextView) itemView.findViewById(R.id.product_rating);

            itemView.setOnClickListener(this);

        }


        @Override
        public void onClick(View v)
        {
            int position = getLayoutPosition();
            ProductDetailFragment productDetailFragment = (ProductDetailFragment) ProductDetailFragment.getInstance(position, products);
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_holder, productDetailFragment, "product_detail_page");
            fragmentTransaction.addToBackStack("product_detail_page_fragment");
            fragmentTransaction.commit();
        }
    }

}
