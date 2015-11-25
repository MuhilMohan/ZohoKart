package com.muhil.zohokart.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.muhil.zohokart.R;
import com.muhil.zohokart.utils.VolleySingleton;

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
        if (rootView != null)
        {
            return rootView;
        }
        else
        {
            rootView = inflater.inflate(R.layout.fragment_gallery_item, container, false);
            imageView = (ImageView) rootView.findViewById(R.id.gallery_image);

            final ImageRequest imageRequest = new ImageRequest(getArguments().getString("image_url"),
                    new Response.Listener<Bitmap>()
                    {
                        @Override
                        public void onResponse(Bitmap response)
                        {
                            imageView.setImageBitmap(response);
                        }
                    },
                    0, 0, null,
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            imageView.setImageResource(R.drawable.placeholder);
                        }
                    });

            VolleySingleton.getInstance(getActivity()).addToRequestQueue(imageRequest);

            rootView.findViewById(R.id.root_view).setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            if (communicator != null)
                            {
                                communicator.toggleGalleryList();
                            }
                        }
                    }
            );

            return rootView;
        }
    }

    public interface GalleryCommunicator
    {
        void toggleGalleryList();
        void setSelectedItemInPager(int position);
    }

}
