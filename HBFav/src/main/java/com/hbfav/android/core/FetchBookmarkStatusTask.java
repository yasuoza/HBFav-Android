package com.hbfav.android.core;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.hbfav.android.model.Entry;
import com.hbfav.android.model.HatenaApi;
import com.hbfav.android.model.HatenaBookmark;

import org.scribe.exceptions.OAuthConnectionException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

public class FetchBookmarkStatusTask extends AsyncTask<Entry, Void, HatenaBookmark> {
    @Override
    protected HatenaBookmark doInBackground(Entry... entries) {
        OAuthRequest request = new OAuthRequest(Verb.GET, HatenaApi.BOOKMARK_URL);
        request.addQuerystringParameter("url", entries[0].getLink());
        HatenaApiManager.getService().signRequest(UserInfoManager.getAccessToken(), request);
        org.scribe.model.Response response;
        try {
            response = request.send();
        } catch (OAuthConnectionException e) {
            e.printStackTrace();
            return null;
        }

        boolean success = (response.getCode() == 200);
        if (!success) {
            return null;
        }

        Gson gson = new Gson();
        return gson.fromJson(response.getBody(), HatenaBookmark.class);
    }
}
