package com.muhil.zohokart.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.muhil.zohokart.R;
import com.muhil.zohokart.adapters.WishlistAdapter;
import com.muhil.zohokart.models.Account;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.utils.ZohoKartSharePreferences;
import com.muhil.zohokart.utils.ZohokartDAO;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class WishlistFragment extends android.support.v4.app.Fragment
{

    View wishlistFragment;
    ZohokartDAO zohokartDAO;
    List<Product> productsInWishlist;
    RecyclerView wishlistRecyclerView;
    WishlistAdapter wishlistAdapter;
    TextView emptyTextView;
    WishlistCommunicator communicator;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String email;

    public WishlistFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        zohokartDAO = new ZohokartDAO(getActivity());
        sharedPreferences = getActivity().getSharedPreferences(ZohoKartSharePreferences.LOGGED_ACCOUNT, Context.MODE_PRIVATE);
    }

    public void setCommunicator(WishlistCommunicator communicator)
    {
        this.communicator = communicator;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        wishlistFragment = inflater.inflate(R.layout.fragment_wishlist, container, false);

        wishlistRecyclerView = (RecyclerView) wishlistFragment.findViewById(R.id.wishlist);
        emptyTextView = (TextView) wishlistFragment.findViewById(R.id.empty_text);

        new WishlistAsyncTask().execute();

        return wishlistFragment;
    }

    public void updateWishlistCount(int count)
    {
        ((TextView) wishlistFragment.findViewById(R.id.wishlist_count)).setText("(" + count + ")");
    }

    public void switchViewElement()
    {
        wishlistRecyclerView.setVisibility(View.GONE);
        emptyTextView.setVisibility(View.VISIBLE);
        (wishlistFragment.findViewById(R.id.wishlist_header)).setVisibility(View.GONE);
    }

    class WishlistAsyncTask extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... params)
        {
            email = sharedPreferences.getString(Account.EMAIL, "default");
            productsInWishlist = zohokartDAO.getProductsFromWishlist(email);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);

            if (productsInWishlist != null)
            {
                if (productsInWishlist.size() > 0)
                {
                    updateWishlistCount(productsInWishlist.size());
                    wishlistAdapter = new WishlistAdapter(getActivity(), productsInWishlist, WishlistFragment.this, communicator);
                    wishlistRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    wishlistRecyclerView.setAdapter(wishlistAdapter);
                }
                else
                {
                    switchViewElement();
                }
            }
            else
            {
                switchViewElement();
            }
        }
    }

    public interface WishlistCommunicator
    {
        void openCart();
    }

}
