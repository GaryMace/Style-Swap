package jameshassmallarms.com.styleswap.impl;

import com.google.firebase.database.DatabaseReference;

import jameshassmallarms.com.styleswap.infrastructure.FireBaseQueries;

/**
 * Created by gary on 24/10/16.
 */

public class User {
    FireBaseQueries fireBaseQueries = new FireBaseQueries();
    private String password;
    private String location;
    private String name;
    private String email;
    private int dressSize;
    private String phoneNum;
    private String bio;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDressSize() {
        return dressSize;
    }

    public void setDressSize(int dressSize) {
        this.dressSize = dressSize;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
