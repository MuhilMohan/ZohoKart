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

    ZohokartDAO zohokartDAO;
    Gson gson;

    SharedPreferences loggedAccountHolder;
    SharedPreferences.Editor editor;
    ImageButton closeButton;
    TextView forgotTextButton, hideTextButton, showTextButton;
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
        forgotTextButton = (TextView) findViewById(R.id.forgotButton);
        hideTextButton = (TextView) findViewById(R.id.hideButton);
        showTextButton = (TextView) findViewById(R.id.showButton);
        password = (EditText) findViewById(R.id.password);
        email = (EditText) findViewById(R.id.email);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        hideTextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                forgotTextButton.setVisibility(View.GONE);
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
                forgotTextButton.setVisibility(View.GONE);
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
                        forgotTextButton.setVisibility(View.GONE);
                        showTextButton.setVisibility(View.VISIBLE);
                        hideTextButton.setVisibility(View.GONE);
                    }
                    else if (hideTextButton.getVisibility() == View.VISIBLE)
                    {
                        forgotTextButton.setVisibility(View.GONE);
                        showTextButton.setVisibility(View.GONE);
                        hideTextButton.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        forgotTextButton.setVisibility(View.GONE);
                        showTextButton.setVisibility(View.VISIBLE);
                        hideTextButton.setVisibility(View.GONE);
                    }
                }
                else
                {
                    forgotTextButton.setVisibility(View.VISIBLE);
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
        startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
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
                            setResult(MainActivity.REQUEST_CODE_LOGIN, getIntent().putExtra(Account.EMAIL, account.getEmail()));
                            finish();
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            Toast.makeText(LoginActivity.this, "Account added to preferences.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "Login unsuccessful", Toast.LENGTH_SHORT).show();
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
