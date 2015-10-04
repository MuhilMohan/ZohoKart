package com.muhil.zohokart.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.muhil.zohokart.R;
import com.muhil.zohokart.models.Phone;
import com.muhil.zohokart.utils.DBHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by muhil-ga42 on 25/09/15.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    List<Phone> phoneList;
    Context context;
    DBHelper dbHelper;

    public RecyclerViewAdapter(List<Phone> phoneList, Context context) {

        this.phoneList = phoneList;
        this.context = context;
        dbHelper = new DBHelper(context);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        Context context = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.product_item_row, viewGroup, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {

        viewHolder.productListItemView.setTag(phoneList.get(position));
        viewHolder.name.setText(phoneList.get(position).getName());
        viewHolder.price.setText("Rs. " + String.valueOf(phoneList.get(position).getPrice()));
        Picasso.with(context).load(phoneList.get(position).getDisplayImageUrl()).centerCrop().resize(70, 70).into(viewHolder.displayImage);
        if (dbHelper.checkWishlist(phoneList.get(position).get_id())){
            viewHolder.wishListButton.setChecked(true);
        }
        else {
            viewHolder.wishListButton.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {
        return phoneList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name, price;
        ImageView displayImage;
        View productListItemView;
        ToggleButton wishListButton;

        public MyViewHolder(View itemView) {
            super(itemView);

            productListItemView = itemView;
            name = (TextView) itemView.findViewById(R.id.phoneName);
            price = (TextView) itemView.findViewById(R.id.price);
            displayImage = (ImageView) itemView.findViewById(R.id.displayImage);
            wishListButton = (ToggleButton) itemView.findViewById(R.id.wishListToggle);

        }
    }

}
