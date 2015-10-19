package com.muhil.zohokart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muhil.zohokart.activities.CartActivity;
import com.muhil.zohokart.activities.LoginActivity;
import com.muhil.zohokart.activities.WishlistActivity;
import com.muhil.zohokart.fragments.FilterFragment;
import com.muhil.zohokart.fragments.MainFragment;
import com.muhil.zohokart.fragments.NavigationFragment;
import com.muhil.zohokart.fragments.ProductListFragment;
import com.muhil.zohokart.fragments.SearchFragment;
import com.muhil.zohokart.models.Account;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.utils.DataImporter;
import com.muhil.zohokart.utils.ZohokartDAO;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationFragment.Communicator, FilterFragment.FilterCommunicator, ProductListFragment.ProductListCommunicator
{

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    DataImporter dataImporter;
    SharedPreferences sharedPreferences;
    NavigationFragment navigationFragment;
    SearchFragment searchFragment;

    ZohokartDAO zohokartDAO;

    Gson gson;

    SearchView searchView;

    android.support.v4.app.FragmentManager fragmentManager;
    android.support.v4.app.FragmentTransaction fragmentTransaction;
    android.support.v4.app.Fragment fragment;
    public static final int ACTION_ACCOUNT_NAME = 1000;

    int backStackCount;
    FragmentManager.BackStackEntry backStackEntry;

    String preferenceName = "logged_account";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // *** fragmentManager for transaction ***
        fragmentManager = getSupportFragmentManager();

        gson = new Gson();
        zohokartDAO = new ZohokartDAO(this);

        // *** getting preferences for logged account ***
        sharedPreferences = getSharedPreferences(preferenceName, MODE_PRIVATE);

        // ** getting data into app ***
        dataImporter = new DataImporter(this);
        dataImporter.importData();

        // *** setting nav drawer ***
        navigationFragment = (NavigationFragment) getFragmentManager().findFragmentById(R.id.fragment);
        navigationFragment.setCommunicator(this);

        // *** setting toolbar and menu icon ***
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_menu_white_24dp);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // *** initializing drawer ***
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // *** including the default main fragment ***
        MainFragment mainFragment = new MainFragment();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_holder, mainFragment, "main_fragment");
        fragmentTransaction.commit();
    }

    private void processSearch(String query) {

        // *** processing results using the search query ***
        searchFragment = (SearchFragment) fragmentManager.findFragmentByTag("search_fragment");
        if (searchFragment != null)
        {
            Toast.makeText(MainActivity.this, "query sent", Toast.LENGTH_SHORT).show();
            searchFragment.processQuery(query);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // *** getting searchview for handling search queries and changing image ***

        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
        int searchImgId = android.support.v7.appcompat.R.id.search_button; // I used the explicit layout ID of searchview's ImageView
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageResource(R.mipmap.ic_youtube_searched_for_white_24dp);

        searchView.setOnSearchClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SearchFragment searchFragment = new SearchFragment();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_holder, searchFragment, "search_fragment");
                fragmentTransaction.addToBackStack("search_fragment");
                fragmentTransaction.commit();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                searchView.clearFocus();
                processSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener()
        {
            @Override
            public boolean onClose()
            {
                fragmentManager.popBackStack();
                return false;
            }
        });

        // *** searchView ends ***

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {

        // *** checking logged account in preferences ***
        Account account;
        String jsonString = sharedPreferences.getString("logged_account", "");
        if (!jsonString.equals(""))
        {
            account = gson.fromJson(jsonString, new TypeToken<Account>(){}.getType());
            if (( menu.findItem(ACTION_ACCOUNT_NAME)) == null)
            {
                menu.findItem(R.id.action_login).setVisible(false);
                menu.add(0, ACTION_ACCOUNT_NAME, 200, account.getName());
            }
        }
        else
        {
            if (( menu.findItem(ACTION_ACCOUNT_NAME)) != null)
            {
                menu.removeItem(ACTION_ACCOUNT_NAME);
                menu.findItem(R.id.action_login).setVisible(true);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings)
        {
            return true;
        }
        else if (id == android.R.id.home)
        {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        else if (id == R.id.action_login)
        {
            startActivity(new Intent(this, LoginActivity.class));
        }
        else if (id == R.id.wish_list)
        {
            startActivity(new Intent(this, WishlistActivity.class));
        }
        else if (id == R.id.cart_icon)
        {
            startActivity(new Intent(this, CartActivity.class));
        }
        else if (id == R.id.menu_search)
        {
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onBackPressed()
    {

        if (searchFragment!= null && searchFragment.isVisible())
        {
            fragmentManager.popBackStack();
            searchView.onActionViewCollapsed();
            searchView.setIconified(true);
        }
        else if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public void closeDrawer()
    {
        drawerLayout.closeDrawers();
    }

    @Override
    public void sendProductList(int subCategoryId)
    {
        Log.d("TRANSACTION", "enetered transaction bay");
        List<Product> products = zohokartDAO.getProductsForSubCategory(subCategoryId);
        ProductListFragment productListFragment = ProductListFragment.getInstance(products);
        productListFragment.setCommunicator(this);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_holder, productListFragment, "product_list");
        fragmentTransaction.addToBackStack("product_list");
        fragmentTransaction.commit();
        Log.d("TRANSACTION", "commit done.");
    }

    @Override
    public void sendFilteredProducts(List<Product> products)
    {
        ProductListFragment productListFragment = (ProductListFragment) fragmentManager.findFragmentByTag("product_list");
        if (productListFragment != null)
        {
            fragmentManager.popBackStack();
            productListFragment = ProductListFragment.getInstance(products);
            productListFragment.setCommunicator(this);
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_holder, productListFragment, "product_list");
            fragmentTransaction.addToBackStack("product_list");
            fragmentTransaction.commit();
            Log.d("FILTERED", "filtered products sent");
        }
    }

    @Override
    public void openFilter(int subCategoryId)
    {
        FilterFragment filterFragment = FilterFragment.getInstance(subCategoryId);
        filterFragment.setCommunicator(this);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_holder, filterFragment, "filter_fragment");
        fragmentTransaction.addToBackStack("filter_fragment");
        fragmentTransaction.commit();
    }
}
