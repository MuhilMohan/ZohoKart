package com.muhil.zohokart.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.muhil.zohokart.MainActivity;
import com.muhil.zohokart.R;
import com.muhil.zohokart.models.Account;
import com.muhil.zohokart.utils.DBHelper;
import com.muhil.zohokart.utils.EmailValidator;
import com.muhil.zohokart.utils.ZohoKartSharePreferences;
import com.muhil.zohokart.utils.ZohokartDAO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity
{

    public static int SIGNUP_CODE = 2000;

    ZohokartDAO zohokartDAO;
    Gson gson;

    SharedPreferences loggedAccountHolder;
    SharedPreferences.Editor editor;
    ImageButton closeButton;
    TextView hideTextButton, showTextButton;
    EditText email, password;
    String emailString, passwordString;
    Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        gson = new Gson();

        closeButton = (ImageButton) findViewById(R.id.closeButton);
        hideTextButton = (TextView) findViewById(R.id.hideButton);
        showTextButton = (TextView) findViewById(R.id.showButton);
        password = (EditText) findViewById(R.id.password);
        email = (EditText) findViewById(R.id.email);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getIntent().getIntExtra("request_code", 0) == CheckoutActivity.REQUEST_LOGIN)
                {
                    setResult(CheckoutActivity.REQUEST_LOGIN, getIntent().putExtra(Account.EMAIL, ""));
                }
                else if (getIntent().getIntExtra("request_code", 0) == MainActivity.REQUEST_CODE_LOGIN_FROM_PRODUCT_DETAIL)
                {
                    setResult(MainActivity.REQUEST_CODE_LOGIN_FROM_PRODUCT_DETAIL, getIntent().putExtra(Account.EMAIL, ""));
                }
                else if (getIntent().getIntExtra("request_code", 0) == MainActivity.REQUEST_CODE_LOGIN_FROM_WISHLIST)
                {
                    setResult(MainActivity.REQUEST_CODE_LOGIN_FROM_WISHLIST, getIntent().putExtra(Account.EMAIL, ""));
                }
                else
                {
                    setResult(MainActivity.REQUEST_CODE_LOGIN, getIntent().putExtra(Account.EMAIL, ""));
                }
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        hideTextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                hideTextButton.setVisibility(View.GONE);
                showTextButton.setVisibility(View.VISIBLE);
                password.setSelection(password.getText().length());
            }
        });

        showTextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                showTextButton.setVisibility(View.GONE);
                hideTextButton.setVisibility(View.VISIBLE);
                password.setSelection(password.getText().length());
            }
        });

        password.addTextChangedListener(new TextWatcher()

        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

                if ( s.length() != 0 )
                {
                    if( showTextButton.getVisibility() == View.VISIBLE )
                    {
                        showTextButton.setVisibility(View.VISIBLE);
                        hideTextButton.setVisibility(View.GONE);
                    }
                    else if (hideTextButton.getVisibility() == View.VISIBLE)
                    {
                        showTextButton.setVisibility(View.GONE);
                        hideTextButton.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        showTextButton.setVisibility(View.VISIBLE);
                        hideTextButton.setVisibility(View.GONE);
                    }
                }
                else
                {
                    showTextButton.setVisibility(View.GONE);
                    hideTextButton.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    public void onSignUpClicked(View view)
    {
        startActivityForResult(new Intent(LoginActivity.this, RegistrationActivity.class).putExtra("request_code", SIGNUP_CODE), SIGNUP_CODE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGNUP_CODE)
        {
            if (resultCode == SIGNUP_CODE)
            {
                if (getIntent().getIntExtra("request_code", 0) == CheckoutActivity.REQUEST_LOGIN)
                {
                    setResult(CheckoutActivity.REQUEST_LOGIN, getIntent().putExtra(Account.EMAIL, data.getStringExtra(Account.EMAIL)));
                }
                else if (getIntent().getIntExtra("request_code", 0) == MainActivity.REQUEST_CODE_LOGIN_FROM_PRODUCT_DETAIL)
                {
                    setResult(MainActivity.REQUEST_CODE_LOGIN_FROM_PRODUCT_DETAIL, getIntent().putExtra(Account.EMAIL, data.getStringExtra(Account.EMAIL)));
                }
                else if (getIntent().getIntExtra("request_code", 0) == MainActivity.REQUEST_CODE_LOGIN_FROM_WISHLIST)
                {
                    setResult(MainActivity.REQUEST_CODE_LOGIN_FROM_WISHLIST, getIntent().putExtra(Account.EMAIL, data.getStringExtra(Account.EMAIL)));
                }
                else
                {
                    setResult(MainActivity.REQUEST_CODE_LOGIN, getIntent().putExtra(Account.EMAIL, data.getStringExtra(Account.EMAIL)));
                }
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        }
    }

    public void onLoginClicked(View view)
    {

        emailString = email.getText().toString();
        passwordString = password.getText().toString();
        
        zohokartDAO = new ZohokartDAO(this);

        if((emailString != null && !emailString.equals("")) && (!passwordString.equals("")))
        {

            if(EmailValidator.validateEmail(emailString))
            {

                if (passwordString.length() > 4)
                {
                    if(zohokartDAO.hasAccount(emailString) )
                    {
                        if ((account = zohokartDAO.getAccountIfAvailable(emailString, passwordString)) != null)
                        {
                            Toast.makeText(LoginActivity.this, "Login successful.", Toast.LENGTH_SHORT).show();
                            loggedAccountHolder = getSharedPreferences(ZohoKartSharePreferences.LOGGED_ACCOUNT, MODE_PRIVATE);
                            editor = loggedAccountHolder.edit();
                            editor.putString(Account.EMAIL, account.getEmail());
                            editor.putString(Account.PASSWORD, account.getPassword());
                            editor.putString(Account.NAME, account.getName());
                            editor.apply();
                            if (getIntent().getIntExtra("request_code", 0) == CheckoutActivity.REQUEST_LOGIN)
                            {
                                setResult(CheckoutActivity.REQUEST_LOGIN, getIntent().putExtra(Account.EMAIL, account.getEmail()));
                            }
                            else if (getIntent().getIntExtra("request_code", 0) == MainActivity.REQUEST_CODE_LOGIN_FROM_PRODUCT_DETAIL)
                            {
                                setResult(MainActivity.REQUEST_CODE_LOGIN_FROM_PRODUCT_DETAIL, getIntent().putExtra(Account.EMAIL, account.getEmail()));
                            }
                            else if (getIntent().getIntExtra("request_code", 0) == MainActivity.REQUEST_CODE_LOGIN_FROM_WISHLIST)
                            {
                                setResult(MainActivity.REQUEST_CODE_LOGIN_FROM_WISHLIST, getIntent().putExtra(Account.EMAIL, account.getEmail()));
                            }
                            else
                            {
                                setResult(MainActivity.REQUEST_CODE_LOGIN, getIntent().putExtra(Account.EMAIL, account.getEmail()));
                            }
                            finish();
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "Login unsuccessful, possible detail mismatch.", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Account does not exist.", Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Password too short.", Toast.LENGTH_SHORT).show();
                }

            }
            else
            {

                Toast.makeText(LoginActivity.this, "Please enter a valid email id.", Toast.LENGTH_SHORT).show();
                
            }
            
        }
        else
        {
            Toast.makeText(LoginActivity.this, "Please fill in all the details.", Toast.LENGTH_SHORT).show();
        }

    }
}
