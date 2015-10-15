package com.muhil.zohokart;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import android.widget.ImageView;

import com.muhil.zohokart.activities.LoginActivity;
import com.muhil.zohokart.fragments.CartFragment;
import com.muhil.zohokart.fragments.MainFragment;
import com.muhil.zohokart.fragments.NavigationFragment;
import com.muhil.zohokart.fragments.ProductListFragment;
import com.muhil.zohokart.fragments.SearchFragment;
import com.muhil.zohokart.fragments.WishlistFragment;
import com.muhil.zohokart.models.Account;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.utils.DataImporter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationFragment.Communicator {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    DataImporter dataImporter;
    SharedPreferences sharedPreferences;
    NavigationFragment navigationFragment;

    SearchView searchView;

    android.support.v4.app.FragmentManager fragmentManager;
    android.support.v4.app.FragmentTransaction fragmentTransaction;

    public static final int ACTION_ACCOUNT_NAME = 1000;

    String preferenceName = "logged_account";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(preferenceName, MODE_PRIVATE);

        fragmentManager = getSupportFragmentManager();

        dataImporter = new DataImporter(this);
        dataImporter.importData();

        navigationFragment = (NavigationFragment) getFragmentManager().findFragmentById(R.id.fragment);
        navigationFragment.setCommunicator(this);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (getSupportActionBar() != null){

            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_menu_white_24dp);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        MainFragment mainFragment = new MainFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_holder, mainFragment, "main_fragment");
        fragmentTransaction.addToBackStack("main_fragment");
        fragmentTransaction.commit();



    }

    private void processSearch(String query) {

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

        MenuItem mSearchMenuItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) mSearchMenuItem.getActionView();
        int searchImgId = android.support.v7.appcompat.R.id.search_button;
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageResource(R.mipmap.ic_add_shopping_cart_black_18dp);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                processSearch(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        MenuItem menuItem = null;
        Account account = new Account();
        String jsonString = sharedPreferences.getString("logged_account", "");
        if (jsonString!=null && !jsonString.equals("")){

            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                account.setName(jsonObject.getString("name"));

            }
            catch (JSONException e){
                e.printStackTrace();
            }

            if ((menuItem = menu.findItem(ACTION_ACCOUNT_NAME)) == null){

                menu.findItem(R.id.action_login).setVisible(false);
                menu.add(0, ACTION_ACCOUNT_NAME, 200, account.getName());

            }

        }
        else {

            if ((menuItem = menu.findItem(ACTION_ACCOUNT_NAME)) != null){

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
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START);
        }
        else if (id == R.id.action_login){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.wish_list){
            WishlistFragment wishlistFragment = new WishlistFragment();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_holder, wishlistFragment, "wish_list");
            fragmentTransaction.addToBackStack("wishlist_fragment");
            fragmentTransaction.commit();
        }
        else if (id == R.id.cart_icon){
            CartFragment cartFragment = new CartFragment();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_holder, cartFragment, "cart");
            fragmentTransaction.addToBackStack("cart_fragment");
            fragmentTransaction.commit();
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onBackPressed() {


        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }

    }

    @Override
    public void closeDrawer() {

        drawerLayout.closeDrawers();

    }

    @Override
    public void sendProductList(int subCategoryId) {

        Log.d("TRANSACTION", "enetered transaction bay");
        ProductListFragment productListFragment = ProductListFragment.getInstance(subCategoryId);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_holder, productListFragment, "product_list");
        fragmentTransaction.addToBackStack("product_list_fragment");
        fragmentTransaction.commit();
        Log.d("TRANSACTION", "commit done.");

    }

}
