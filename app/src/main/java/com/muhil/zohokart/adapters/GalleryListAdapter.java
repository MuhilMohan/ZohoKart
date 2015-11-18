package com.muhil.zohokart.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.muhil.zohokart.R;
import com.muhil.zohokart.activities.ProductGalleryActivity;
import com.muhil.zohokart.fragments.GalleryItemFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muhil-ga42 on 18/11/15.
 */
public class GalleryListAdapter extends RecyclerView.Adapter<GalleryListAdapter.GalleryListItemViewHolder>
{

    public static List<GalleryListItemViewHolder> galleryListItems = new ArrayList<>();

    GalleryItemFragment.GalleryCommunicator communicator;
    ProductGalleryActivity productGalleryActivity;
    List<String> imageLinks;
    LayoutInflater inflater;

    public GalleryListAdapter(ProductGalleryActivity productGalleryActivity, List<String> imageLinks)
    {
        this.communicator = productGalleryActivity;
        this.productGalleryActivity = productGalleryActivity;
        this.imageLinks = imageLinks;
        inflater = LayoutInflater.from(productGalleryActivity);
    }

    @Override
    public GalleryListAdapter.GalleryListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View galleryListView = inflater.inflate(R.layout.gallery_list_item, parent, false);
        GalleryListItemViewHolder galleryListItemViewHolder = new GalleryListItemViewHolder(galleryListView);
        galleryListItems.add(galleryListItemViewHolder);
        return galleryListItemViewHolder;
    }

    @Override
    public void onBindViewHolder(GalleryListAdapter.GalleryListItemViewHolder holder, int position)
    {
        if (position == 0)
        {
            holder.itemView.findViewById(R.id.gallery_list_item_selected).setVisibility(View.VISIBLE);
        }
        Picasso.with(productGalleryActivity).load(imageLinks.get(position)).into(holder.galleryListImage);
    }

    @Override
    public int getItemCount()
    {
        return imageLinks.size();
    }

    class GalleryListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        ImageView galleryListImage;
        View itemView;

        public GalleryListItemViewHolder(View itemView)
        {
            super(itemView);
            this.itemView = itemView;
            galleryListImage = (ImageView) itemView.findViewById(R.id.gallery_list_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            setSelectedItem(getLayoutPosition());
            communicator.setSelectedItemInPager(getLayoutPosition());
        }

    }

    public static void setSelectedItem(int position)
    {
        for (GalleryListItemViewHolder galleryListItemViewHolder : galleryListItems)
        {
            galleryListItemViewHolder.itemView.findViewById(R.id.gallery_list_item_selected).setVisibility(View.GONE);
        }
        galleryListItems.get(position).itemView.findViewById(R.id.gallery_list_item_selected).setVisibility(View.VISIBLE);
    }

}
