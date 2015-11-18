package com.muhil.zohokart;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.muhil.zohokart.activities.CheckoutActivity;
import com.muhil.zohokart.activities.LoginActivity;
import com.muhil.zohokart.activities.ProductGalleryActivity;
import com.muhil.zohokart.activities.ProfileActivity;
import com.muhil.zohokart.adapters.WishlistAdapter;
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
import com.muhil.zohokart.models.ZohoKartFragments;
import com.muhil.zohokart.services.CartNotifyService;
import com.muhil.zohokart.utils.DataImporter;
import com.muhil.zohokart.utils.SnackBarProvider;
import com.muhil.zohokart.utils.ZohoKartSharePreferences;
import com.muhil.zohokart.utils.ZohokartDAO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationFragment.Communicator, FilterFragment.FilterCommunicator,
        ProductListCommunicator, WishlistFragment.WishlistCommunicator, CartFragment.CartCommunicator,
        ProductDetailFragment.ProductDetailCommunicator, ProductDetailPagerFragment.ProductDetailPageCommunicator,
        BannerFragment.BannerCommunicator, MainFragment.MainCommunicator, FragmentManager.OnBackStackChangedListener, SpecificationFragment.SpecCommunicator
{

    public static final int REQUEST_CODE_LOGIN = 101;
    public static final int REQUEST_CODE_LOGOUT = 102;
    public static final int REQUEST_CODE_LOGIN_FROM_PRODUCT_DETAIL = 103;
    public static final int REQUEST_CODE_LOGIN_FROM_WISHLIST = 104;
    public static final int REQUEST_CODE_CHECKOUT = 105;

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    DataImporter dataImporter;
    NavigationFragment navigationFragment;

    Product product;
    WishlistAdapter.WishlistViewHolder holder;
    List<Product> products, productsToShow;
    List<PromotionBanner> promotionBanners;
    ZohokartDAO zohokartDAO;

    int subCategoryId, currentItemPosition, selectedFilterCount = 0, oldSelectedFilterCount = 0, count = 0;
    boolean ifTopRated = false, ifRecentlyViewed = false, ifFromPager, ifSelectedFilterItemsChanged;

    SearchView searchView;

    Fragment fragment;
    MainFragment mainFragment;
    FilterFragment filterFragment;
    CartFragment cartFragment;
    WishlistFragment wishlistFragment;
    SpecificationFragment specificationFragment;
    ProductListFragment productListFragment, filteredProductListFragment;
    ProductDetailFragment productDetailFragment;
    android.support.v4.app.FragmentManager fragmentManager;
    android.support.v4.app.FragmentTransaction fragmentTransaction;
    public static final int ACTION_ACCOUNT_NAME = 1000;

    int backStackCount;

    SharedPreferences sharedPreferences, filterPref;
    SharedPreferences.Editor editor;

    AlarmManager alarmManager;
    PendingIntent cartNotifyService;
    Calendar cartNotifyCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // *** getting preferences for logged account ***
        sharedPreferences = getSharedPreferences(ZohoKartSharePreferences.LOGGED_ACCOUNT, MODE_PRIVATE);
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

        if (haveNetworkConnection())
        {
            checkInternetAndShowContent();
        }
        else
        {
            showNetworkErrorAlert();
        }

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        clearFilter();
    }

    private void checkInternetAndShowContent()
    {
        int xValue = findViewById(R.id.no_internet).getWidth();
        (findViewById(R.id.no_internet)).animate().setDuration(500).x(xValue).setListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
                (findViewById(R.id.no_internet)).setVisibility(View.GONE);
            }
        });

        new DataImportingTask().execute();

        // *** fragmentManager for transaction ***
        fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);
        zohokartDAO = new ZohokartDAO(this);

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
                    public void onDrawerOpened(View drawerView)
                    {
                    }

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
                            } else
                            {
                                fragmentManager.popBackStack();
                                openProductList(productsToShow);
                            }
                        }
                        productsToShow = null;
                    }

                    @Override
                    public void onDrawerStateChanged(int newState)
                    {
                    }
                }
        );
    }

    private void showNetworkErrorAlert()
    {
        AlertDialog.Builder internetDialog = new AlertDialog.Builder(this);
        internetDialog.setMessage("No internet connection");
        internetDialog.setCancelable(false);
        internetDialog.setPositiveButton("RETRY", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (haveNetworkConnection())
                {
                    checkInternetAndShowContent();
                } else
                {
                    showNetworkErrorAlert();
                }
            }
        });
        internetDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem;

        String accountEmail = sharedPreferences.getString(Account.EMAIL, "default");
        Log.d("INVALIDATING", "" + accountEmail);
        menuItem = menu.findItem(R.id.wish_list);
        count = zohokartDAO.getProductsFromWishlist(accountEmail).size();
        Log.d("INVALIDATING", "" + count);
        menuItem.setIcon(buildCounterDrawable(count, ZohoKartFragments.WISHLIST_FRAGMENT));

        menuItem = menu.findItem(R.id.cart_icon);
        count = zohokartDAO.getProductsFromCart(accountEmail).size();
        Log.d("INVALIDATING", "" + count);
        menuItem.setIcon(buildCounterDrawable(count, ZohoKartFragments.CART_FRAGMENT));

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
                } else
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
    public void invalidateOptions()
    {
        Log.d("INVALIDATING", "resetting");
        invalidateOptionsMenu();
    }

    @Override
    public void showGallery(int productId)
    {
        Intent intent = new Intent(this, ProductGalleryActivity.class);
        intent.putExtra("product_id", productId);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
    }

    private Drawable buildCounterDrawable(int count, String tag)
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = null;
        switch (tag)
        {
            case ZohoKartFragments.WISHLIST_FRAGMENT:
                view = inflater.inflate(R.layout.wishlist_with_count_layout, null);
                if (count == 0) {
                    View counterTextPanel = view.findViewById(R.id.countValuePanel);
                    counterTextPanel.setVisibility(View.GONE);
                } else {
                    Log.d("INVALIDATING", "visible");
                    View counterTextPanel = view.findViewById(R.id.countValuePanel);
                    counterTextPanel.setVisibility(View.VISIBLE);
                    TextView textView = (TextView) view.findViewById(R.id.count);
                    textView.setText("" + count);
                }
                break;
            case ZohoKartFragments.CART_FRAGMENT:
                view = inflater.inflate(R.layout.cart_with_count_layout, null);
                if (count == 0) {
                    View counterTextPanel = view.findViewById(R.id.countValuePanel);
                    counterTextPanel.setVisibility(View.GONE);
                } else {
                    Log.d("INVALIDATING", "visible");
                    View counterTextPanel = view.findViewById(R.id.countValuePanel);
                    counterTextPanel.setVisibility(View.VISIBLE);
                    TextView textView = (TextView) view.findViewById(R.id.count);
                    textView.setText("" + count);
                }
        }

        Bitmap bitmap = null;
        if (view != null)
        {
            view.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            view.setDrawingCacheEnabled(true);
            view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);
        }

        return new BitmapDrawable(getResources(), bitmap);
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
                filterFragment = (FilterFragment) fragmentManager.findFragmentByTag(ZohoKartFragments.FILTER_FRAGMENT);
                specificationFragment = (SpecificationFragment) fragmentManager.findFragmentByTag(ZohoKartFragments.SPECIFICATION_FRAGMENT);
                filteredProductListFragment = (ProductListFragment) fragmentManager.findFragmentByTag(ZohoKartFragments.FILTERED_PRODUCT_LIST_FRAGMENT);
                fragment = fragmentManager.findFragmentByTag(ZohoKartFragments.PRODUCT_LIST_FRAGMENT);
                cartFragment = (CartFragment) fragmentManager.findFragmentByTag(ZohoKartFragments.CART_FRAGMENT);
                wishlistFragment = (WishlistFragment) fragmentManager.findFragmentByTag(ZohoKartFragments.WISHLIST_FRAGMENT);
                if (fragment!=null && fragment.isVisible())
                {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                else if ((cartFragment!=null && cartFragment.isVisible()) || (wishlistFragment!=null && wishlistFragment.isVisible()))
                {
                    revealSearch();
                    refreshProductDetail(ZohoKartFragments.WISHLIST_FRAGMENT);
                }
                else if (specificationFragment != null && specificationFragment.isVisible())
                {
                    refreshProductDetail(ZohoKartFragments.SPECIFICATION_FRAGMENT);
                }
                else if (filteredProductListFragment != null && filteredProductListFragment.isVisible())
                {
                    Log.d("FILTER_BACK", "cleared");
                    clearFilter();
                    pop();
                }
                else if (filterFragment != null && filterFragment.isVisible())
                {
                    if ((selectedFilterCount > 0 && selectedFilterCount != oldSelectedFilterCount) || ifSelectedFilterItemsChanged)
                    {
                        AlertDialog.Builder filterAlert = new AlertDialog.Builder(this);
                        filterAlert.setMessage("Do you want to close without saving?");
                        filterAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                pop();
                            }
                        });
                        filterAlert.setNegativeButton("No", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        });
                        filterAlert.show();
                        ifSelectedFilterItemsChanged = false;
                    }
                    else if (selectedFilterCount == 0)
                    {
                        if (filteredProductListFragment != null)
                        {
                            fragmentManager.popBackStackImmediate(ZohoKartFragments.FILTERED_PRODUCT_LIST_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        }
                        else
                        {
                            pop();
                        }
                    }
                    else
                    {
                        pop();
                    }
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
            openLoginPage("LOGIN");
        }
        else if (id == ACTION_ACCOUNT_NAME)
        {
            startActivityForResult(new Intent(this, ProfileActivity.class), REQUEST_CODE_LOGOUT);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        }
        else if (id == R.id.wish_list)
        {
            hideSearch();
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
            hideSearch();
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

    private void pop()
    {
        fragmentManager.popBackStack();
    }

    public void hideSearch()
    {
        searchView.animate().setDuration(300).alpha(0).setListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                searchView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void revealSearch()
    {
        searchView.setVisibility(View.VISIBLE);
        searchView.animate().setDuration(300).alpha(1).setListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                searchView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void setProductMovedToCart(Product product, WishlistAdapter.WishlistViewHolder holder)
    {
        this.holder = holder;
        this.product = product;
    }

    public void lockDrawer()
    {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void setSelectedFilterCount(int count)
    {
        this.selectedFilterCount = count;
    }

    @Override
    public void setOldSelectedFilterCount(int count)
    {
        this.oldSelectedFilterCount = count;
    }

    @Override
    public void setSelectedFilterItemsChanged(boolean state)
    {
        this.ifSelectedFilterItemsChanged = state;
    }

    public void releaseDrawer()
    {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    private void clearFilter()
    {
        Log.d("BACKSTACK", "product list");
        filterPref = getSharedPreferences(ZohoKartSharePreferences.SELECTED_FILTERS, MODE_PRIVATE);
        editor = filterPref.edit();
        editor.clear();
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOGOUT)
        {
            if (resultCode == REQUEST_CODE_LOGOUT)
            {
                String response = data.getStringExtra(Account.EMAIL);
                if (!(response.equals("")))
                {
                    wishlistFragment = (WishlistFragment) fragmentManager.findFragmentByTag(ZohoKartFragments.WISHLIST_FRAGMENT);
                    cartFragment = (CartFragment) fragmentManager.findFragmentByTag(ZohoKartFragments.CART_FRAGMENT);
                    if (wishlistFragment != null && wishlistFragment.isVisible())
                    {
                        wishlistFragment.setEmail(response);
                        wishlistFragment.updateWishlist();

                    }
                    else if (cartFragment != null && cartFragment.isVisible())
                    {
                        cartFragment.setEmail(response);
                        cartFragment.updateCart();
                    }
                    else
                    {
                        showMainFragment();
                    }
                }
                editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                invalidateOptions();
                SnackBarProvider.getSnackbar("Logged out successfully.", findViewById(R.id.index_page)).show();
            }
        }
        else  if (requestCode == REQUEST_CODE_LOGIN)
        {
            if (resultCode == REQUEST_CODE_LOGIN)
            {
                String emailFromLogin = data.getStringExtra(Account.EMAIL);
                if (!(emailFromLogin.equals("")))
                {
                    wishlistFragment = (WishlistFragment) fragmentManager.findFragmentByTag(ZohoKartFragments.WISHLIST_FRAGMENT);
                    cartFragment = (CartFragment) fragmentManager.findFragmentByTag(ZohoKartFragments.CART_FRAGMENT);
                    if (wishlistFragment != null && wishlistFragment.isVisible())
                    {
                        wishlistFragment.setEmail(emailFromLogin);
                        wishlistFragment.updateWishlist();
                        invalidateOptions();

                    }
                    else if (cartFragment != null && cartFragment.isVisible())
                    {
                        cartFragment.setEmail(emailFromLogin);
                        cartFragment.updateCart();
                        invalidateOptions();
                    }
                    else
                    {
                        showMainFragment();
                        invalidateOptions();
                    }
                }
            }
        }
        else if (requestCode == REQUEST_CODE_LOGIN_FROM_PRODUCT_DETAIL)
        {
            if (resultCode == REQUEST_CODE_LOGIN_FROM_PRODUCT_DETAIL)
            {
                String emailFromLogin = data.getStringExtra(Account.EMAIL);
                if (!(emailFromLogin.equals("")))
                {
                    if (zohokartDAO.addToCart(products.get(currentItemPosition).getId(), emailFromLogin))
                    {
                        SnackBarProvider.getSnackbar("Product added to cart.", findViewById(R.id.index_page)).show();
                        productDetailFragment = (ProductDetailFragment) fragmentManager.findFragmentByTag(ZohoKartFragments.PRODUCT_DETAIL_FRAGMENT);
                        if (productDetailFragment != null && productDetailFragment.isVisible())
                        {
                            productDetailFragment.toogleCartAction();
                        }
                    }
                }
            }
        }
        else if (requestCode == REQUEST_CODE_LOGIN_FROM_WISHLIST)
        {
            if (resultCode == REQUEST_CODE_LOGIN_FROM_WISHLIST)
            {
                String emailFromLogin = data.getStringExtra(Account.EMAIL);
                if (!(emailFromLogin.equals("")))
                {
                    if (zohokartDAO.addToWishlist(product.getId(), emailFromLogin))
                    {
                        if (zohokartDAO.addToCart(product.getId(), emailFromLogin))
                        {
                            SnackBarProvider.getSnackbar("Product added to cart.", findViewById(R.id.index_page)).show();
                            wishlistFragment = (WishlistFragment) fragmentManager.findFragmentByTag(ZohoKartFragments.WISHLIST_FRAGMENT);
                            if (wishlistFragment != null && wishlistFragment.isVisible())
                            {
                                wishlistFragment.setEmail(emailFromLogin);
                                wishlistFragment.updateWishlist();
                                wishlistFragment.changeCartActionView(holder);
                                wishlistFragment.setEmailInAdapter(emailFromLogin);
                                invalidateOptions();

                            }
                            product = null;
                            holder = null;
                        }
                        else
                        {
                            SnackBarProvider.getSnackbar("Error while adding to cart.", findViewById(R.id.index_page)).show();
                        }
                    }
                    else
                    {
                        SnackBarProvider.getSnackbar("Error while adding to cart.", findViewById(R.id.index_page)).show();
                    }
                }
            }
        }
        else if (requestCode == REQUEST_CODE_CHECKOUT)
        {
            if (resultCode == REQUEST_CODE_CHECKOUT)
            {
                boolean checkoutStatus = data.getBooleanExtra("checkout", false);
                if (checkoutStatus)
                {
                    showMainFragment();
                }
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        filterFragment = (FilterFragment) fragmentManager.findFragmentByTag(ZohoKartFragments.FILTER_FRAGMENT);
        specificationFragment = (SpecificationFragment) fragmentManager.findFragmentByTag(ZohoKartFragments.SPECIFICATION_FRAGMENT);
        productListFragment = (ProductListFragment) fragmentManager.findFragmentByTag(ZohoKartFragments.PRODUCT_LIST_FRAGMENT);
        filteredProductListFragment = (ProductListFragment) fragmentManager.findFragmentByTag(ZohoKartFragments.FILTERED_PRODUCT_LIST_FRAGMENT);
        cartFragment = (CartFragment) fragmentManager.findFragmentByTag(ZohoKartFragments.CART_FRAGMENT);
        wishlistFragment = (WishlistFragment) fragmentManager.findFragmentByTag(ZohoKartFragments.WISHLIST_FRAGMENT);
        if (!searchView.isIconified())
        {
            Log.d("SEARCH", "set iconify");
            searchView.onActionViewCollapsed();
            searchView.setIconified(true);
        }
        else if ((cartFragment!=null && cartFragment.isVisible()) || (wishlistFragment!=null && wishlistFragment.isVisible()))
        {
            revealSearch();
            refreshProductDetail(ZohoKartFragments.WISHLIST_FRAGMENT);
        }
        else if (specificationFragment != null && specificationFragment.isVisible())
        {
            refreshProductDetail(ZohoKartFragments.SPECIFICATION_FRAGMENT);
        }
        else if (productListFragment != null && productListFragment.isVisible())
        {
            clearFilter();
            fragmentManager.popBackStack();
            if (!productListFragment.isFilter())
            {
                this.subCategoryId = 0;
            }
            searchView.onActionViewCollapsed();
            searchView.setIconified(true);
        }
        else if (filteredProductListFragment != null && filteredProductListFragment.isVisible())
        {
            Log.d("FILTER_BACK", "cleared");
            clearFilter();
            fragmentManager.popBackStack();
        }
        else if (filterFragment != null && filterFragment.isVisible())
        {
            if ((selectedFilterCount > 0 && selectedFilterCount != oldSelectedFilterCount) || ifSelectedFilterItemsChanged)
            {
                AlertDialog.Builder filterAlert = new AlertDialog.Builder(this);
                filterAlert.setMessage("Do you want to close without saving?");
                filterAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        pop();
                    }
                });
                filterAlert.setNegativeButton("No", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
                filterAlert.show();
                ifSelectedFilterItemsChanged = false;
            }
            else if (selectedFilterCount == 0)
            {
                if (filteredProductListFragment != null)
                {
                    fragmentManager.popBackStackImmediate(ZohoKartFragments.FILTERED_PRODUCT_LIST_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                else
                {
                    pop();
                }
            }
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

    private void refreshProductDetail(String tag)
    {
        productDetailFragment = (ProductDetailFragment) fragmentManager.findFragmentByTag(ZohoKartFragments.PRODUCT_DETAIL_FRAGMENT);
        if (productDetailFragment != null)
        {
            fragmentManager.popBackStack(ZohoKartFragments.PRODUCT_DETAIL_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            Log.d("BS_COUNT", "" + fragmentManager.getBackStackEntryCount());
            productDetailFragment = ProductDetailFragment.getInstance(currentItemPosition, products);
            productDetailFragment.setCommunicator(this, this);
            fragmentTransaction = fragmentManager.beginTransaction();
            if (tag.equals(ZohoKartFragments.SPECIFICATION_FRAGMENT))
            {
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
            }
            else
            {
                fragmentTransaction.setCustomAnimations(R.anim.fade_out, R.anim.fade_in);
            }
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
                clearFilter();
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
        invalidateOptions();
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
        productListFragment = (ProductListFragment) fragmentManager.findFragmentByTag(ZohoKartFragments.FILTERED_PRODUCT_LIST_FRAGMENT);
        if (productListFragment != null)
        {
            Log.d("FILTER_FRAG", "filtered product list fragment popped");
            fragmentManager.popBackStackImmediate(ZohoKartFragments.FILTERED_PRODUCT_LIST_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        else
        {
            fragmentManager.popBackStack();
        }
        ProductListFragment productListFragment = ProductListFragment.getInstance(products, true);
        productListFragment.setCommunicator(this);
        if (products.size() > 0)
        {
            productListFragment.setFilterEnabled(true);
            productListFragment.setSortEnabled(true);
        }
        stackFragment(productListFragment, ZohoKartFragments.FILTERED_PRODUCT_LIST_FRAGMENT);
        Log.d("FILTERED", "filtered products sent");
    }

    @Override
    public void tellToMainParameters(int position, List<Product> products)
    {
        this.currentItemPosition = position;
        this.products = products;
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
    public void setIfTopRated(boolean status)
    {
        this.ifTopRated = status;
    }

    @Override
    public void setIfRecentlyViewed(boolean status)
    {
        this.ifRecentlyViewed = status;
    }

    @Override
    public void setIfFromPager(boolean status)
    {
        this.ifFromPager = status;
    }

    @Override
    public void openProductList(List<Product> products)
    {
        ProductListFragment productListFragment = ProductListFragment.getInstance(products, false);
        productListFragment.setCommunicator(this);
        if (products.size() > 0)
        {
            if (ifTopRated || ifRecentlyViewed || ifFromPager)
            {
                productListFragment.setFilterEnabled(false);
                productListFragment.setSortEnabled(true);
                this.ifTopRated = false;
                this.ifRecentlyViewed = false;
                this.ifFromPager = false;
            }
            else
            {
                productListFragment.setFilterEnabled(true);
                productListFragment.setSortEnabled(true);
            }
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
    public void openLoginPage(String tag)
    {
        switch (tag)
        {
            case "LOGIN":
                startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_CODE_LOGIN);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case ZohoKartFragments.PRODUCT_DETAIL_FRAGMENT:
                startActivityForResult(new Intent(this, LoginActivity.class).putExtra("request_code", REQUEST_CODE_LOGIN_FROM_PRODUCT_DETAIL), REQUEST_CODE_LOGIN_FROM_PRODUCT_DETAIL);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case ZohoKartFragments.WISHLIST_FRAGMENT:
                startActivityForResult(new Intent(this, LoginActivity.class).putExtra("request_code", REQUEST_CODE_LOGIN_FROM_WISHLIST), REQUEST_CODE_LOGIN_FROM_WISHLIST);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
    }

    @Override
    public void setCartNotificationAlarm(String email)
    {
        Log.d("CART_NOTIFY", "inside alarm maker");
        if (cartNotifyService != null && alarmManager != null)
        {
            Log.d("CART_NOTIFY", "cancelling alarm");
            alarmManager.cancel(cartNotifyService);
        }
        cartNotifyCalendar = Calendar.getInstance();
        cartNotifyCalendar.add(Calendar.SECOND, 20);
        Intent deliveryServiceIntent = new Intent(this, CartNotifyService.class);
        deliveryServiceIntent.putExtra("email", email);
        cartNotifyService = PendingIntent.getService(this, (int) System.currentTimeMillis(), deliveryServiceIntent, 0);
        alarmManager.set(AlarmManager.RTC, cartNotifyCalendar.getTimeInMillis(), cartNotifyService);
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

        if (haveNetworkConnection())
        {
            fragmentTransaction = fragmentManager.beginTransaction();
            switch (tag)
            {
                case ZohoKartFragments.SPECIFICATION_FRAGMENT:
                    fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;

                default:
                    fragmentTransaction.setCustomAnimations(R.anim.fade_out, R.anim.fade_in, R.anim.fade_out, R.anim.fade_in);
                    break;
            }
            fragmentTransaction.replace(R.id.fragment_holder, fragment, tag);
            fragmentTransaction.addToBackStack(tag);
            fragmentTransaction.commit();
        }
        else
        {
            showNetworkErrorAlertWithinApp(fragment, tag);
        }
    }

    private void showNetworkErrorAlertWithinApp(final Fragment fragment, final String tag)
    {
        AlertDialog.Builder internetDialog = new AlertDialog.Builder(this);
        internetDialog.setMessage("No internet connection");
        internetDialog.setCancelable(false);
        internetDialog.setPositiveButton("RETRY", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (haveNetworkConnection())
                {
                    stackFragment(fragment, tag);
                } else
                {
                    showNetworkErrorAlertWithinApp(fragment, tag);
                }
            }
        });
        internetDialog.show();
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
