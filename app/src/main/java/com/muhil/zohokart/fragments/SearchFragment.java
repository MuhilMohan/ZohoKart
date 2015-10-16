package com.muhil.zohokart.fragments;


import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.muhil.zohokart.R;
import com.muhil.zohokart.adapters.ProductListingAdapter;
import com.muhil.zohokart.comparators.PriceHighToLowComparator;
import com.muhil.zohokart.comparators.PriceLowToHighComparator;
import com.muhil.zohokart.comparators.StarsHighToLowComparator;
import com.muhil.zohokart.comparators.StarsLowToHighComparator;
import com.muhil.zohokart.decorators.DividerItemDecoration;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.utils.ZohokartDAO;

import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends android.support.v4.app.Fragment
{
    View searchLayout;
    RecyclerView recyclerView;
    List<Product> products;
    ZohokartDAO zohokartDAO;
    ProductListingAdapter productListingAdapter;

    public SearchFragment()
    {
        // Required empty public constructor
    }

    public static SearchFragment getInstance(String query)
    {
        SearchFragment searchFragment = new SearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString("query_string", query);
        searchFragment.setArguments(bundle);
        return searchFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        zohokartDAO = new ZohokartDAO(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        searchLayout = inflater.inflate(R.layout.fragment_search, container, false);

        final Bundle bundle = getArguments();
        recyclerView = (RecyclerView) searchLayout.findViewById(R.id.search_result);
        new SearchAsyncTask().execute(bundle.getString("query_string"));

        return searchLayout;
    }

    class SearchAsyncTask extends AsyncTask<String, Void, Void>
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            (searchLayout.findViewById(R.id.search_progress)).setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... params)
        {
            products = zohokartDAO.getProductsBySearchString(params[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);

            if (products.size() > 0)
            {
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                RecyclerView.ItemDecoration divider = new DividerItemDecoration(getActivity(), LinearLayout.VERTICAL);
                recyclerView.addItemDecoration(divider);
                productListingAdapter = new ProductListingAdapter(products, getActivity(), getActivity().getSupportFragmentManager(), searchLayout);
                recyclerView.setAdapter(productListingAdapter);

                (searchLayout.findViewById(R.id.search_result)).setVisibility(View.VISIBLE);
                (searchLayout.findViewById(R.id.search_progress)).setVisibility(View.GONE);

            }
            else
            {
                (searchLayout.findViewById(R.id.search_progress)).setVisibility(View.GONE);
                ((TextView) searchLayout.findViewById(R.id.empty_search)).setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "no products.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
