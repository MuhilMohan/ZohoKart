package com.muhil.zohokart.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.muhil.zohokart.MainActivity;
import com.muhil.zohokart.R;
import com.muhil.zohokart.fragments.OrderConfirmationFragment;
import com.muhil.zohokart.fragments.PaymentFragment;
import com.muhil.zohokart.models.Account;
import com.muhil.zohokart.models.ZohoKartFragments;
import com.muhil.zohokart.utils.ZohoKartSharePreferences;

import java.util.List;

public class CheckoutActivity extends AppCompatActivity implements OrderConfirmationFragment.OrderConfirmationCommunicator,
        PaymentFragment.PaymentCommunicator
{

    public static final int REQUEST_LOGIN = 1001;

    Toolbar toolbar;
    SharedPreferences sharedPreferences;
    String email, password;
    List<Integer> productIds;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    int backStackCount;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_arrow_back_white_24dp);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            (toolbar.findViewById(R.id.app_icon)).setVisibility(View.GONE);
            toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        }

        productIds = getIntent().getIntegerArrayListExtra("product_ids");

        fragmentManager = getSupportFragmentManager();

        sharedPreferences = getSharedPreferences(ZohoKartSharePreferences.LOGGED_ACCOUNT, MODE_PRIVATE);

        addOrderConfirmationFragmentIfLoggedIn();

    }

    private void addOrderConfirmationFragmentIfLoggedIn()
    {
        email = sharedPreferences.getString(Account.EMAIL, "");
        password = sharedPreferences.getString(Account.PASSWORD, "");

        if (!(email.equals("")) && !(password.equals("")))
        {
            OrderConfirmationFragment orderConfirmationFragment = OrderConfirmationFragment.getInstance(email, password, productIds);
            orderConfirmationFragment.setCommunicator(this);
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_out, R.anim.fade_in);
            fragmentTransaction.replace(R.id.checkout_fragments_holder, orderConfirmationFragment, "order_confirmation_fragment");
            fragmentTransaction.commit();
        }
        else
        {
            startActivityForResult(new Intent(this, LoginActivity.class).putExtra("request_code", REQUEST_LOGIN), CheckoutActivity.REQUEST_LOGIN);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CheckoutActivity.REQUEST_LOGIN)
        {
            if (resultCode == CheckoutActivity.REQUEST_LOGIN)
            {
                String emailFromLogin = data.getStringExtra(Account.EMAIL);
                if (emailFromLogin.equals(""))
                {
                    Log.d("LOGIN_RES", "loading detail");
                    setResult(MainActivity.REQUEST_CODE_CHECKOUT, getIntent().putExtra("checkout", false));
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
                else
                {
                    Log.d("ORDER_FRAG", "loading orders");
                    addOrderConfirmationFragmentIfLoggedIn();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order_summary, menu);
        return true;
    }

    @Override
    public void onBackPressed()
    {
        backStackCount = fragmentManager.getBackStackEntryCount();
        if (backStackCount > 0)
        {
            fragmentManager.popBackStack();
        }
        else
        {
            setResult(MainActivity.REQUEST_CODE_CHECKOUT, getIntent().putExtra("checkout", false));
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home)
        {
            backStackCount = fragmentManager.getBackStackEntryCount();
            if (backStackCount > 0)
            {
                fragmentManager.popBackStack();
            }
            else
            {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        }

        return super.onOptionsItemSelected(item);
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
    public void saveAndCloseCheckout()
    {
        setResult(MainActivity.REQUEST_CODE_CHECKOUT, getIntent().putExtra("checkout", true));
        finish();
    }

    private void stackFragment(Fragment fragment, String tag)
    {
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_out, R.anim.fade_in, R.anim.fade_out, R.anim.fade_in);
        fragmentTransaction.replace(R.id.checkout_fragments_holder, fragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    @Override
    public void proceedToPayment(String email, List<Integer> productIds, List<Integer> quantities)
    {
        PaymentFragment paymentFragment = PaymentFragment.getInstance(email, productIds, quantities);
        paymentFragment.setCommunicator(this);
        stackFragment(paymentFragment, ZohoKartFragments.PAYMENT_FRAGMENT);
    }
}
