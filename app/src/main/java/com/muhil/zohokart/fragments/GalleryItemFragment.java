package com.muhil.zohokart.fragments;

import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.FloatMath;
import android.view.GestureDetector;
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

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    View rootView;
    GestureDetector gestureDetector;
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
        gestureDetector = new GestureDetector(getActivity(), new SwipeDetector());
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

        rootView.findViewById(R.id.root_view).setOnTouchListener(
                new View.OnTouchListener()
                {
                    @Override
                    public boolean onTouch(View v, MotionEvent event)
                    {
                        gestureDetector.onTouchEvent(event);
                        return true;
                    }
                }
        );

        return rootView;
    }

    public interface GalleryCommunicator
    {
        void toggleGalleryList(boolean flag);
        void setSelectedItemInPager(int position);
    }

    class SwipeDetector extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onDown(MotionEvent e)
        {
            return super.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY)
            {
                communicator.toggleGalleryList(true); //bottom to top => UP swipe
            }
            else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY)
            {
                communicator.toggleGalleryList(false); //top to bottom => DOWN swipe
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

}
