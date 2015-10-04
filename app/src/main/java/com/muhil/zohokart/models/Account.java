package com.muhil.zohokart.models;

/**
 * Created by muhil-ga42 on 01/10/15.
 */
public class Account {

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
