package com.muhil.zohokart.fragments;

import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.FloatMath;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.muhil.zohokart.R;
import com.squareup.picasso.Picasso;

public class GalleryItemFragment extends android.support.v4.app.Fragment
{

    View rootView;
    ImageView imageView;
    GalleryCommunicator communicator;

    public static Fragment getInstance(String imageUrl)
    {
        GalleryItemFragment galleryItemFragment = new GalleryItemFragment();
        Bundle args = new Bundle();
        args.putString("image_url", imageUrl);
        galleryItemFragment.setArguments(args);
        return galleryItemFragment;
    }

    public void setCommunicator(GalleryCommunicator communicator)
    {
        this.communicator = communicator;
    }

    public GalleryItemFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_gallery_item, container, false);
        imageView = (ImageView) rootView.findViewById(R.id.gallery_image);
        Picasso.with(getActivity()).load(getArguments().getString("image_url")).into(imageView);

        imageView.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        communicator.toggleGalleryList();
                    }
                }
        );

        return rootView;
    }

    public interface GalleryCommunicator
    {
        void toggleGalleryList();
        void setSelectedItemInPager(int position);
    }

}
