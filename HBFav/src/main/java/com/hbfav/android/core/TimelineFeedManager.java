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
import com.hbfav.android.util.gson.TimelineExclusionStrategy;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class TimelineFeedManager {
    private static TimelineFeedManager instance = new TimelineFeedManager();
    private final static String TAG = TimelineFeedManager.class.getSimpleName();
    private ArrayList<Entry> bookmarks = new ArrayList<Entry>();
    private boolean appendingBookmarks = false;
    private boolean loadedAllBookmarks = false;

    public static TimelineFeedManager getInstance() {
        if (instance == null) {
            instance = new TimelineFeedManager();
        }
        return instance;
    }

    public void clearList() {
        MainActivity.getRequestQueue().cancelAll(TAG);
        instance.appendingBookmarks = false;
        instance.loadedAllBookmarks = false;
        instance.bookmarks = new ArrayList<Entry>();
    }

    public void cancelAllRequest() {
        MainActivity.getRequestQueue().cancelAll(TAG);
        instance.appendingBookmarks = false;
    }

    public Entry get(Integer index) {
        return instance.bookmarks.get(index);
    }

    public ArrayList<Entry> getList() {
        return instance.bookmarks;
    }

    public void fetchFeed(String endpoint, final boolean prepend, final FeedResponseHandler feedResponseHandler) {
        instance.appendingBookmarks = !prepend;
        MainActivity.getRequestQueue().add(new HBFavAPIStringRequest(Request.Method.GET, endpoint, TAG,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Feed feed = instance.timelineGson().fromJson(response, Feed.class);
                        ArrayList<Entry> entries = new ArrayList<Entry>(new LinkedHashSet<Entry>(feed.getBookmarks()));
                        if (prepend) {
                            prependAll(entries);
                        } else {
                            addAll(entries);
                        }
                        feedResponseHandler.onSuccess();
                        feedResponseHandler.onFinish();
                        instance.appendingBookmarks = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        feedResponseHandler.onFinish();
                        instance.appendingBookmarks = false;
                    }
                }
        ));
    }

    public void fetchFeed(final boolean prepend, final FeedResponseHandler feedResponseHandler) {
        String endpoint = prepend ? getPrependUrl() : getAppendUrl();
        fetchFeed(endpoint, prepend, feedResponseHandler);
    }

    public void fetchFeed(final Integer position, final FeedResponseHandler feedResponseHandler) {
        String endpoint = Constants.HBFAV_BASE_URL + getAppendUrlFrom(get(position).getDateTime());
        MainActivity.getRequestQueue().add(new HBFavAPIStringRequest(Request.Method.GET, endpoint,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Feed feed = instance.timelineGson().fromJson(response, Feed.class);
                        ArrayList<Entry> entries = new ArrayList<Entry>(new LinkedHashSet<Entry>(feed.getBookmarks()));
                        insertAll(position, entries);
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

    public boolean isAppending() {
        return instance.appendingBookmarks;
    }

    public boolean loadedAllBookmarks() {
        return instance.loadedAllBookmarks;
    }

    private void addAll(ArrayList<Entry> entries) {
        Integer beforeCount = instance.bookmarks.size();
        instance.bookmarks.addAll(entries);
        instance.bookmarks = new ArrayList<Entry>(new LinkedHashSet<Entry>(instance.bookmarks));
        if (beforeCount == instance.bookmarks.size()) {
            instance.loadedAllBookmarks = true;
        }
    }

    private void prependAll(ArrayList<Entry> entries) {
        Integer boundary = entries.size();
        if (boundary == 0) {
            return;
        }

        entries.addAll(instance.bookmarks);
        instance.bookmarks = new ArrayList<Entry>(new LinkedHashSet<Entry>(entries));

        // 配列の長さが同じ
        // => 重複がない
        // => 今回と前回のフェッチ間にまだフェッチしていないブックマークがあるかもしれない
        if (entries.size() == instance.bookmarks.size()) {
            instance.bookmarks.add(boundary, Entry.newPlaceholder(entries.get(boundary - 1).getDateTime()));
        }

        // To test, uncomment this line
        // instance.bookmarks.add(2, Entry.newPlaceholder(entries.get(1).getDateTime()));
    }

    private void insertAll(int position, ArrayList<Entry> entries) {
        instance.bookmarks.remove(position);
        instance.bookmarks.addAll(position, entries);
        Integer boundary = instance.bookmarks.size();
        instance.bookmarks = new ArrayList<Entry>(new LinkedHashSet<Entry>(instance.bookmarks));

        // 配列の長さが同じ
        // => 重複がない
        // => 今回と前回のフェッチ間にまだフェッチしていないブックマークがあるかもしれない
        if (boundary == instance.bookmarks.size()) {
            instance.bookmarks.add(position + entries.size(), Entry.newPlaceholder(entries.get(entries.size() - 1)
                    .getDateTime()));
        }
    }

    private String getAppendUrlFrom(final DateTime dateTime) {
        return UserInfoManager.getUserName() + "?until=" + dateTime.getMillis() / 1000l;
    }

    private String getPrependUrl() {
        return Constants.HBFAV_BASE_URL + UserInfoManager.getUserName();
    }

    private String getAppendUrl() {
        final ArrayList<Entry> entries = getList();
        if (entries.size() > 0) {
            return Constants.HBFAV_BASE_URL + getAppendUrlFrom(entries.get(entries.size() - 1).getDateTime());
        }
        return getPrependUrl();
    }

    private Gson timelineGson() {
        return new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter())
                .setExclusionStrategies(new TimelineExclusionStrategy())
                .create();
    }
}
