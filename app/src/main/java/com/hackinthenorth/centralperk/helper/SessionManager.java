package com.hackinthenorth.centralperk.helper;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.hackinthenorth.centralperk.entity.Constants;


public class SessionManager {

    // Shared preferences file name
    private static final String PREF_NAME = Constants.mAppTitle;
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    public static SessionManager mInstance;
    private static String TAG = SessionManager.class.getSimpleName();
    private static Context mContext;
    // Shared pref mode
    int PRIVATE_MODE = 0;
    // Shared Preferences editor
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private SessionManager(Context context) {
        mContext = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (mInstance == null)
            mInstance = new SessionManager(context);
        return mInstance;
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        // commit changes
        editor.commit();
        Log.d(TAG, "User Login session modified!");
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

}
