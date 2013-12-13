package com.hbfav.android.core;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.hbfav.android.Constants;
import com.hbfav.android.model.HatenaApi;
import com.hbfav.android.model.ResultMyTags;
import com.hbfav.android.model.Tag;
import com.hbfav.android.ui.MainActivity;
import com.hbfav.android.util.IntegerMapComparator;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.exceptions.OAuthConnectionException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class UserInfoManager {
    private static final String ACCESS_TOKEN = "access_token";
    private static final String ACCESS_TOKEN_SECRET = "access_token_secret";
    private static boolean isOauthTwitter = false;
    private static boolean isOauthFacebook = false;
    private static boolean isOauthMixi = false;
    private static boolean isOauthEvernote = false;
    private static String[] myTags = new String[]{};

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

        return new Token(token, secret);
    }

    public static String getThumbUrl() {
        return Constants.BASE_THUMBNAIL_URL + getUserName() + "/profile.gif";
    }

    public static void refreshShareServiceAvailability() {
        new FetchShareConfigTask().execute();
    }

    public static void refreshMyTagsIfNeeded() {
        if (myTags == null || myTags.length == 0) {
            new FetchMyTagsTask().execute();
        }
    }

    public static void refreshMyTags() {
        new FetchMyTagsTask().execute();
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

    public static String[] getMyTags() {
        return myTags;
    }


    /** Async tasks **/

    public static class FetchShareConfigTask extends AsyncTask<Void, Void, Boolean> {
        final String endpoint = HatenaApi.MY_URL;

        @Override
        protected Boolean doInBackground(Void... params) {
            OAuthRequest request = new OAuthRequest(Verb.GET, endpoint);
            HatenaApiManager.getService().signRequest(UserInfoManager.getAccessToken(), request);
            org.scribe.model.Response response;
            try {
                response = request.send();
            } catch (OAuthConnectionException e) {
                e.printStackTrace();
                return false;
            }

            String res = response.getBody();
            if (res == null || res.isEmpty()) {
                return false;
            }

            try {
                JSONObject userObj = new JSONObject(res);
                isOauthTwitter = userObj.getBoolean("is_oauth_twitter");
                isOauthFacebook = userObj.getBoolean("is_oauth_facebook");
                isOauthMixi = userObj.getBoolean("is_oauth_mixi_check");
                isOauthEvernote = userObj.getBoolean("is_oauth_evernote");
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    private static class FetchMyTagsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            OAuthRequest request = new OAuthRequest(Verb.GET, HatenaApi.MY_TAGS_URL);
            HatenaApiManager.getService().signRequest(UserInfoManager.getAccessToken(), request);
            org.scribe.model.Response response;
            try {
                response = request.send();
            } catch (OAuthConnectionException e) {
                e.printStackTrace();
                return null;
            }

            String res = response.getBody();
            if (res == null || res.isEmpty() || response.getCode() != 200) {
                return null;
            }

            HashMap<String, Integer> tagsMap = new HashMap<String, Integer>();

            Gson gson = new Gson();
            ResultMyTags result = gson.fromJson(res, ResultMyTags.class);
            Tag[] tags = result.getTags();
            int length = tags.length > Constants.MAX_MY_TAG_COUNT ? Constants.MAX_MY_TAG_COUNT : tags.length;
            for (int i = 0; i < length; i++) {
                tagsMap.put(tags[i].getTag(), tags[i].getCount());
            }

            TreeMap<String, Integer> tagsTreeMap =
                    new TreeMap<String, Integer>(new IntegerMapComparator(tagsMap));
            tagsTreeMap.putAll(tagsMap);

            Set<String> tagsSet = tagsTreeMap.keySet();

            myTags = tagsSet.toArray(new String[length]);
            return null;
        }
    }
}
