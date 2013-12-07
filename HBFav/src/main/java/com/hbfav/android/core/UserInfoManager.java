package com.hbfav.android.core;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.hbfav.android.Constants;
import com.hbfav.android.ui.MainActivity;

import org.scribe.model.Token;

public class UserInfoManager {
    private static final String ACCESS_TOKEN = "access_token";
    private static final String ACCESS_TOKEN_SECRET = "access_token_secret";

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

    public static void setAccessToken(String token, String secret) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.getContextOfApplication());
        sp.edit().putString(ACCESS_TOKEN, token).apply();
        sp.edit().putString(ACCESS_TOKEN_SECRET, secret).apply();
    }

    public static Token getAccessToken() {
        String token = PreferenceManager.getDefaultSharedPreferences(MainActivity.getContextOfApplication()).getString(ACCESS_TOKEN, "");
        String secret = PreferenceManager.getDefaultSharedPreferences(MainActivity.getContextOfApplication()).getString(ACCESS_TOKEN_SECRET, "");

        return token.isEmpty() || secret.isEmpty() ? null : new Token(token, secret);
    }

    public static String getThumbUrl() {
        return Constants.BASE_THUMBNAIL_URL + getUserName() + "/profile.gif";
    }

}
