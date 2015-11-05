package com.muhil.zohokart.fragments;


import android.content.Context;
import android.content.SharedPreferences;
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
import com.muhil.zohokart.models.Book;
import com.muhil.zohokart.models.FilterPair;
import com.muhil.zohokart.models.Furniture;
import com.muhil.zohokart.models.HealthCare;
import com.muhil.zohokart.models.Kitchen;
import com.muhil.zohokart.models.Laptop;
import com.muhil.zohokart.models.Lighting;
import com.muhil.zohokart.models.Luggage;
import com.muhil.zohokart.models.Mobile;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.models.Sport;
import com.muhil.zohokart.models.Tablet;
import com.muhil.zohokart.utils.ZohoKartSharePreferences;
import com.muhil.zohokart.utils.ZohokartDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    SharedPreferences filterPref;
    SharedPreferences.Editor filterPrefEditor;
    Set<String> selectedFilterItems;

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
        filterPref = getActivity().getSharedPreferences(ZohoKartSharePreferences.SELECTED_FILTERS, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        filterFragment = inflater.inflate(R.layout.fragment_filter, container, false);
        communicator.lockDrawer();

        selectedFilterItems = filterPref.getStringSet(ZohoKartSharePreferences.SELECTED_FILTER_ITEMS, null);
        if (selectedFilterItems == null)
        {
            selectedFilterItems = new HashSet<>();
        }
        else if (selectedFilterItems.size() > 0)
        {
            (filterFragment.findViewById(R.id.filter_button)).setVisibility(View.VISIBLE);
        }

        filterPrefEditor = filterPref.edit();

        new FilterPopulateAsyncTask().execute(bundle.getInt("sub_category_id"));

        (filterFragment.findViewById(R.id.filter_button)).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        filteredProducts = zohokartDAO.getFilteredProducts(filterPairs, bundle.getInt("sub_category_id"));
                        communicator.sendFilteredProducts(filteredProducts);
                        filterPrefEditor.putStringSet(ZohoKartSharePreferences.SELECTED_FILTER_ITEMS, selectedFilterItems);
                        filterPrefEditor.apply();
                    }
                }
        );

        return filterFragment;
    }

    class FilterPopulateAsyncTask extends AsyncTask<Integer, Void, Void>
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Integer... subCategoryId)
        {

            switch (subCategoryId[0])
            {
                case 100:
                    tempFilterOptions = zohokartDAO.getBrandsForFilter(subCategoryId[0]);
                    filterOptions.putAll(tempFilterOptions);
                    filterOptions.putAll(Mobile.FILTER_OPTIONS);
                    break;
                case 101:
                    tempFilterOptions = zohokartDAO.getBrandsForFilter(subCategoryId[0]);
                    filterOptions.putAll(tempFilterOptions);
                    filterOptions.putAll(Tablet.FILTER_OPTIONS);
                    break;
                case 102:
                    tempFilterOptions = zohokartDAO.getBrandsForFilter(subCategoryId[0]);
                    filterOptions.putAll(tempFilterOptions);
                    filterOptions.putAll(Laptop.FILTER_OPTIONS);
                    break;
                case 200:
                    tempFilterOptions = zohokartDAO.getBrandsForFilter(subCategoryId[0]);
                    filterOptions.putAll(tempFilterOptions);
                    filterOptions.putAll(Furniture.FILTER_OPTIONS);
                case 201:
                    tempFilterOptions = zohokartDAO.getBrandsForFilter(subCategoryId[0]);
                    filterOptions.putAll(tempFilterOptions);
                    filterOptions.putAll(Lighting.FILTER_OPTIONS);
                    break;
                case 202:
                    tempFilterOptions = zohokartDAO.getBrandsForFilter(subCategoryId[0]);
                    filterOptions.putAll(tempFilterOptions);
                    filterOptions.putAll(Kitchen.FILTER_OPTIONS);
                    break;
                case 400:
                    tempFilterOptions = zohokartDAO.getBrandsForFilter(subCategoryId[0]);
                    filterOptions.putAll(tempFilterOptions);
                    filterOptions.putAll(HealthCare.FILTER_OPTIONS);
                    break;
                case 401:
                    tempFilterOptions = zohokartDAO.getBrandsForFilter(subCategoryId[0]);
                    filterOptions.putAll(tempFilterOptions);
                    filterOptions.putAll(Sport.FILTER_OPTIONS);
                    break;
                case 402:
                    tempFilterOptions = zohokartDAO.getBrandsForFilter(subCategoryId[0]);
                    filterOptions.putAll(tempFilterOptions);
                    filterOptions.putAll(Luggage.FILTER_OPTIONS);
                    break;
                case 500:
                    tempFilterOptions = zohokartDAO.getBrandsForFilter(subCategoryId[0]);
                    filterOptions.putAll(tempFilterOptions);
                    filterOptions.putAll(Book.FILTER_OPTIONS);
                    break;
                case 501:
                    tempFilterOptions = zohokartDAO.getBrandsForFilter(subCategoryId[0]);
                    filterOptions.putAll(tempFilterOptions);
                    filterOptions.putAll(Book.FILTER_OPTIONS);
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

                    if (selectedFilterItems.contains(filterOptionItem.getKey()))
                    {
                        ((CheckBox) filterItemView.findViewById(R.id.filter_item_checker)).setChecked(true);
                        filterPairs.add(filterOptionItem.getValue());
                    }

                    (filterItemView.findViewById(R.id.filter_select_action)).setOnClickListener(
                            new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    CheckBox checkBox = ((CheckBox) v.findViewById(R.id.filter_item_checker));
                                    TextView filterItemName = ((TextView) v.findViewById(R.id.filter_item_name));
                                    FilterPair filterPair = (FilterPair) checkBox.getTag();

                                    if (!checkBox.isChecked())
                                    {
                                        if (!filterPairs.contains(filterPair))
                                        {
                                            selectedFilterItems.add(filterItemName.getText().toString());
                                            filterPairs.add(filterPair);
                                            checkBox.setChecked(true);
                                        }
                                        if (filterPairs.size() > 0)
                                        {
                                            (filterFragment.findViewById(R.id.filter_button)).setVisibility(View.VISIBLE);
                                        }
                                    }
                                    else
                                    {
                                        if (filterPairs.contains(filterPair))
                                        {
                                            selectedFilterItems.remove(filterItemName.getText().toString());
                                            filterPairs.remove(filterPair);
                                            checkBox.setChecked(false);
                                        }
                                        if (filterPairs.size() <= 0)
                                        {
                                            (filterFragment.findViewById(R.id.filter_button)).setVisibility(View.GONE);
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
        void lockDrawer();
    }

}
