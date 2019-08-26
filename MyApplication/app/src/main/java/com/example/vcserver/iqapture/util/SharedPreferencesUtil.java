package com.example.vcserver.iqapture.util;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * @author 36483  存储
 */
public class SharedPreferencesUtil {
    private static SharedPreferencesUtil sInstances;
    private SharedPreferences mSharedPreferences;
    private static final String DB_BASE_NAME = "fanyu_file";


    public static SharedPreferencesUtil getsInstances(Context context) {
        if (sInstances == null) {
            synchronized (SharedPreferencesUtil.class) {
                if (sInstances == null) {
                    sInstances = new SharedPreferencesUtil(context);
                }
            }
        }
        return sInstances;
    }


    private SharedPreferencesUtil(Context context) {
        mSharedPreferences = context.getSharedPreferences(DB_BASE_NAME,
                Context.MODE_PRIVATE);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void putInt(String key, int value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public String getString(String key, String defaultValue) {
        return mSharedPreferences.getString(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return mSharedPreferences.getInt(key, defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return mSharedPreferences.getBoolean(key, defaultValue);
    }



}
