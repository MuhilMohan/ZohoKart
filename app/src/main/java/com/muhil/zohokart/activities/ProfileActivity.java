package com.muhil.zohokart.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.muhil.zohokart.MainActivity;
import com.muhil.zohokart.R;
import com.muhil.zohokart.fragments.OrderLineItemsFragment;
import com.muhil.zohokart.fragments.OrdersFragment;
import com.muhil.zohokart.fragments.SavedCardFragment;
import com.muhil.zohokart.models.Account;
import com.muhil.zohokart.models.PaymentCard;
import com.muhil.zohokart.models.ZohoKartFragments;
import com.muhil.zohokart.utils.ZohoKartSharePreferences;
import com.muhil.zohokart.utils.ZohokartDAO;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements SavedCardFragment.PaymentCardCommunicator, OrdersFragment.OrderCommunicator
{

    List<PaymentCard> cards;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    ZohokartDAO zohokartDAO;
    Toolbar toolbar;
    Account account;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String email, password, name;
    LinearLayout accountCardHolder;
    LayoutInflater inflater;
    CardView card_item_view;
    public static int CARD_ID = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fragmentManager = getSupportFragmentManager();
        inflater = LayoutInflater.from(ProfileActivity.this);
        cards = new ArrayList<>();

        accountCardHolder = (LinearLayout) findViewById(R.id.account_card_holder);

        // *** setting the toolbar ***
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_arrow_back_white_24dp);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            (toolbar.findViewById(R.id.logout_action)).setVisibility(View.VISIBLE);
            toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        }

        // *** getting data from preferences and db to update the view ***
        zohokartDAO = new ZohokartDAO(this);
        sharedPreferences = getSharedPreferences(ZohoKartSharePreferences.LOGGED_ACCOUNT, MODE_PRIVATE);
        email = sharedPreferences.getString(Account.EMAIL, "");
        password = sharedPreferences.getString(Account.PASSWORD, "");
        name = sharedPreferences.getString(Account.NAME, "");
        account = zohokartDAO.getAccountIfAvailable(email, password);

        // *** updating the view with values ***
        ((TextView) findViewById(R.id.account_name_text)).setText(account.getName());
        ((TextView) findViewById(R.id.account_email_text)).setText(sharedPreferences.getString(Account.EMAIL, ""));
        ((TextView) findViewById(R.id.account_phone_number_text)).setText(account.getPhoneNumber());
        if (account.getDeliveryAddress().equals(""))
        {
            ((TextView) findViewById(R.id.account_address_text)).setText("Address not set yet.");
        }
        else
        {
            ((TextView) findViewById(R.id.account_address_text)).setText(account.getDeliveryAddress());
        }



        // *** setting click listeners for elements ***
        (findViewById(R.id.edit_address_action)).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        (findViewById(R.id.edit_address_action)).setVisibility(View.GONE);
                        (findViewById(R.id.account_address_text)).setVisibility(View.GONE);
                        (findViewById(R.id.account_address_edit)).setVisibility(View.VISIBLE);
                        (findViewById(R.id.edit_actions)).setVisibility(View.VISIBLE);
                    }
                }
        );

        (findViewById(R.id.logout_action)).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(ProfileActivity.this);
                        alertDialogBuilder.setTitle("");
                        alertDialogBuilder.setMessage("Do you want to logout?");

                        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                                setResult(MainActivity.REQUEST_CODE_LOGOUT, null);
                                finish();
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                            }
                        });
                        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        });

                        alertDialogBuilder.show();

                    }
                }
        );

        (findViewById(R.id.edit_cancel_action)).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(ProfileActivity.this);
                        alertDialogBuilder.setTitle("");
                        alertDialogBuilder.setMessage("Do you want to cancel?");

                        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                (findViewById(R.id.edit_address_action)).setVisibility(View.VISIBLE);
                                (findViewById(R.id.account_address_text)).setVisibility(View.VISIBLE);
                                (findViewById(R.id.account_address_edit)).setVisibility(View.GONE);
                                (findViewById(R.id.edit_actions)).setVisibility(View.GONE);
                            }
                        });
                        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        });

                        alertDialogBuilder.show();
                    }
                }
        );

        (findViewById(R.id.edit_save_action)).setOnClickListener(

                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        final String address = ((EditText) findViewById(R.id.account_address_edit)).getText().toString();
                        Toast.makeText(ProfileActivity.this, address, Toast.LENGTH_SHORT).show();
                        if (!address.equals(""))
                        {
                            android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(ProfileActivity.this);
                            alertDialogBuilder.setTitle("");
                            alertDialogBuilder.setMessage("Do you want to save this address for future deliveries?");

                            alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {

                                    if (zohokartDAO.updateAddressForAccount(address, email))
                                    {
                                        ((TextView) findViewById(R.id.account_address_text)).setText(address);
                                        getSnackbar("Delivery Address updated.");
                                        (findViewById(R.id.edit_address_action)).setVisibility(View.VISIBLE);
                                        (findViewById(R.id.account_address_text)).setVisibility(View.VISIBLE);
                                        (findViewById(R.id.account_address_edit)).setVisibility(View.GONE);
                                        (findViewById(R.id.edit_actions)).setVisibility(View.GONE);
                                    }
                                }
                            });
                            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.dismiss();
                                }
                            });

                            alertDialogBuilder.show();
                        }
                        else
                        {
                            getSnackbar("Please fill in address to save.");
                        }
                    }
                }
        );

        new SavedCardsListingAsyncTask().execute(email);

        (findViewById(R.id.new_card_action)).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        SavedCardFragment savedCardFragment = SavedCardFragment.getInstance(email);
                        savedCardFragment.setCommunicator(ProfileActivity.this);
                        stackFragment(savedCardFragment, ZohoKartFragments.SAVED_CARD_FRAGMENT);
                    }
                }
        );

        (findViewById(R.id.my_orders)).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        OrdersFragment ordersFragment = OrdersFragment.getInstance(email);
                        ordersFragment.setCommunicator(ProfileActivity.this);
                        stackFragment(ordersFragment, ZohoKartFragments.ORDERS_FRAGMENT);
                    }
                }
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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
        if (id == android.R.id.home)
        {
            if (((FrameLayout) findViewById(R.id.fragment_holder)).getChildCount() > 0)
            {
                fragmentManager.popBackStack();
            }
            else
            {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public Snackbar getSnackbar(String textToDisplay)
    {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.parent_view), textToDisplay, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();
        ((TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
        return snackbar;
    }

    @Override
    public void sendCard(final PaymentCard paymentCard)
    {
        zohokartDAO.addCard(paymentCard, email);
        recreate();
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
    public void openOrderLineItemsForOrderId(String orderId)
    {
        OrderLineItemsFragment orderLineItemsFragment = OrderLineItemsFragment.getInstance(orderId);
        stackFragment(orderLineItemsFragment, ZohoKartFragments.ORDER_LINE_ITEMS_FRAGMENT);
    }

    class SavedCardsListingAsyncTask extends AsyncTask<String, Void, List<PaymentCard>>
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            (findViewById(R.id.cards_loading)).setVisibility(View.VISIBLE);
            (findViewById(R.id.saved_cards_empty)).setVisibility(View.VISIBLE);
        }

        @Override
        protected List<PaymentCard> doInBackground(String... params)
        {
            cards = zohokartDAO.getCards(params[0]);
            return cards;
        }

        @Override
        protected void onPostExecute(final List<PaymentCard> paymentCards)
        {
            super.onPostExecute(paymentCards);
            if (paymentCards.size() > 0)
            {
                for (final PaymentCard paymentCard : paymentCards)
                {
                    card_item_view = (CardView) inflater.inflate(R.layout.saved_card_item_view, (ViewGroup) findViewById(R.id.account_card_holder), false);
                    ((TextView) card_item_view.findViewById(R.id.card_number)).setText("XXXX-XXXX-XXXX-" + paymentCard.getCardNumber().substring((paymentCard.getCardNumber().length()-4), paymentCard.getCardNumber().length()));
                    ((TextView) card_item_view.findViewById(R.id.card_type)).setText(paymentCard.getCardType());
                    card_item_view.setTag(paymentCard);
                    card_item_view.setId(CARD_ID + paymentCards.indexOf(paymentCard));
                    accountCardHolder.addView(card_item_view);
                }
                (findViewById(R.id.cards_loading)).setVisibility(View.GONE);
                (findViewById(R.id.saved_cards_empty)).setVisibility(View.GONE);
            }
            else
            {
                (findViewById(R.id.cards_loading)).setVisibility(View.GONE);
                (findViewById(R.id.saved_cards_empty)).setVisibility(View.VISIBLE);
            }
        }
    }

    private void stackFragment(Fragment fragment, String tag)
    {
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_out, R.anim.fade_in, R.anim.fade_out, R.anim.fade_in);
        fragmentTransaction.replace(R.id.fragment_holder, fragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

}
