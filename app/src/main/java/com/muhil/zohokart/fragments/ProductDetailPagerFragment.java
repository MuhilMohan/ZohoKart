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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muhil.zohokart.R;
import com.muhil.zohokart.models.Account;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.models.specification.Specification;
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

    Set<String> recentlyUsed;
    View rootView;
    Product product;
    ImageView imageView;
    ZohokartDAO zohokartDAO;
    ImageView fullStar, halfStar, emptyStar;
    DecimalFormat decimalFormat = new DecimalFormat("#.00");
    Double stars;
    LinearLayout.LayoutParams params;
    SharedPreferences sharedPreferences;
    SharedPreferences recentlyUsedPref;
    SharedPreferences.Editor editor;
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
        product = (Product) getArguments().getSerializable("product");
        sharedPreferences = getActivity().getSharedPreferences(ZohoKartSharePreferences.LOGGED_ACCOUNT, Context.MODE_PRIVATE);
        email = sharedPreferences.getString(Account.EMAIL, "default");
        recentlyUsedPref = getActivity().getSharedPreferences(ZohoKartSharePreferences.RECENTLY_VIEWED_PRODUCTS, Context.MODE_PRIVATE);
        recentlyUsed = recentlyUsedPref.getStringSet(ZohoKartSharePreferences.PRODUCT_LIST, null);
        if (recentlyUsed == null)
        {
            recentlyUsed = new LinkedHashSet<>(10);
            recentlyUsed.add(String.valueOf(product.getId()));
        }
        else
        {
            if (!(recentlyUsed.contains(String.valueOf(product.getId()))))
            {
                if (recentlyUsed.size() == 10)
                {
                    String firstObject = null;
                    for (String string : recentlyUsed)
                    {
                        firstObject = string;
                        break;
                    }
                    recentlyUsed.remove(firstObject);
                    recentlyUsed.add(String.valueOf(product.getId()));
                }
                else
                {
                    recentlyUsed.add(String.valueOf(product.getId()));
                }
            }

        }
        editor = recentlyUsedPref.edit();
        editor.putStringSet(ZohoKartSharePreferences.PRODUCT_LIST, recentlyUsed);
        editor.apply();
        communicator.updateRecentlyViewed();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_product_detail_pager, container, false);

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

        if (zohokartDAO.checkInWishlist(product.getId(), email))
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

        return rootView;
    }

    public Snackbar getSnackbar(String textToDisplay)
    {
        Snackbar snackbar = Snackbar.make(rootView, textToDisplay, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();
        ((TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
        return snackbar;
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
                    getSnackbar("Added to wishlist").show();
                }
                else
                {
                    getSnackbar("Error while adding to wishlist").show();
                    ((ToggleButton) v).setChecked(false);
                }
            }
        }
        else
        {
            if (zohokartDAO.removeFromWishList(product.getId(), email))
            {
                getSnackbar("Removed from wishlist").show();
            } else
            {
                getSnackbar("Error while removing from wishlist").show();
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
        void updateRecentlyViewed();
    }

}
