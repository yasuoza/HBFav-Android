package com.hbfav.android.core;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.hbfav.android.Constants;
import com.hbfav.android.ui.MainActivity;

public class UserInfoManager {

    public static void setUserName(final String userName) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.getContextOfApplication());
        sp.edit().putString(Constants.PREF_USER_NAME, userName).apply();
    }

    public static String getUserName() {
        return PreferenceManager.getDefaultSharedPreferences(MainActivity.getContextOfApplication()).getString(Constants.PREF_USER_NAME, "");
    }
}
