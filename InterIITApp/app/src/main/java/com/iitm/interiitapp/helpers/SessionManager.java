package com.iitm.interiitapp.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import com.iitm.interiitapp.networking.LoginRequest;

public class SessionManager {

    private static final String PREF_NAME = "InterIITApp";
    private static final String IS_LOGGED_IN = "is_logged_in";
    private static final String STATE = "state";
    private static final String MOBILE = "mobile";
    private static final String PASSWORD = "password";

    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context){
        this.context = context;
        this.preferences = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = preferences.edit();
    }

    public boolean isLoggedIn(){
        return preferences.getBoolean(IS_LOGGED_IN, false);
    }

    public void saveLoginDetails(String mobile, String password, int state){
        editor.putString(PASSWORD, password);
        editor.putString(MOBILE, mobile);
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putInt(STATE, state);
        editor.commit();
    }

    public String getPassword() {
        return preferences.getString(PASSWORD, null);
    }

    public String getMobile() {
        return preferences.getString(MOBILE, null);
    }

    public int getState(){ return  preferences.getInt(STATE, LoginRequest.USER);}

    public void logout() {
        editor.putString(PASSWORD, null);
        editor.putString(MOBILE, null);
        editor.putBoolean(IS_LOGGED_IN, false);
        editor.putInt(STATE, LoginRequest.USER);
        editor.commit();
    }
}
