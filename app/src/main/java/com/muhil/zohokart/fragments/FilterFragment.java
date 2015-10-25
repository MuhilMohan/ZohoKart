package com.muhil.zohokart.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.muhil.zohokart.R;
import com.muhil.zohokart.models.FilterPair;
import com.muhil.zohokart.models.Mobile;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.utils.ZohokartDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilterFragment extends android.support.v4.app.Fragment
{

    View filterFragment, filterItemView;
    List<FilterPair> filterPairs;
    List<Product> filteredProducts;
    Map<String, Map<String, FilterPair>> filterOptions, tempFilterOptions;
    Map<String, FilterPair> filterItems;
    CardView filterGroup;
    LayoutInflater layoutInflater;
    Bundle bundle;
    ZohokartDAO zohokartDAO;
    FilterCommunicator communicator;
    AppCompatActivity mainActivity;

    public FilterFragment()
    {
        // Required empty public constructor
    }

    public static FilterFragment getInstance(int subCategoryId)
    {
        FilterFragment filterFragment = new FilterFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("sub_category_id", subCategoryId);
        filterFragment.setArguments(bundle);
        return filterFragment;
    }

    public void setCommunicator(FilterCommunicator communicator)
    {
        this.communicator = communicator;
    }

    public void setActivity(AppCompatActivity activity)
    {
        this.mainActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        filterOptions = new HashMap<>();
        layoutInflater = LayoutInflater.from(getActivity());
        bundle = getArguments();
        zohokartDAO = new ZohokartDAO(getActivity());
        filterPairs = new ArrayList<>();
        filteredProducts = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        filterFragment = inflater.inflate(R.layout.fragment_filter, container, false);

        new FilterPopoulateAsyncTask().execute(bundle.getInt("sub_category_id"));

        (filterFragment.findViewById(R.id.filter_button)).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        filteredProducts = zohokartDAO.getFilteredProducts(filterPairs, bundle.getInt("sub_category_id"));
                        Toast.makeText(getActivity(), String.valueOf(filteredProducts.size()), Toast.LENGTH_SHORT).show();
                        communicator.sendFilteredProducts(filteredProducts);
                    }
                }
        );

        return filterFragment;
    }

    class FilterPopoulateAsyncTask extends AsyncTask<Integer, Void, Void>
    {

        @Override
        protected Void doInBackground(Integer... params)
        {

            switch (params[0])
            {
                case 100:
                    tempFilterOptions = zohokartDAO.getBrandsForFilter(params[0]);
                    filterOptions.putAll(tempFilterOptions);
                    filterOptions.putAll(Mobile.FILTER_OPTIONS);
                    Log.d("FILTER_OPTIONS", String.valueOf(filterOptions.size()));
                    break;
                case 101:
                    tempFilterOptions = zohokartDAO.getBrandsForFilter(params[0]);
                    filterOptions.putAll(tempFilterOptions);
                    break;
                default:
                    break;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);

            for (final Map.Entry<String, Map<String, FilterPair>> filterOptionGroup : filterOptions.entrySet())
            {

                filterGroup = (CardView) layoutInflater.inflate(R.layout.filter_group_item, ((LinearLayout) filterFragment.findViewById(R.id.filter_item_holder)), false);
                ((TextView) filterGroup.findViewById(R.id.filter_group_header)).setText(filterOptionGroup.getKey());

                filterItems = filterOptionGroup.getValue();
                Log.d("FILTER", "inside filter item " + filterItems.size());
                for (Map.Entry<String, FilterPair> filterOptionItem : filterItems.entrySet())
                {
                    filterItemView = layoutInflater.inflate(R.layout.filter_item, filterGroup, false);
                    ((TextView) filterItemView.findViewById(R.id.filter_item_name)).setText(filterOptionItem.getKey());
                    (filterItemView.findViewById(R.id.filter_item_checker)).setTag(filterOptionItem.getValue());

                    (filterItemView.findViewById(R.id.filter_select_action)).setOnClickListener(
                            new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    CheckBox checkBox = ((CheckBox) v.findViewById(R.id.filter_item_checker));
                                    FilterPair filterPair = (FilterPair) checkBox.getTag();
                                    if (!checkBox.isChecked())
                                    {
                                        Toast.makeText(getActivity(), filterPair.getSelectionString(), Toast.LENGTH_SHORT).show();
                                        if (!filterPairs.contains(filterPair))
                                        {
                                            filterPairs.add(filterPair);
                                            checkBox.setChecked(true);
                                        }
                                    }
                                    else
                                    {
                                        if (filterPairs.contains(filterPair))
                                        {
                                            filterPairs.remove(filterPair);
                                            checkBox.setChecked(false);
                                            Toast.makeText(getActivity(), filterPair.getSelectionString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                    );

                    ((LinearLayout) filterGroup.findViewById(R.id.filter_options_holder)).addView(filterItemView);
                }

                ((LinearLayout) filterFragment.findViewById(R.id.filter_item_holder)).addView(filterGroup);

            }

        }
    }

    public interface FilterCommunicator
    {
        void sendFilteredProducts(List<Product> products);
    }

}
