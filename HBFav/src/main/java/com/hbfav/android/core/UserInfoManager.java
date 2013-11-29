package com.hbfav.android.core;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.hbfav.android.Constants;
import com.hbfav.android.ui.MainActivity;

public class UserInfoManager {
    public static void setUserName(final String userName) {
        if (userName == null || userName.equals(getUserName())) {
            return;
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.getContextOfApplication());
        sp.edit().putString(Constants.PREF_USER_NAME, userName).apply();
    }

    public static String getUserName() {
        return PreferenceManager.getDefaultSharedPreferences(MainActivity.getContextOfApplication()).getString
                (Constants.PREF_USER_NAME, "");
    }

    public static String getThumbUrl() {
        return Constants.BASE_THUMBNAIL_URL + getUserName() + "/profile.gif";
    }
}
