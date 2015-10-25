package com.muhil.zohokart.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.muhil.zohokart.R;
import com.muhil.zohokart.fragments.WishlistFragment;
import com.muhil.zohokart.models.Account;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.utils.ZohoKartSharePreferences;
import com.muhil.zohokart.utils.ZohokartDAO;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by muhil-ga42 on 04/10/15.
 */
public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder>
{
    ZohokartDAO zohokartDAO;

    public List<Product> wishlist;
    Context context;
    WishlistFragment wishlistFragment;
    WishlistFragment.WishlistCommunicator communicator;
    DecimalFormat decimalFormat = new DecimalFormat("#.00");

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String email;

    public WishlistAdapter(Context context, List<Product> wishlist, WishlistFragment wishlistFragment, WishlistFragment.WishlistCommunicator wishlistCommunicator)
    {
        this.context = context;
        this.wishlist = wishlist;
        this.wishlistFragment = wishlistFragment;
        this.communicator = wishlistCommunicator;
        zohokartDAO = new ZohokartDAO(context);
    }

    @Override
    public WishlistAdapter.WishlistViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.wishlist_item_row, parent, false);
        return new WishlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final WishlistAdapter.WishlistViewHolder holder, int position)
    {
        sharedPreferences = context.getSharedPreferences(ZohoKartSharePreferences.LOGGED_ACCOUNT, Context.MODE_PRIVATE);
        email = sharedPreferences.getString(Account.EMAIL, "default");
        holder.removeProductView.setTag(wishlist.get(position));
        holder.addToCart.setTag(wishlist.get(position));
        holder.title.setText(wishlist.get(position).getTitle());
        holder.description.setText(wishlist.get(position).getDescription());
        holder.price.setText("Rs. " + String.valueOf(decimalFormat.format(wishlist.get(position).getPrice())));
        Picasso.with(context).load(wishlist.get(position).getThumbnail()).into(holder.displayImage);

        if (zohokartDAO.checkInCart(wishlist.get(position).getId(), email))
        {

            holder.addToCart.setVisibility(View.GONE);
            holder.goToCart.setVisibility(View.VISIBLE);

        }

        holder.removeProductView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final Product product = (Product) v.getTag();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("");
                alertDialogBuilder.setMessage("Are you sure?");

                alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (zohokartDAO.removeFromWishList(product.getId(), email))
                        {
                            Toast.makeText(context, "Product removed from wishlist", Toast.LENGTH_SHORT).show();
                            int position = wishlist.indexOf(product);
                            wishlist.remove(position);
                            notifyItemRemoved(position);
                            wishlistFragment.updateWishlistCount(wishlist.size());
                            if (wishlist.size() == 0)
                            {
                                wishlistFragment.switchViewElement();
                            }
                        }
                        else
                        {
                            dialog.dismiss();
                            Toast.makeText(context, "error while removing from wishlist.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });

                alertDialogBuilder.show();
            }
        });

        holder.addToCart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Product product = (Product) v.getTag();
                if (zohokartDAO.addToCart(product.getId(), email))
                {
                    Toast.makeText(context, "product added to cart.", Toast.LENGTH_SHORT).show();
                    v.setVisibility(View.GONE);
                    holder.goToCart.setVisibility(View.VISIBLE);
                }
                else
                {
                    Toast.makeText(context, "error while adding to cart.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.goToCart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            communicator.openCart();

            }
        });

    }

    @Override
    public int getItemCount()
    {
        return wishlist.size();
    }

    class WishlistViewHolder extends RecyclerView.ViewHolder
    {

        TextView title, price, description;
        ImageView displayImage;
        LinearLayout removeProductView, addToCart, goToCart;

        public WishlistViewHolder(View itemView)
        {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            price = (TextView) itemView.findViewById(R.id.price);
            displayImage = (ImageView) itemView.findViewById(R.id.display_image);
            removeProductView = (LinearLayout) itemView.findViewById(R.id.removeProduct);
            addToCart = (LinearLayout) itemView.findViewById(R.id.add_to_cart_action);
            goToCart = (LinearLayout) itemView.findViewById(R.id.go_to_cart_action);


        }
    }

}
