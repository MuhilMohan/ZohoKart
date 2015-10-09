package com.muhil.zohokart.models;

import android.content.ContentResolver;
import android.net.Uri;

import com.muhil.zohokart.utils.ZohokartContentProvider;

/**
 * Created by muhil-ga42 on 01/10/15.
 */
public class Account {

    public static final String TABLE_NAME = "accounts";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String PHONE_NUMBER = "phone_number";
    public static final String DATE_OF_BIRTH = "date_of_birth";

    public static final String[] PROJECTION = {NAME, EMAIL, PASSWORD, PHONE_NUMBER, DATE_OF_BIRTH};

    public static final Uri CONTENT_URI = Uri.withAppendedPath(ZohokartContentProvider.CONTENT_URI, TABLE_NAME);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/com.muhil.zohokart.models.Account";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/com.muhil.zohokart.models.Account";

    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private String dateOfBirth;

    public Account() {
    }

    public Account(String dateOfBirth, String email, String name, String password, String phoneNumber) {
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.name = name;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
