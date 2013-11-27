package com.hbfav.android.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hbfav.android.Constants;
import com.hbfav.android.model.Entry;
import com.hbfav.android.model.Feed;
import com.hbfav.android.util.gson.DateTimeTypeConverter;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
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
        HBFavFetcher.get(endpoint, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] bytes) {
                String json = new String(bytes);
                Feed feed = entryGson().fromJson(json, Feed.class);
                ArrayList<Entry> entries = new ArrayList<Entry>(new LinkedHashSet<Entry>(feed.getBookmarks()));
                setList(entries);
                feedResponseHandler.onSuccess();
            }

            @Override
            public void onFinish() {
                feedResponseHandler.onFinish();
            }
        });
    }

    private Gson entryGson() {
        return new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter())
                .create();
    }
}
