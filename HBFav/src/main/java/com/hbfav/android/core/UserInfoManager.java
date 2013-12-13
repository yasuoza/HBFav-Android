package com.hbfav.android.core;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.hbfav.android.Constants;
import com.hbfav.android.model.HatenaApi;
import com.hbfav.android.ui.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.exceptions.OAuthConnectionException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;

public class UserInfoManager {
    private static final String ACCESS_TOKEN = "access_token";
    private static final String ACCESS_TOKEN_SECRET = "access_token_secret";
    private static boolean isOauthTwitter = false;
    private static boolean isOauthFacebook = false;
    private static boolean isOauthMixi = false;
    private static boolean isOauthEvernote = false;

    public static void setUserName(final String userName) {
        if (userName == null || userName.equals(getUserName())) {
            return;
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.getContextOfApplication());
        sp.edit().putString(Constants.getPrefUserName(), userName).apply();
    }

    public static String getUserName() {
        return PreferenceManager.getDefaultSharedPreferences(MainActivity.getContextOfApplication()).getString
                (Constants.getPrefUserName(), "");
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

    public static void refreshShareServiceAvailability() {
        final String endpoint = HatenaApi.MY_URL;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                OAuthRequest request = new OAuthRequest(Verb.GET, endpoint);
                HatenaApiManager.getService().signRequest(UserInfoManager.getAccessToken(), request);
                org.scribe.model.Response response;
                try {
                    response = request.send();
                } catch (OAuthConnectionException e) {
                    e.printStackTrace();
                    return null;
                }

                String res = response.getBody();
                if (res == null || res.isEmpty()) {
                    return null;
                }

                try {
                    JSONObject userObj = new JSONObject(res);
                    isOauthTwitter = userObj.getBoolean("is_oauth_twitter");
                    isOauthFacebook = userObj.getBoolean("is_oauth_facebook");
                    isOauthMixi = userObj.getBoolean("is_oauth_mixi_check");
                    isOauthEvernote = userObj.getBoolean("is_oauth_evernote");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public static boolean isOauthTwitter() {
        return isOauthTwitter;
    }

    public static boolean isOauthFacebook() {
        return isOauthFacebook;
    }

    public static boolean isOauthMixi() {
        return isOauthMixi;
    }

    public static boolean isOauthEvernote() {
        return isOauthEvernote;
    }
}
