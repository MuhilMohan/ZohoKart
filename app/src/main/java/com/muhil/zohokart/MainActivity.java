package com.muhil.zohokart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.muhil.zohokart.activities.CartActivity;
import com.muhil.zohokart.activities.LoginActivity;
import com.muhil.zohokart.activities.WishlistActivity;
import com.muhil.zohokart.fragments.MainFragment;
import com.muhil.zohokart.fragments.NavigationFragment;
import com.muhil.zohokart.fragments.ProductListFragment;
import com.muhil.zohokart.fragments.SearchFragment;
import com.muhil.zohokart.models.Account;
import com.muhil.zohokart.utils.DataImporter;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements NavigationFragment.Communicator {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    DataImporter dataImporter;
    SharedPreferences sharedPreferences;
    NavigationFragment navigationFragment;

    SearchView searchView;

    android.support.v4.app.FragmentManager fragmentManager;
    android.support.v4.app.FragmentTransaction fragmentTransaction;
    android.support.v4.app.Fragment fragment;
    public static final int ACTION_ACCOUNT_NAME = 1000;

    String preferenceName = "logged_account";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // *** getting SP for logged account ***
        sharedPreferences = getSharedPreferences(preferenceName, MODE_PRIVATE);

        // *** fragmentManager for transaction ***
        fragmentManager = getSupportFragmentManager();

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
        if (fragmentTransaction != null)
        {
            fragmentTransaction.add(R.id.fragment_holder, mainFragment, "main_fragment");
            fragmentTransaction.commit();
        }
    }

    private void processSearch(String query) {

        // *** processing results using the search query ***
        SearchFragment searchFragment = SearchFragment.getInstance(query);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_holder, searchFragment, "search_fragment");
        fragmentTransaction.addToBackStack("search_fragment");
        fragmentTransaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // *** getting searchview for handling search queries ***
        MenuItem mSearchMenuItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) mSearchMenuItem.getActionView();
        
        searchView.setOnSearchClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(MainActivity.this, "search clicked.", Toast.LENGTH_SHORT).show();
            }
        });
        
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
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
                fragment = fragmentManager.findFragmentByTag("search_fragment");
                if (fragment != null) fragmentManager.popBackStack();
                return false;
            }
        });

        Account account = new Account();
        String jsonString = sharedPreferences.getString("logged_account", "");
        if (jsonString!=null && !jsonString.equals(""))
        {
            try
            {
                JSONObject jsonObject = new JSONObject(jsonString);
                account.setName(jsonObject.getString("name"));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
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
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
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
        ProductListFragment productListFragment = ProductListFragment.getInstance(subCategoryId);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_holder, productListFragment, "product_list");
        fragmentTransaction.addToBackStack("product_list_fragment");
        fragmentTransaction.commit();
        Log.d("TRANSACTION", "commit done.");
    }

}
