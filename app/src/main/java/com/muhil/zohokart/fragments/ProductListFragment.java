package com.muhil.zohokart.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muhil.zohokart.MainActivity;
import com.muhil.zohokart.R;
import com.muhil.zohokart.adapters.ProductListingAdapter;
import com.muhil.zohokart.comparators.PriceHighToLowComparator;
import com.muhil.zohokart.comparators.PriceLowToHighComparator;
import com.muhil.zohokart.comparators.StarsHighToLowComparator;
import com.muhil.zohokart.comparators.StarsLowToHighComparator;
import com.muhil.zohokart.decorators.DividerItemDecoration;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.utils.DBHelper;
import com.muhil.zohokart.utils.ZohokartDAO;

import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductListFragment extends android.support.v4.app.Fragment {

    Gson gson;
    List<Product> productList;
    RecyclerView recyclerView;
    ProductListingAdapter productListingAdapter;
    String[] sortingItems;

    public ProductListFragment() {
        // Required empty public constructor
    }

    public static ProductListFragment getInstance(String products){

        ProductListFragment productListFragment = new ProductListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("products", products);
        productListFragment.setArguments(bundle);
        return productListFragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sortingItems = new String[]{"Price low to high", "Price high to low", "Stars low to high", "Stars high to low", "None"};

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        gson = new Gson();
        final View fragmentLayout =  inflater.inflate(R.layout.fragment_product_list, container, false);
        final Bundle bundle = getArguments();
        productList = gson.fromJson(bundle.getString("products"), new TypeToken<List<Product>>() {}.getType());
        recyclerView = (RecyclerView) fragmentLayout.findViewById(R.id.products);

        if (productList.size() > 0)
        {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            RecyclerView.ItemDecoration divider = new DividerItemDecoration(getActivity(), LinearLayout.VERTICAL);
            recyclerView.addItemDecoration(divider);
            productListingAdapter = new ProductListingAdapter(productList, getActivity(), getActivity().getSupportFragmentManager(), fragmentLayout);
            recyclerView.setAdapter(productListingAdapter);
        }
        else {
            Toast.makeText(getActivity(), "no products.", Toast.LENGTH_SHORT).show();
        }

        (fragmentLayout.findViewById(R.id.sort_action)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Sort by");
                alertDialogBuilder.setItems(sortingItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            (fragmentLayout.findViewById(R.id.selected_sort)).setVisibility(View.VISIBLE);
                            ((TextView) fragmentLayout.findViewById(R.id.selected_sort)).setText(sortingItems[which]);
                            Collections.sort(productList, new PriceLowToHighComparator());
                            sortList();
                        }
                        else if (which == 1)
                        {
                            (fragmentLayout.findViewById(R.id.selected_sort)).setVisibility(View.VISIBLE);
                            ((TextView) fragmentLayout.findViewById(R.id.selected_sort)).setText(sortingItems[which]);
                            Collections.sort(productList, new PriceHighToLowComparator());
                            sortList();
                        }
                        else if (which == 2)
                        {
                            (fragmentLayout.findViewById(R.id.selected_sort)).setVisibility(View.VISIBLE);
                            ((TextView) fragmentLayout.findViewById(R.id.selected_sort)).setText(sortingItems[which]);
                            Collections.sort(productList, new StarsLowToHighComparator());
                            sortList();
                        }
                        else if (which == 3)
                        {
                            (fragmentLayout.findViewById(R.id.selected_sort)).setVisibility(View.VISIBLE);
                            ((TextView) fragmentLayout.findViewById(R.id.selected_sort)).setText(sortingItems[which]);
                            Collections.sort(productList, new StarsHighToLowComparator());
                            sortList();
                        } else if (which == 4)
                        {
                            if ((fragmentLayout.findViewById(R.id.selected_sort)).getVisibility() == View.VISIBLE)
                            {
                                (fragmentLayout.findViewById(R.id.selected_sort)).setVisibility(View.GONE);
                            }
                            productList = gson.fromJson(bundle.getString("products"), new TypeToken<List<Product>>() {
                            }.getType());
                            sortList();
                        }
                    }
                });
                alertDialogBuilder.show();
            }
        });

        return fragmentLayout;
    }

    public void sortList()
    {
        productListingAdapter.updateDataSet(productList);
        recyclerView.scrollToPosition(0);
    }

}
