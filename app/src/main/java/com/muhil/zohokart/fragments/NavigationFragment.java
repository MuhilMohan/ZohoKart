package com.muhil.zohokart.fragments;


import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
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
    ImageView subCategoryImage;
    View subCategoryMenuItem, view;
    CardView categoryMenuItem;
    LayoutInflater inflater;
    List<Category> categories;
    List<SubCategory> subCategories;
    Map<Integer, List<SubCategory>> subCategoriesByCategory;
    Communicator communicator;
    int index;

    ZohokartDAO zohokartDAO;

    public static int[] SUB_CATEGORY_IMAGES = {R.mipmap.mobile_xxhdpi, R.mipmap.tablets_xxhdpi, R.mipmap.laptops_xxhdpi,
            R.mipmap.furniture_xxhdpi_min, R.mipmap.lighting_xxhdpi, R.mipmap.kitchen_dining_xxhdpi_min,
            R.mipmap.health_carexxhdpi_min, R.mipmap.sports_fitness_xxhdpi, R.mipmap.luggage_travel_xxhdpi,
            R.mipmap.books_xxhdpi, R.mipmap.books_xxhdpi, R.mipmap.books_xxhdpi};

    public NavigationFragment()
    {
        // Required empty public constructor
    }

    // ***** getting activity for communication *****
    public void setCommunicator(Communicator communicator)
    {
        this.communicator = communicator;
    }

    public void restScroll()
    {
        ((ScrollView) view.findViewById(R.id.navigation_scroll)).fullScroll(View.FOCUS_UP);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        zohokartDAO = new ZohokartDAO(getActivity());
        inflater = LayoutInflater.from(getActivity());
        index = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_navigation, container, false);

        (view.findViewById(R.id.home_action_button)).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        communicator.showMainFragment();
                    }
                }
        );

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
                        subCategoryImage = (ImageView) subCategoryMenuItem.findViewById(R.id.sub_category_image);
                        subCategoryImage.setImageResource(SUB_CATEGORY_IMAGES[index]);
                        index++;
                        subCategoryMenuItem.setTag(subCategory);

                        subCategoryMenuItem.setId(subCategory.getId());

                        subCategoryMenuItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SubCategory subCategory = (SubCategory) v.getTag();
                                communicator.sendProductList(subCategory.getId());
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

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    // ***** interface to communicate with activity *****
    public interface Communicator
    {
        void closeDrawer();
        void sendProductList(int subCategoryId);
        void showMainFragment();
    }

}
