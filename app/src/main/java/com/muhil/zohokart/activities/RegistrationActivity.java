package com.muhil.zohokart.activities;

import android.app.DialogFragment;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.muhil.zohokart.R;
import com.muhil.zohokart.fragments.DatePickerFragment;
import com.muhil.zohokart.models.Account;
import com.muhil.zohokart.utils.DBHelper;
import com.muhil.zohokart.utils.EmailValidator;
import com.muhil.zohokart.utils.ZohokartDAO;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationActivity extends AppCompatActivity
{

    ZohokartDAO zohokartDAO;
    Gson gson;

    EditText name, email, password, phoneNumber;
    TextView dateOfBirth;
    ImageButton closeButton;
    Button registrationButton;
    boolean registrationResult;
    String nameString, emailString, passwordString, phoneNumberString, dateOfBirthString;
    TextView hideTextButton, showTextButton;

    SharedPreferences loggedAccountHolder;
    SharedPreferences.Editor editor;

    public String preferenceName = "logged_account";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        gson = new Gson();

        hideTextButton = (TextView) findViewById(R.id.hideButton);
        showTextButton = (TextView) findViewById(R.id.showButton);

        hideTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                hideTextButton.setVisibility(View.GONE);
                showTextButton.setVisibility(View.VISIBLE);
                password.setSelection(password.getText().length());
            }
        });

        showTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                showTextButton.setVisibility(View.GONE);
                hideTextButton.setVisibility(View.VISIBLE);
                password.setSelection(password.getText().length());
            }
        });

        closeButton = (ImageButton) findViewById(R.id.closeButton);
        registrationButton = (Button) findViewById(R.id.registrationButton);
        name = (EditText) findViewById(R.id.accountName);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        dateOfBirth = (TextView) findViewById(R.id.dateOfBirth);

        password.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

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

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setResult(LoginActivity.SIGNUP_CODE, getIntent().putExtra(Account.EMAIL, ""));
                finish();
            }
        });

        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                zohokartDAO = new ZohokartDAO(RegistrationActivity.this);
                Account account = new Account();

                nameString = name.getText().toString();
                emailString = email.getText().toString();
                passwordString = password.getText().toString();
                phoneNumberString = phoneNumber.getText().toString();
                dateOfBirthString = dateOfBirth.getText().toString();

                if((nameString!=null && !nameString.equals("")) && (emailString!=null && !emailString.equals("")) && (passwordString!=null && !passwordString.equals("")) &&
                (phoneNumberString!=null && !phoneNumberString.equals("")) && (!dateOfBirthString.equals("")))
                {

                    if(EmailValidator.validateEmail(emailString))
                    {

                        if (passwordString.length() > 4)
                        {
                            if (phoneNumberString.length() == 10)
                            {
                                if(!zohokartDAO.hasAccount(emailString))
                                {
                                    account.setName(nameString);
                                    account.setEmail(emailString);
                                    account.setPassword(passwordString);
                                    account.setPhoneNumber(phoneNumberString);
                                    account.setDateOfBirth(dateOfBirthString);
                                    registrationResult = zohokartDAO.addAccount(account);

                                    if(registrationResult)
                                    {
                                        loggedAccountHolder = getSharedPreferences(preferenceName, MODE_PRIVATE);
                                        editor = loggedAccountHolder.edit();

                                        editor.putString(Account.EMAIL, account.getEmail());
                                        editor.putString(Account.PASSWORD, account.getPassword());
                                        editor.putString(Account.NAME, account.getName());

                                        editor.apply();

                                        setResult(LoginActivity.SIGNUP_CODE, getIntent().putExtra(Account.EMAIL, account.getEmail()));
                                        finish();
                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    }
                                    else
                                    {
                                        Toast.makeText(RegistrationActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else
                                {
                                    Toast.makeText(RegistrationActivity.this, "Email already registered", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(RegistrationActivity.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(RegistrationActivity.this, "Password too short", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(RegistrationActivity.this, "Please enter a valid email id", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(RegistrationActivity.this, "Please fill in all the details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration, menu);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void OnShowCalendarClicked(View view)
    {
        DialogFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getFragmentManager(), "DatePicker");
    }

    public void onSigninClicked(View view)
    {
        finish();
    }
}
