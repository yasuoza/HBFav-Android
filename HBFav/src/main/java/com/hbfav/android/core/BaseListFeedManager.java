package com.hbfav.android.core;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hbfav.android.Constants;
import com.hbfav.android.model.Entry;
import com.hbfav.android.model.Feed;
import com.hbfav.android.ui.MainActivity;
import com.hbfav.android.util.gson.DateTimeTypeConverter;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public abstract class BaseListFeedManager {
    public abstract void setList(ArrayList<Entry> entries);

    public abstract ArrayList<Entry> getList();

    public abstract void setCategory(int category);

    public abstract int getCategory();

    protected abstract String getEndPoint();


    public Entry get(Integer index) {
        return getList().get(index);
    }

    public void clearList() {
        setList(new ArrayList<Entry>());
    }

    public void replaceFeed(final FeedResponseHandler feedResponseHandler) {
        String endpoint = getEndPoint();
        int categoryIndex = getCategory();
        if (categoryIndex < 0 || categoryIndex >= Constants.CATEGORIES.length) {
            return;
        }
        if (categoryIndex != 0) {
            endpoint += "?category=" + Constants.CATEGORIES[categoryIndex].toLowerCase();
        }
        MainActivity.getRequestQueue().add(new HBFavAPIStringRequest(Request.Method.GET, endpoint,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Feed feed = entryGson().fromJson(response, Feed.class);
                        ArrayList<Entry> entries = new ArrayList<Entry>(new LinkedHashSet<Entry>(feed.getBookmarks()));
                        setList(entries);
                        feedResponseHandler.onSuccess();
                        feedResponseHandler.onFinish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        feedResponseHandler.onFinish();
                    }
                }
        ));
    }

    private Gson entryGson() {
        return new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter())
                .create();
    }
}
