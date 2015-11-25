package com.muhil.zohokart.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.util.ArraySet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muhil.zohokart.R;
import com.muhil.zohokart.models.Account;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.models.specification.Specification;
import com.muhil.zohokart.utils.SnackBarProvider;
import com.muhil.zohokart.utils.ZohoKartSharePreferences;
import com.muhil.zohokart.utils.ZohokartDAO;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductDetailPagerFragment extends android.support.v4.app.Fragment implements View.OnClickListener
{

    View rootView;
    Product product;
    ImageView imageView;
    ZohokartDAO zohokartDAO;
    ImageView fullStar, halfStar, emptyStar;
    DecimalFormat decimalFormat = new DecimalFormat("#.00");
    Double stars;
    LinearLayout.LayoutParams params;
    SharedPreferences sharedPreferences;
    String email;
    ProductDetailPageCommunicator communicator;

    public static ProductDetailPagerFragment getInstance(Product product)
    {
        ProductDetailPagerFragment productDetailPagerFragment = new ProductDetailPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("product", product);
        productDetailPagerFragment.setArguments(bundle);
        return productDetailPagerFragment;
    }

    public ProductDetailPagerFragment()
    {
        // Required empty public constructor
    }

    public void setCommunicator(ProductDetailPageCommunicator communicator)
    {
        this.communicator = communicator;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        zohokartDAO = new ZohokartDAO(getActivity());
        sharedPreferences = getActivity().getSharedPreferences(ZohoKartSharePreferences.LOGGED_ACCOUNT, Context.MODE_PRIVATE);
        email = sharedPreferences.getString(Account.EMAIL, "default");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_product_detail_pager, container, false);

        product = (Product) getArguments().getSerializable("product");
        ((TextView) rootView.findViewById(R.id.title)).setText(product.getTitle());
        ((TextView) rootView.findViewById(R.id.description)).setText(product.getDescription());
        ((TextView) rootView.findViewById(R.id.price)).setText(String.valueOf(decimalFormat.format(product.getPrice())));
        ((TextView) rootView.findViewById(R.id.rating)).setText(String.valueOf(product.getRatings()) + " Ratings");
        ((TextView) rootView.findViewById(R.id.warranty_text)).setText(product.getWarranty());

        imageView = (ImageView) rootView.findViewById(R.id.product_thumbnail);
        Picasso.with(getActivity()).load(product.getThumbnail()).into(imageView);

        stars = product.getStars();

        ((LinearLayout) rootView.findViewById(R.id.stars)).removeAllViews();

        fillStars();

        if (communicator.checkWishlist(product.getId(), email))
        {
            ((ToggleButton) rootView.findViewById(R.id.wishlist_icon)).setChecked(true);
        }
        else
        {
            ((ToggleButton) rootView.findViewById(R.id.wishlist_icon)).setChecked(false);
        }

        (rootView.findViewById(R.id.wishlist_icon)).setOnClickListener(this);

        (rootView.findViewById(R.id.specifications)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                communicator.openSpecifications(product);
            }
        });

        imageView.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        communicator.showGallery(product.getId(), rootView.findViewById(R.id.title));
                    }
                }
        );

        return rootView;
    }

    @Override
    public void onClick(View v)
    {
        if (((ToggleButton) v).isChecked())
        {
            if (email != null && !email.equals(""))
            {
                if (zohokartDAO.addToWishlist(product.getId(), email))
                {
                    SnackBarProvider.getSnackbar("Added to wishlist", rootView).show();
                    communicator.invalidateOptions();
                }
                else
                {
                    SnackBarProvider.getSnackbar("Error while adding to wishlist", rootView).show();
                    ((ToggleButton) v).setChecked(false);
                }
            }
        }
        else
        {
            if (zohokartDAO.removeFromWishList(product.getId(), email))
            {
                SnackBarProvider.getSnackbar("Removed from wishlist", rootView).show();
                communicator.invalidateOptions();
            } else
            {
                SnackBarProvider.getSnackbar("Error while removing from wishlist", rootView).show();
                ((ToggleButton) v).setChecked(true);
            }
        }

    }

    private void fillStars()
    {
        for (int i = 0; i < 5; i++)
        {
            if (stars >= 1)
            {
                fullStar = new ImageView(getActivity());
                params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(1, 1, 1, 1);
                fullStar.setLayoutParams(params);
                fullStar.setImageResource(R.mipmap.fa_star_54_0_ffeb3b_none);
                ((LinearLayout) rootView.findViewById(R.id.stars)).addView(fullStar);
                stars = stars-1;
            }
            else if (stars > 0)
            {
                halfStar = new ImageView(getActivity());
                params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(1, 1, 1, 1);
                halfStar.setLayoutParams(params);
                halfStar.setImageResource(R.mipmap.fa_star_half_empty_54_0_ffeb3b_none);
                ((LinearLayout) rootView.findViewById(R.id.stars)).addView(halfStar);
                stars = stars-0.5;
            }
            else
            {
                emptyStar = new ImageView(getActivity());
                params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(1, 1, 1, 1);
                emptyStar.setLayoutParams(params);
                emptyStar.setImageResource(R.mipmap.fa_star_o_54_0_ffeb3b_none);
                ((LinearLayout) rootView.findViewById(R.id.stars)).addView(emptyStar);
            }
        }
    }

    public interface ProductDetailPageCommunicator
    {
        void openSpecifications(Product product);
        boolean checkWishlist(int productId, String email);
        void invalidateOptions();
        void showGallery(int productId, View view);
    }

}
