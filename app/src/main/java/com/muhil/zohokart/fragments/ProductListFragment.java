package com.muhil.zohokart.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.muhil.zohokart.decorators.DividerItemDecoration;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.utils.DBHelper;
import com.muhil.zohokart.utils.ZohokartDAO;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductListFragment extends Fragment {

    Gson gson;
    List<Product> productList;
    RecyclerView recyclerView;
    ProductListingAdapter productListingAdapter;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        gson = new Gson();
        View fragmentLayout =  inflater.inflate(R.layout.fragment_product_list, container, false);
        Bundle bundle = getArguments();
        productList = gson.fromJson(bundle.getString("products"), new TypeToken<List<Product>>() {}.getType());
        recyclerView = (RecyclerView) fragmentLayout.findViewById(R.id.products);

        if (productList.size() > 0){

            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            RecyclerView.ItemDecoration divider = new DividerItemDecoration(getActivity(), LinearLayout.VERTICAL);
            recyclerView.addItemDecoration(divider);
            productListingAdapter = new ProductListingAdapter(productList, getActivity());
            recyclerView.setAdapter(productListingAdapter);

        }
        else {
            Toast.makeText(getActivity(), "no products.", Toast.LENGTH_SHORT).show();
        }

        return fragmentLayout;
    }

}
