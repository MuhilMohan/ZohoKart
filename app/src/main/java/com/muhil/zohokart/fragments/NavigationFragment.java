package com.muhil.zohokart.fragments;


import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.muhil.zohokart.R;
import com.muhil.zohokart.models.Category;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.models.SubCategory;
import com.muhil.zohokart.utils.ZohokartDAO;

import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

public class NavigationFragment extends Fragment
{
    LinearLayout menuLinearLayout;
    TextView subCategoryName;
    View subCategoryMenuItem, view;
    CardView categoryMenuItem;
    LayoutInflater inflater;
    List<Category> categories;
    List<SubCategory> subCategories;
    Map<Integer, List<SubCategory>> subCategoriesByCategory;
    Communicator communicator;

    ZohokartDAO zohokartDAO;

    public NavigationFragment()
    {
        // Required empty public constructor
    }

    // ***** getting activity for communication *****
    public void setCommunicator(Communicator communicator)
    {
        this.communicator = communicator;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        zohokartDAO = new ZohokartDAO(getActivity());
        inflater = LayoutInflater.from(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_navigation, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // ***** starting asynctask for filling the navigation drawer *****
        new NavigationAsyncTask().execute();
    }

    // ***** asynctask for filling the navigation drawer *****
    class NavigationAsyncTask extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            (view.findViewById(R.id.navigation_progress)).setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            categories = zohokartDAO.getCategories();
            Log.d("NAV", "number of categories from db = " + categories.size());
            subCategoriesByCategory = zohokartDAO.getSubCategoriesByCategory();
            Log.d("NAV", "number of sub-categories from db = " + subCategoriesByCategory.size());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            menuLinearLayout = (LinearLayout) view.findViewById(R.id.navigation_linear_layout);
            for (Category category : categories)
            {
                categoryMenuItem = (CardView) inflater.inflate(R.layout.navigation_menu_row, menuLinearLayout, false);
                ((TextView) categoryMenuItem.findViewById(R.id.category_name)).setText(category.getName());

                if ((subCategories = subCategoriesByCategory.get(category.getId())) != null)
                {
                    for (SubCategory subCategory : subCategories)
                    {
                        subCategoryMenuItem = inflater.inflate(R.layout.navigation_menu_item_row, categoryMenuItem, false);
                        subCategoryName = (TextView) subCategoryMenuItem.findViewById(R.id.subCategoryItem);
                        subCategoryName.setText(subCategory.getName());
                        subCategoryMenuItem.setTag(subCategory);

                        subCategoryMenuItem.setId(subCategory.getId());

                        subCategoryMenuItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SubCategory subCategory = (SubCategory) v.getTag();
                                communicator.sendProductList(subCategory.getId());
                                Toast.makeText(getActivity(), "Sub-category id: " + subCategory.getId(), Toast.LENGTH_SHORT).show();
                                communicator.closeDrawer();
                            }
                        });

                        ((LinearLayout) categoryMenuItem.findViewById(R.id.sub_category_holder)).addView(subCategoryMenuItem);
                    }

                    menuLinearLayout.addView(categoryMenuItem);

                }
            }

            (view.findViewById(R.id.navigation_scroll)).setVisibility(View.VISIBLE);
            (view.findViewById(R.id.navigation_progress)).setVisibility(View.GONE);

        }
    }

    // ***** interface to communicate with activity *****
    public interface Communicator
    {
        void closeDrawer();
        void sendProductList(int subCategoryId);
    }

}
