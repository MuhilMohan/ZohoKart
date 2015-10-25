package com.muhil.zohokart.fragments;


import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.muhil.zohokart.R;
import com.muhil.zohokart.adapters.ProductListingAdapter;
import com.muhil.zohokart.comparators.PriceHighToLowComparator;
import com.muhil.zohokart.comparators.PriceLowToHighComparator;
import com.muhil.zohokart.comparators.StarsHighToLowComparator;
import com.muhil.zohokart.comparators.StarsLowToHighComparator;
import com.muhil.zohokart.interfaces.ProductListCommunicator;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.utils.ZohokartDAO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductListFragment extends android.support.v4.app.Fragment
{
    ZohokartDAO zohokartDAO;
    List<Product> productList;
    RecyclerView recyclerView;
    ProductListingAdapter productListingAdapter;
    String[] sortingItems;
    View productListFragment;
    Bundle bundle;
    ProductListCommunicator communicator;

    public ProductListFragment()
    {
        // Required empty public constructor
    }

    public static ProductListFragment getInstance(List<Product> products)
    {
        ProductListFragment productListFragment = new ProductListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("products", (Serializable) products);
        productListFragment.setArguments(bundle);
        return productListFragment;
    }

    public void setCommunicator(ProductListCommunicator communicator)
    {
        this.communicator = communicator;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sortingItems = new String[]{"Price low to high", "Price high to low", "Stars low to high", "Stars high to low", "None"};
        zohokartDAO = new ZohokartDAO(getActivity());
        productList = new ArrayList<>();
        bundle = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        productListFragment =  inflater.inflate(R.layout.fragment_product_list, container, false);
        recyclerView = (RecyclerView) productListFragment.findViewById(R.id.products);

        new ProductListingAsyncTask().execute(bundle);

        return productListFragment;
    }

    public void sortList(List<Product> products)
    {
        productListingAdapter.updateDataSet(products);
        recyclerView.scrollToPosition(0);
    }

    class ProductListingAsyncTask extends AsyncTask<Bundle, Void, List<Product>>
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            (productListFragment.findViewById(R.id.product_list_progress)).setVisibility(View.VISIBLE);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected List<Product> doInBackground(Bundle... params)
        {
            return (List<Product>) params[0].getSerializable("products");
        }

        @Override
        protected void onPostExecute(final List<Product> products)
        {
            super.onPostExecute(products);

            if (productList.size() > 0)
            {
                productList.clear();
                productList.addAll(products);
            }
            else
            {
                productList.addAll(products);
            }

            if (productList.size() > 0)
            {
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                productListingAdapter = new ProductListingAdapter(productList, getActivity(), communicator, productListFragment);
                recyclerView.setAdapter(productListingAdapter);

                (productListFragment.findViewById(R.id.filter_action)).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        communicator.openFilter(productList.get(0).getSubCategoryId());
                    }
                });

                (productListFragment.findViewById(R.id.sort_action)).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {


                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                        alertDialogBuilder.setTitle("Sort by");
                        alertDialogBuilder.setItems(sortingItems, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                if (which == 0)
                                {
                                    (productListFragment.findViewById(R.id.selected_sort)).setVisibility(View.VISIBLE);
                                    ((TextView) productListFragment.findViewById(R.id.selected_sort)).setText(sortingItems[which]);
                                    Collections.sort(productList, new PriceLowToHighComparator());
                                    sortList(productList);
                                } else if (which == 1)
                                {
                                    (productListFragment.findViewById(R.id.selected_sort)).setVisibility(View.VISIBLE);
                                    ((TextView) productListFragment.findViewById(R.id.selected_sort)).setText(sortingItems[which]);
                                    Collections.sort(productList, new PriceHighToLowComparator());
                                    sortList(productList);
                                } else if (which == 2)
                                {
                                    (productListFragment.findViewById(R.id.selected_sort)).setVisibility(View.VISIBLE);
                                    ((TextView) productListFragment.findViewById(R.id.selected_sort)).setText(sortingItems[which]);
                                    Collections.sort(productList, new StarsLowToHighComparator());
                                    sortList(productList);
                                } else if (which == 3)
                                {
                                    (productListFragment.findViewById(R.id.selected_sort)).setVisibility(View.VISIBLE);
                                    ((TextView) productListFragment.findViewById(R.id.selected_sort)).setText(sortingItems[which]);
                                    Collections.sort(productList, new StarsHighToLowComparator());
                                    sortList(productList);
                                } else if (which == 4)
                                {
                                    if ((productListFragment.findViewById(R.id.selected_sort)).getVisibility() == View.VISIBLE)
                                    {
                                        (productListFragment.findViewById(R.id.selected_sort)).setVisibility(View.GONE);
                                    }
                                    sortList(products);
                                }
                            }
                        });
                        alertDialogBuilder.show();
                    }
                });

                (productListFragment.findViewById(R.id.list_actions)).setVisibility(View.VISIBLE);
                (productListFragment.findViewById(R.id.products)).setVisibility(View.VISIBLE);
                (productListFragment.findViewById(R.id.product_list_progress)).setVisibility(View.GONE);
            }
            else
            {
                (productListFragment.findViewById(R.id.product_list_progress)).setVisibility(View.GONE);
                (productListFragment.findViewById(R.id.empty_list)).setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "no products.", Toast.LENGTH_SHORT).show();
                (productListFragment.findViewById(R.id.list_actions)).setVisibility(View.GONE);
            }
        }
    }
}
