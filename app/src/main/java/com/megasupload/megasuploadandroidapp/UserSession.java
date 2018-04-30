package com.megasupload.megasuploadandroidapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class UserSession {
    public static final String PREFER_NAME = "Reg";
    public static final String IS_USER_LOGIN = "IsLoggedIn";
    public static final String KEY_NAME = "Name";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;
    private static int PRIVATE_MODE = 0;

    public UserSession(final Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = preferences.edit();
    }

    public void createUserLoginSession(final String name){
        editor.putBoolean(IS_USER_LOGIN,true);
        editor.putString(KEY_NAME, name);
        editor.commit();
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }

    public boolean isLoggedIn(){
        return preferences.getBoolean(IS_USER_LOGIN, false);
    }
}