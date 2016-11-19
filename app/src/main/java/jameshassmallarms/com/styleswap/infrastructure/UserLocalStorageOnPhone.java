package jameshassmallarms.com.styleswap.infrastructure;

import android.content.Context;
import android.content.SharedPreferences;

import jameshassmallarms.com.styleswap.impl.User;

/**
 * Created by Mark_2 on 18/11/2016.
 */

public class UserLocalStorageOnPhone {
    public static final String SP_NAME = "userDetails";
    SharedPreferences userLocalDatabase;

    public UserLocalStorageOnPhone(Context context){
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeUserData(User user){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putString("name", user.getName());
        spEditor.putInt("age", user.getAge());
        spEditor.putString("password", user.getPassword());
        spEditor.putString("email", user.getEmail());
        spEditor.putInt("Dress Size", user.getDressSize());
        spEditor.commit();
    }

    public User getLoggedInUser(){
        String name = userLocalDatabase.getString("name", "");
        String password = userLocalDatabase.getString("password", "");
        String email = userLocalDatabase.getString("email", "");
        String location = userLocalDatabase.getString("location", "");
        int age = userLocalDatabase.getInt("age", -1);
        int dressSize = userLocalDatabase.getInt("Dress Size", -1);

        User storedUser = new User(name, age, email, password, dressSize);
        return storedUser;
    }

    public void setUserLoggedIn(boolean loggedIn){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putBoolean("loggedIn", loggedIn);
        spEditor.commit();
    }

    public void clearUserData(){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.clear();
        spEditor.commit();
    }

    public boolean getUserLoggedIn(){
        if(userLocalDatabase.getBoolean("loggedIn", false) == true){
            return true;
        }else{
            return false;
        }
    }
}