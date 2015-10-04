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

import com.muhil.zohokart.R;
import com.muhil.zohokart.models.Account;
import com.muhil.zohokart.utils.DBHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    SharedPreferences loggedAccountHolder;
    SharedPreferences.Editor editor;
    ImageButton closeButton;
    TextView forgotTextButton, hideTextButton, showTextButton;
    EditText email, password;
    String emailString, passwordString;
    DBHelper dbHelper;
    Account account;

    String preferenceName = "logged_account";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

        hideTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                forgotTextButton.setVisibility(View.GONE);
                hideTextButton.setVisibility(View.GONE);
                showTextButton.setVisibility(View.VISIBLE);
                password.setSelection(password.getText().length());
            }
        });

        showTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                forgotTextButton.setVisibility(View.GONE);
                showTextButton.setVisibility(View.GONE);
                hideTextButton.setVisibility(View.VISIBLE);
                password.setSelection(password.getText().length());
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if ( s.length() != 0 ) {
                    if( showTextButton.getVisibility() == View.VISIBLE ){
                        forgotTextButton.setVisibility(View.GONE);
                        showTextButton.setVisibility(View.VISIBLE);
                        hideTextButton.setVisibility(View.GONE);
                    }
                    else if (hideTextButton.getVisibility() == View.VISIBLE){
                        forgotTextButton.setVisibility(View.GONE);
                        showTextButton.setVisibility(View.GONE);
                        hideTextButton.setVisibility(View.VISIBLE);
                    }
                    else {
                        forgotTextButton.setVisibility(View.GONE);
                        showTextButton.setVisibility(View.VISIBLE);
                        hideTextButton.setVisibility(View.GONE);
                    }
                }
                else {
                    forgotTextButton.setVisibility(View.VISIBLE);
                    showTextButton.setVisibility(View.GONE);
                    hideTextButton.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
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

        return super.onOptionsItemSelected(item);
    }

    public void onSignUpClicked(View view) {

        startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
        finish();

    }


    public void onLoginClicked(View view) {

        emailString = email.getText().toString();
        passwordString = password.getText().toString();
        
        dbHelper = new DBHelper(this);

        if((emailString != null && !emailString.equals("")) && (!passwordString.equals(""))) {

            int atPosition=emailString.indexOf('@');
            int lastDotPosition=emailString.lastIndexOf('.');

            if(atPosition==-1 || lastDotPosition==-1 || (atPosition+2)>=lastDotPosition){
                Toast.makeText(LoginActivity.this, "Please enter a valid email id.", Toast.LENGTH_SHORT).show();
            }
            else {
                
                if (passwordString.length() > 4){
                    
                    if(dbHelper.hasAccount(emailString)){
                        
                        if ((account = dbHelper.getAccountIfAvailable(emailString, passwordString)) != null){

                            Toast.makeText(LoginActivity.this, "Login successful.", Toast.LENGTH_SHORT).show();
                            loggedAccountHolder = getSharedPreferences(preferenceName, MODE_PRIVATE);
                            editor = loggedAccountHolder.edit();
                            JSONObject jsonObject = new JSONObject();

                            try {
                                jsonObject.put("name", account.getName());
                                jsonObject.put("email", account.getEmail());
                                jsonObject.put("password", account.getPassword());
                                jsonObject.put("phone_number", account.getPhoneNumber());
                                jsonObject.put("date_of_birth", account.getDateOfBirth());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            editor.putString("logged_account", jsonObject.toString());
                            editor.commit();
                            finish();
                            Toast.makeText(LoginActivity.this, "Account added to preferences.", Toast.LENGTH_SHORT).show();


                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Login unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                        
                    }
                    else {

                        Toast.makeText(LoginActivity.this, "Account does not exist.", Toast.LENGTH_SHORT).show();

                    }
                    
                }
                else {
                    Toast.makeText(LoginActivity.this, "Password too short.", Toast.LENGTH_SHORT).show();
                }
                
            }
            
        }
        else{

            Toast.makeText(LoginActivity.this, "Please fill in all the details.", Toast.LENGTH_SHORT).show();

        }

    }
}
