package com.muhil.zohokart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.muhil.zohokart.models.ZohoKartFragments;
import com.muhil.zohokart.utils.DataImporter;
import com.muhil.zohokart.utils.SnackBarProvider;
import com.muhil.zohokart.utils.ZohoKartSharePreferences;
import com.muhil.zohokart.utils.ZohokartDAO;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationFragment.Communicator, FilterFragment.FilterCommunicator,
        ProductListCommunicator, WishlistFragment.WishlistCommunicator, CartFragment.CartCommunicator,
        ProductDetailFragment.ProductDetailCommunicator, ProductDetailPagerFragment.ProductDetailPageCommunicator,
        BannerFragment.BannerCommunicator, MainFragment.MainCommunicator, FragmentManager.OnBackStackChangedListener, SpecificationFragment.SpecCommunicator
{

    public static final int REQUEST_CODE_LOGIN = 101;
    public static final int REQUEST_CODE_LOGOUT = 102;
    public static final int REQUEST_PERMISSION_FOR_STORAGE = 1001;
    public static final int REQUEST_CODE_CHECKOUT = 103;

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    DataImporter dataImporter;
    NavigationFragment navigationFragment;

    List<Product> products, productsToShow;
    List<PromotionBanner> promotionBanners;
    ZohokartDAO zohokartDAO;

    int subCategoryId, currentItemPosition;

    SearchView searchView;
    EditText searchEditText;

    Fragment fragment;
    MainFragment mainFragment;
    CartFragment cartFragment;
    WishlistFragment wishlistFragment;
    SpecificationFragment specificationFragment;
    ProductListFragment productListFragment;
    ProductDetailFragment productDetailFragment;
    android.support.v4.app.FragmentManager fragmentManager;
    android.support.v4.app.FragmentTransaction fragmentTransaction;
    public static final int ACTION_ACCOUNT_NAME = 1000;

    int backStackCount;

    SharedPreferences sharedPreferences, filterPref;
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

        checkInternetAndShowContent();

        findViewById(R.id.retry_network).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        checkInternetAndShowContent();
                    }
                }
        );

    }

    private void checkInternetAndShowContent()
    {
        if (haveNetworkConnection())
        {
            Toast.makeText(MainActivity.this, "Internet available.", Toast.LENGTH_SHORT).show();

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
                            navigationFragment.restScroll();
                            if (productsToShow != null)
                            {
                                productListFragment = (ProductListFragment) fragmentManager.findFragmentByTag(ZohoKartFragments.PRODUCT_LIST_FRAGMENT);
                                if (productListFragment == null)
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
        else
        {
            (findViewById(R.id.no_internet)).setVisibility(View.VISIBLE);
        }
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
                fragment = fragmentManager.findFragmentByTag(ZohoKartFragments.PRODUCT_LIST_FRAGMENT);
                if (fragment != null && fragment.isVisible())
                {
                    fragmentManager.popBackStack();
                    openProductListForSearch(products);
                    searchView.onActionViewCollapsed();
                    searchView.setIconified(true);
                }
                else
                {
                    openProductListForSearch(products);
                    searchView.onActionViewCollapsed();
                    searchView.setIconified(true);
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
                (toolbar.findViewById(R.id.app_icon)).setVisibility(View.VISIBLE);
                return false;
            }
        });

        // *** searchView ends ***

        return true;
    }

    private void openProductListForSearch(List<Product> products)
    {
        ProductListFragment productListFragment = ProductListFragment.getInstance(products, false);
        productListFragment.setCommunicator(this);
        if (products.size() > 0)
        {
            productListFragment.setSortEnabled(true);
        }
        stackFragment(productListFragment, ZohoKartFragments.PRODUCT_LIST_FRAGMENT);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                (findViewById(R.id.product_list_loading)).setVisibility(View.GONE);
            }
        }, 300);
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
                specificationFragment = (SpecificationFragment) fragmentManager.findFragmentByTag(ZohoKartFragments.SPECIFICATION_FRAGMENT);
                fragment = fragmentManager.findFragmentByTag(ZohoKartFragments.PRODUCT_LIST_FRAGMENT);
                cartFragment = (CartFragment) fragmentManager.findFragmentByTag(ZohoKartFragments.CART_FRAGMENT);
                wishlistFragment = (WishlistFragment) fragmentManager.findFragmentByTag(ZohoKartFragments.WISHLIST_FRAGMENT);
                if (fragment!=null && fragment.isVisible())
                {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                else if ((cartFragment!=null && cartFragment.isVisible()) || (wishlistFragment!=null && wishlistFragment.isVisible()))
                {
                    refreshProductDetail();
                }
                else if (specificationFragment != null && specificationFragment.isVisible())
                {
                    refreshProductDetail();
                }
                else if (backStackCount == 2)
                {
                    pop();
                }
                else
                {
                    pop();
                }
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
            fragment = fragmentManager.findFragmentByTag(ZohoKartFragments.WISHLIST_FRAGMENT);
            if (fragment == null)
            {
                WishlistFragment wishlistFragment = new WishlistFragment();
                wishlistFragment.setCommunicator(this);
                stackFragment(wishlistFragment, ZohoKartFragments.WISHLIST_FRAGMENT);
            }
            else if (!(fragment.isVisible()))
            {
                fragmentManager.popBackStackImmediate(ZohoKartFragments.WISHLIST_FRAGMENT, 0);
            }
        }
        else if (id == R.id.cart_icon)
        {
            fragment = fragmentManager.findFragmentByTag(ZohoKartFragments.CART_FRAGMENT);
            if (fragment == null)
            {
                openCart();
            }
            else if (!(fragment.isVisible()))
            {
                fragmentManager.popBackStackImmediate(ZohoKartFragments.CART_FRAGMENT, 0);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void popAndReset()
    {
        clearFilter();
        fragmentManager.popBackStack();
        this.subCategoryId = 0;
    }

    private void pop()
    {
        clearFilter();
        fragmentManager.popBackStack();
    }

    public void lockDrawer()
    {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public void releaseDrawer()
    {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    private void clearFilter()
    {
        fragment = fragmentManager.findFragmentByTag(ZohoKartFragments.PRODUCT_LIST_FRAGMENT);
        if(fragment != null && fragment.isVisible())
        {
            Log.d("BACKSTACK", "product list");
            filterPref = getSharedPreferences(ZohoKartSharePreferences.SELECTED_FILTERS, MODE_PRIVATE);
            editor = filterPref.edit();
            editor.clear();
            editor.apply();
        }
        else
        {
            Log.d("BACKSTACK", "product list popped");
        }
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
                SnackBarProvider.getSnackbar("Logged out successfully.", findViewById(R.id.index_page)).show();
            }
        }
        else  if (requestCode == REQUEST_CODE_LOGIN)
        {
            if (resultCode == REQUEST_CODE_LOGIN)
            {
                showMainFragment();
            }
        }
        else if (requestCode == REQUEST_CODE_CHECKOUT)
        {
            if (resultCode == REQUEST_CODE_CHECKOUT)
            {
                showMainFragment();
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        specificationFragment = (SpecificationFragment) fragmentManager.findFragmentByTag(ZohoKartFragments.SPECIFICATION_FRAGMENT);
        fragment = fragmentManager.findFragmentByTag(ZohoKartFragments.PRODUCT_LIST_FRAGMENT);
        cartFragment = (CartFragment) fragmentManager.findFragmentByTag(ZohoKartFragments.CART_FRAGMENT);
        wishlistFragment = (WishlistFragment) fragmentManager.findFragmentByTag(ZohoKartFragments.WISHLIST_FRAGMENT);
        if ((cartFragment!=null && cartFragment.isVisible()) || (wishlistFragment!=null && wishlistFragment.isVisible()))
        {
            refreshProductDetail();
        }
        else if (specificationFragment != null && specificationFragment.isVisible())
        {
            refreshProductDetail();
        }
        else if (fragment != null && fragment.isVisible())
        {
            clearFilter();
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

    private void refreshProductDetail()
    {
        productDetailFragment = (ProductDetailFragment) fragmentManager.findFragmentByTag(ZohoKartFragments.PRODUCT_DETAIL_FRAGMENT);
        if (productDetailFragment != null)
        {
            fragmentManager.popBackStack(ZohoKartFragments.PRODUCT_DETAIL_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            Log.d("BS_COUNT", "" + fragmentManager.getBackStackEntryCount());
            productDetailFragment = ProductDetailFragment.getInstance(currentItemPosition, products);
            productDetailFragment.setCommunicator(this, this);
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_out, R.anim.slide_up_from_bottom, R.anim.fade_out, R.anim.slide_up_from_bottom);
            fragmentTransaction.replace(R.id.fragment_holder, productDetailFragment, ZohoKartFragments.PRODUCT_DETAIL_FRAGMENT);
            fragmentTransaction.addToBackStack(ZohoKartFragments.PRODUCT_DETAIL_FRAGMENT);
            fragmentTransaction.commit();
        }
        else
        {
            pop();
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
        mainFragment = (MainFragment) fragmentManager.findFragmentByTag(ZohoKartFragments.MAIN_FRAGMENT);
        if (mainFragment.isVisible())
        {
            mainFragment.resetScroll();
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
            mainFragment.resetScroll();
            closeDrawer();
            this.subCategoryId = 0;
        }
    }

    @Override
    public void updateWishlistStatus()
    {
    }

    @Override
    public void sendFilteredProducts(List<Product> products)
    {
        fragmentManager.popBackStack(ZohoKartFragments.PRODUCT_LIST_FRAGMENT, 0);
        ProductListFragment productListFragment = ProductListFragment.getInstance(products, true);
        productListFragment.setCommunicator(this);
        if (products.size() > 0)
        {
            productListFragment.setFilterEnabled(true);
            productListFragment.setSortEnabled(true);
        }
        stackFragment(productListFragment, ZohoKartFragments.PRODUCT_LIST_FRAGMENT);
        Log.d("FILTERED", "filtered products sent");
    }

    @Override
    public void openFilter()
    {
        FilterFragment filterFragment = FilterFragment.getInstance(this.subCategoryId);
        filterFragment.setCommunicator(this);
        stackFragment(filterFragment, ZohoKartFragments.FILTER_FRAGMENT);
    }

    @Override
    public void showProductDetailFragment(int position, List<Product> products)
    {
        this.currentItemPosition = position;
        this.products = products;
        ProductDetailFragment productDetailFragment = ProductDetailFragment.getInstance(position, products);
        productDetailFragment.setCommunicator(this, this);
        stackFragment(productDetailFragment, ZohoKartFragments.PRODUCT_DETAIL_FRAGMENT);
    }

    @Override
    public void openCart()
    {
        CartFragment cartFragment = new CartFragment();
        cartFragment.setCommunicator(this);
        stackFragment(cartFragment, ZohoKartFragments.CART_FRAGMENT);
    }

    @Override
    public void openCheckout(List<Integer> productIds)
    {
        startActivityForResult(new Intent(this, CheckoutActivity.class).putIntegerArrayListExtra("product_ids", (ArrayList<Integer>) productIds), REQUEST_CODE_CHECKOUT);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
        specificationFragment.setCommunicator(this);
        stackFragment(specificationFragment, ZohoKartFragments.SPECIFICATION_FRAGMENT);
    }

    @Override
    public boolean checkWishlist(int productId, String email)
    {
        return zohokartDAO.checkInWishlist(productId, email);

    }

    @Override
    public void updateRecentlyViewed()
    {
        mainFragment = (MainFragment) fragmentManager.findFragmentByTag(ZohoKartFragments.MAIN_FRAGMENT);
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
        ProductListFragment productListFragment = ProductListFragment.getInstance(products, false);
        productListFragment.setCommunicator(this);
        if (products.size() > 0)
        {
            productListFragment.setFilterEnabled(true);
            productListFragment.setSortEnabled(true);
        }
        stackFragment(productListFragment, ZohoKartFragments.PRODUCT_LIST_FRAGMENT);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                (findViewById(R.id.product_list_loading)).setVisibility(View.GONE);
            }
        }, 300);
    }

    @Override
    public void showAllTopRatedProducts()
    {
        mainFragment = (MainFragment) fragmentManager.findFragmentByTag(ZohoKartFragments.MAIN_FRAGMENT);
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

    @Override
    public void onBackStackChanged()
    {
        backStackCount = fragmentManager.getBackStackEntryCount();
        if (getSupportActionBar() != null)
        {
            if (backStackCount > 0)
            {
                fragment = fragmentManager.findFragmentByTag(ZohoKartFragments.PRODUCT_LIST_FRAGMENT);
                if (fragment!=null && fragment.isVisible())
                {
                    getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_menu_white_24dp);
                }
                else
                {
                    getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_arrow_back_white_24dp);
                }
            }
            else
            {
                releaseDrawer();
                getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_menu_white_24dp);
            }
        }
        mainFragment = (MainFragment) fragmentManager.findFragmentByTag(ZohoKartFragments.MAIN_FRAGMENT);
        if (mainFragment.isVisible())
        {
            mainFragment.resetScroll();
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

            fragment = fragmentManager.findFragmentByTag(ZohoKartFragments.PRODUCT_LIST_FRAGMENT);
            if (fragment != null && fragment.isVisible())
            {
                (findViewById(R.id.product_list_loading)).setVisibility(View.VISIBLE);
                fragmentManager.popBackStack();
                productsToShow = products;
            }
            else
            {
                (findViewById(R.id.product_list_loading)).setVisibility(View.VISIBLE);
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
            fragmentTransaction.add(R.id.fragment_holder, mainFragment, ZohoKartFragments.MAIN_FRAGMENT);
            fragmentTransaction.commit();
        }
    }

    private void stackFragment(Fragment fragment, String tag)
    {
        fragmentTransaction = fragmentManager.beginTransaction();
        if (tag.equals(ZohoKartFragments.PRODUCT_DETAIL_FRAGMENT))
        {
            fragmentTransaction.setCustomAnimations(R.anim.slide_down_from_top, R.anim.fade_in, R.anim.fade_out, R.anim.slide_up_from_bottom);
        }
        else if (tag.equals(ZohoKartFragments.SPECIFICATION_FRAGMENT) || tag.equals(ZohoKartFragments.WISHLIST_FRAGMENT) ||
                tag.equals(ZohoKartFragments.CART_FRAGMENT))
        {
            fragmentTransaction.setCustomAnimations(R.anim.slide_down_from_top, R.anim.fade_in, R.anim.fade_out, R.anim.slide_up_from_bottom);
        }
        else
        {
            fragmentTransaction.setCustomAnimations(R.anim.fade_out, R.anim.fade_in, R.anim.fade_out, R.anim.fade_in);
        }
        fragmentTransaction.replace(R.id.fragment_holder, fragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] availableNetworks = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo networkInfo : availableNetworks) {
            if (networkInfo.getTypeName().equalsIgnoreCase("WIFI"))
                if (networkInfo.isConnected())
                    haveConnectedWifi = true;
            if (networkInfo.getTypeName().equalsIgnoreCase("MOBILE"))
                if (networkInfo.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

}
