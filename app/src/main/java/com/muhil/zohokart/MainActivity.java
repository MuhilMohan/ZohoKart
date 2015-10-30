package com.muhil.zohokart;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;
import android.widget.Toast;

import com.muhil.zohokart.activities.CheckoutActivity;
import com.muhil.zohokart.activities.LoginActivity;
import com.muhil.zohokart.activities.ProfileActivity;
import com.muhil.zohokart.activities.RegistrationActivity;
import com.muhil.zohokart.fragments.BannerFragment;
import com.muhil.zohokart.fragments.CartFragment;
import com.muhil.zohokart.fragments.FilterFragment;
import com.muhil.zohokart.fragments.MainFragment;
import com.muhil.zohokart.fragments.NavigationFragment;
import com.muhil.zohokart.fragments.ProductDetailFragment;
import com.muhil.zohokart.fragments.ProductDetailPagerFragment;
import com.muhil.zohokart.fragments.ProductListFragment;
import com.muhil.zohokart.fragments.SpecificationFragment;
import com.muhil.zohokart.fragments.WishlistFragment;
import com.muhil.zohokart.interfaces.ProductListCommunicator;
import com.muhil.zohokart.models.Account;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.models.PromotionBanner;
import com.muhil.zohokart.models.Wishlist;
import com.muhil.zohokart.utils.DataImporter;
import com.muhil.zohokart.utils.ZohoKartSharePreferences;
import com.muhil.zohokart.utils.ZohokartDAO;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationFragment.Communicator, FilterFragment.FilterCommunicator,
        ProductListCommunicator, WishlistFragment.WishlistCommunicator, CartFragment.CartCommunicator,
        ProductDetailFragment.ProductDetailCommunicator, ProductDetailPagerFragment.ProductDetailPageCommunicator,
        BannerFragment.BannerCommunicator, MainFragment.MainCommunicator, FragmentManager.OnBackStackChangedListener
{

    public static final int REQUEST_CODE_LOGIN = 101;
    public static final int REQUEST_CODE_LOGOUT = 102;

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    DataImporter dataImporter;
    NavigationFragment navigationFragment;

    List<Product> products, productsToShow;
    List<PromotionBanner> promotionBanners;
    ZohokartDAO zohokartDAO;

    int subCategoryId;

    SearchView searchView;

    Fragment fragment;
    CartFragment cartFragment;
    MainFragment mainFragment;
    ProductListFragment productListFragment;
    WishlistFragment wishlistFragment;
    ProductDetailFragment productDetailFragment;
    android.support.v4.app.FragmentManager fragmentManager;
    android.support.v4.app.FragmentTransaction fragmentTransaction;
    public static final int ACTION_ACCOUNT_NAME = 1000;

    int backStackCount;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // *** setting toolbar and menu icon ***
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_menu_white_24dp);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
            (toolbar.findViewById(R.id.app_icon)).setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            showMainFragment();
                        }
                    }
            );
        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        new DataImportingTask().execute();

        // *** fragmentManager for transaction ***
        fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);
        zohokartDAO = new ZohokartDAO(this);

        // *** getting preferences for logged account ***
        sharedPreferences = getSharedPreferences(ZohoKartSharePreferences.LOGGED_ACCOUNT, MODE_PRIVATE);

        // *** initializing drawer ***
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerListener(
                new DrawerLayout.DrawerListener()
                {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset)
                    {
                    }
                    @Override
                    public void onDrawerOpened(View drawerView) {}

                    @Override
                    public void onDrawerClosed(View drawerView)
                    {
                        if (productsToShow != null)
                        {
                            productListFragment = (ProductListFragment) fragmentManager.findFragmentByTag("product_list");
                            if (productListFragment == null )
                            {
                                openProductList(productsToShow);
                            }
                            else
                            {
                                fragmentManager.popBackStack();
                                openProductList(productsToShow);
                            }
                        }
                        productsToShow = null;
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {}
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // *** getting searchview for handling search queries and changing image ***

        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
        int searchImgId = android.support.v7.appcompat.R.id.search_button;
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageResource(R.mipmap.ic_search_white_24dp);

        searchView.setOnSearchClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                (toolbar.findViewById(R.id.app_icon)).setVisibility(View.GONE);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                List<Product> products = zohokartDAO.getProductsBySearchString(query);
                fragment = fragmentManager.findFragmentByTag("product_list");
                if (fragment != null && fragment.isVisible())
                {
                    fragmentManager.popBackStack();
                    openProductList(products);
                }
                else
                {
                    openProductList(products);
                }
                searchView.clearFocus();
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
                fragment = fragmentManager.findFragmentByTag("product_list");
                if (fragment != null && fragment.isVisible())
                {
                    fragmentManager.popBackStack();
                }
                (toolbar.findViewById(R.id.app_icon)).setVisibility(View.VISIBLE);
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
        String accountName = sharedPreferences.getString(Account.NAME, "");
        if (!accountName.equals(""))
        {
            if (( menu.findItem(ACTION_ACCOUNT_NAME)) == null)
            {
                menu.findItem(R.id.action_login).setVisible(false);
                menu.add(0, ACTION_ACCOUNT_NAME, 200, accountName);
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
            backStackCount = fragmentManager.getBackStackEntryCount();
            if (backStackCount > 0)
            {
                fragmentManager.popBackStack();
            }
            else
            {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        }
        else if (id == R.id.action_login)
        {
            openLoginPage();
        }
        else if (id == ACTION_ACCOUNT_NAME)
        {
            startActivityForResult(new Intent(this, ProfileActivity.class), REQUEST_CODE_LOGOUT);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        }
        else if (id == R.id.wish_list)
        {
            fragment = fragmentManager.findFragmentByTag("wishlist");
            if (fragment != null && fragment.isVisible())
            {

            }
            else
            {
                WishlistFragment wishlistFragment = new WishlistFragment();
                wishlistFragment.setCommunicator(this);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                fragmentTransaction.replace(R.id.fragment_holder, wishlistFragment, "wishlist");
                fragmentTransaction.addToBackStack("wishlist");
                fragmentTransaction.commit();
            }
        }
        else if (id == R.id.cart_icon)
        {
            fragment = fragmentManager.findFragmentByTag("cart");
            if (fragment != null && fragment.isVisible())
            {

            }
            else
            {
                openCart();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOGOUT)
        {
            if (resultCode == REQUEST_CODE_LOGOUT)
            {
                editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                showMainFragment();
                getSnackbar("Logged out successfully.").show();
            }
        }
        else  if (requestCode == REQUEST_CODE_LOGIN)
        {
            if (resultCode == REQUEST_CODE_LOGIN)
            {
                cartFragment = (CartFragment) fragmentManager.findFragmentByTag("cart");
                if (cartFragment != null && cartFragment.isVisible())
                {
                    cartFragment.setEmail(data.getStringExtra(Account.EMAIL));
                    cartFragment.updateCart();
                }
                productDetailFragment = (ProductDetailFragment) fragmentManager.findFragmentByTag("product_detail_page");
                if (productDetailFragment != null && productDetailFragment.isVisible())
                {
                    productDetailFragment.setEmail(data.getStringExtra(Account.EMAIL));
                }
                wishlistFragment = (WishlistFragment) fragmentManager.findFragmentByTag("wishlist");
                if (wishlistFragment != null && wishlistFragment.isVisible())
                {
                    wishlistFragment.setEmail(data.getStringExtra(Account.EMAIL));
                    wishlistFragment.updateWishlist();
                }
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        fragment = fragmentManager.findFragmentByTag("product_list");
        if (fragment != null && fragment.isVisible())
        {
            fragmentManager.popBackStack();
            this.subCategoryId = 0;
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
        if (this.subCategoryId != subCategoryId)
        {
            this.subCategoryId = subCategoryId;
            if (subCategoryId > 99)
            {
                new ProductListingAsyncTask().execute(subCategoryId);
                this.subCategoryId = 0;
            }
        }
        else
        {
            closeDrawer();
        }

    }

    @Override
    public void showMainFragment()
    {
        fragment = fragmentManager.findFragmentByTag("main_fragment");
        if (fragment != null && fragment.isVisible())
        {
            closeDrawer();
        }
        else
        {
            backStackCount = fragmentManager.getBackStackEntryCount();
            while (backStackCount > 0)
            {
                fragmentManager.popBackStack();
                backStackCount--;
            }
            closeDrawer();
            this.subCategoryId = 0;
        }
    }

    @Override
    public void sendFilteredProducts(List<Product> products)
    {
        backStackCount = fragmentManager.getBackStackEntryCount();
        while (backStackCount > 0)
        {
            fragmentManager.popBackStack();
            backStackCount--;
        }
        ProductListFragment productListFragment = ProductListFragment.getInstance(products);
        productListFragment.setCommunicator(this);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.fragment_holder, productListFragment, "product_list");
        fragmentTransaction.addToBackStack("product_list");
        fragmentTransaction.commit();
        Log.d("FILTERED", "filtered products sent");
    }

    @Override
    public void openFilter(int subCategoryId)
    {
        FilterFragment filterFragment = FilterFragment.getInstance(subCategoryId);
        filterFragment.setCommunicator(this);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.fragment_holder, filterFragment, "filter_fragment");
        fragmentTransaction.addToBackStack("filter_fragment");
        fragmentTransaction.commit();
    }

    @Override
    public void showProductDetailFragment(int position, List<Product> products)
    {
        ProductDetailFragment productDetailFragment = ProductDetailFragment.getInstance(position, products);
        productDetailFragment.setCommunicator(this, this);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.fragment_holder, productDetailFragment, "product_detail_page");
        fragmentTransaction.addToBackStack("product_detail_page");
        fragmentTransaction.commit();
    }

    @Override
    public void openCart()
    {
        CartFragment cartFragment = new CartFragment();
        cartFragment.setCommunicator(this);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.fragment_holder, cartFragment, "cart");
        fragmentTransaction.addToBackStack("cart");
        fragmentTransaction.commit();
    }

    @Override
    public void openWishlist()
    {

    }

    @Override
    public void openCheckout(List<Integer> productIds)
    {
        startActivity(new Intent(this, CheckoutActivity.class).putIntegerArrayListExtra("product_ids", (ArrayList<Integer>) productIds));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public Snackbar getSnackbar(String textToDisplay)
    {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.index_page), textToDisplay, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();
        ((TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
        return snackbar;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void openSpecifications(Product product)
    {
        SpecificationFragment specificationFragment = SpecificationFragment.getInstance(product);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.fragment_holder, specificationFragment, "specification_fragment");
        fragmentTransaction.addToBackStack("specification_fragment");
        fragmentTransaction.commit();
    }

    @Override
    public void updateRecentlyViewed()
    {
        mainFragment = (MainFragment) fragmentManager.findFragmentByTag("main_fragment");
        if (mainFragment != null)
        {
            mainFragment.resetRecentlyUsed();
        }
    }

    @Override
    public void openProductListPage(List<Product> products)
    {
        if (products.size() > 1)
        {
            openProductList(products);
        }
        else
        {
            this.showProductDetailFragment(0, products);
        }
    }

    @Override
    public void openProductList(List<Product> products)
    {
        ProductListFragment productListFragment = ProductListFragment.getInstance(products);
        productListFragment.setCommunicator(this);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_out, R.anim.fade_in, R.anim.fade_out, R.anim.fade_in);
        fragmentTransaction.replace(R.id.fragment_holder, productListFragment, "product_list");
        fragmentTransaction.addToBackStack("product_list");
        fragmentTransaction.commit();
    }

    public void hideLoadingOfMainFragment()
    {
        mainFragment = (MainFragment) fragmentManager.findFragmentByTag("main_fragment");
        if (mainFragment != null)
        {
            mainFragment.setLoadingGone();
        }
    }

    @Override
    public void showAllTopRatedProducts()
    {
        mainFragment = (MainFragment) fragmentManager.findFragmentByTag("main_fragment");
        if (mainFragment != null && mainFragment.isVisible())
        {
            mainFragment.showTopRatedProducts();
        }
    }

    @Override
    public void openLoginPage()
    {
        startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_CODE_LOGIN);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void openRegistrationPage()
    {
        startActivity(new Intent(this, RegistrationActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onBackStackChanged()
    {
        backStackCount = fragmentManager.getBackStackEntryCount();
        if (getSupportActionBar() != null)
        {
            if (backStackCount > 0)
            {
                getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_arrow_back_white_24dp);
            }
            else
            {
                getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_menu_white_24dp);
            }
        }
    }

    class ProductListingAsyncTask extends AsyncTask<Integer, Void, List<Product>>
    {

        @Override
        protected void onPreExecute() {}

        @Override
        protected List<Product> doInBackground(Integer... params)
        {
            products = zohokartDAO.getProductsForSubCategory(params[0]);
            return products;
        }

        @Override
        protected void onPostExecute(List<Product> products)
        {
            super.onPostExecute(products);

            fragment = fragmentManager.findFragmentByTag("product_list");
            if (fragment != null && fragment.isVisible())
            {
                fragmentManager.popBackStack();
                productsToShow = products;
            }
            else
            {
                productsToShow = products;
            }
        }
    }

    class DataImportingTask extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            (findViewById(R.id.product_list_loading)).setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            // ** getting data into app ***
            dataImporter = new DataImporter(MainActivity.this);
            dataImporter.importData();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);

            (findViewById(R.id.product_list_loading)).setVisibility(View.GONE);

            // *** setting nav drawer ***
            navigationFragment = (NavigationFragment) getFragmentManager().findFragmentById(R.id.fragment);
            navigationFragment.setCommunicator(MainActivity.this);

            // *** including the default main fragment ***
            promotionBanners = zohokartDAO.getBanners();
            MainFragment mainFragment = MainFragment.getInstance(promotionBanners);
            mainFragment.setCommunicator(MainActivity.this, MainActivity.this, MainActivity.this);
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_holder, mainFragment, "main_fragment");
            fragmentTransaction.commit();

        }
    }
}
