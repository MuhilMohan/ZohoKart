package com.muhil.zohokart.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.muhil.zohokart.R;
import com.muhil.zohokart.fragments.OrderConfirmationFragment;
import com.muhil.zohokart.models.Account;
import com.muhil.zohokart.utils.ZohoKartSharePreferences;

import java.util.List;

public class CheckoutActivity extends AppCompatActivity
{

    SharedPreferences sharedPreferences;
    String email, password;
    List<Integer> productIds;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        productIds = getIntent().getIntegerArrayListExtra("product_ids");

        fragmentManager = getSupportFragmentManager();

        sharedPreferences = getSharedPreferences(ZohoKartSharePreferences.LOGGED_ACCOUNT, MODE_PRIVATE);

        email = sharedPreferences.getString(Account.EMAIL, "");
        password = sharedPreferences.getString(Account.PASSWORD, "");

        if (!(email.equals("")) && !(password.equals("")))
        {
            OrderConfirmationFragment orderConfirmationFragment = OrderConfirmationFragment.getInstance(email, password, productIds);
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.checkout_fragments_holder, orderConfirmationFragment, "order_confirmation_fragment");
            fragmentTransaction.commit();
        }
        else
        {
            startActivityForResult(new Intent(this, LoginActivity.class), 1000);

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
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
