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
    private static TimelineFeedManager manager = new TimelineFeedManager();
    private ArrayList<Entry> bookmarks = new ArrayList<Entry>();
    private boolean appendingBookmarks = false;
    private boolean loadedAllBookmarks = false;

    public static void clearAll() {
        manager.bookmarks = new ArrayList<Entry>();
    }

    public static Entry get(Integer index) {
        return manager.bookmarks.get(index);
    }

    public static ArrayList<Entry> getList() {
        return manager.bookmarks;
    }

    public static void fetchFeed(String endpoint, final boolean prepend, final FeedResponseHandler feedResponseHandler) {
        manager.appendingBookmarks = !prepend;
        MainActivity.getRequestQueue().add(new HBFavAPIStringRequest(Request.Method.GET, endpoint,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Feed feed = manager.timelineGson().fromJson(response, Feed.class);
                        ArrayList<Entry> entries = new ArrayList<Entry>(new LinkedHashSet<Entry>(feed.getBookmarks()));
                        if (prepend) {
                            prependAll(entries);
                        } else {
                            addAll(entries);
                        }
                        feedResponseHandler.onSuccess();
                        feedResponseHandler.onFinish();
                        manager.appendingBookmarks = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        feedResponseHandler.onFinish();
                        manager.appendingBookmarks = false;
                    }
                }
        ));
    }

    public static void fetchFeed(final boolean prepend, final FeedResponseHandler feedResponseHandler) {
        String endpoint = prepend ? getPrependUrl() : getAppendUrl();
        fetchFeed(endpoint, prepend, feedResponseHandler);
    }

    public static void fetchFeed(final Integer position, final FeedResponseHandler feedResponseHandler) {
        String endpoint = Constants.HBFAV_BASE_URL + getAppendUrlFrom(get(position).getDateTime());
        MainActivity.getRequestQueue().add(new HBFavAPIStringRequest(Request.Method.GET, endpoint,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Feed feed = manager.timelineGson().fromJson(response, Feed.class);
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

    public static boolean isAppending() {
        return manager.appendingBookmarks;
    }

    public static boolean loadedAllBookmarks() {
        return manager.loadedAllBookmarks;
    }

    private static void addAll(ArrayList<Entry> entries) {
        Integer beforeCount = manager.bookmarks.size();
        manager.bookmarks.addAll(entries);
        manager.bookmarks = new ArrayList<Entry>(new LinkedHashSet<Entry>(manager.bookmarks));
        if (beforeCount == manager.bookmarks.size()) {
            manager.loadedAllBookmarks = true;
        }
    }

    private static void prependAll(ArrayList<Entry> entries) {
        Integer boundary = entries.size();
        entries.addAll(manager.bookmarks);
        manager.bookmarks = new ArrayList<Entry>(new LinkedHashSet<Entry>(entries));

        // 配列の長さが同じ
        // => 重複がない
        // => 今回と前回のフェッチ間にまだフェッチしていないブックマークがあるかもしれない
        if (entries.size() == manager.bookmarks.size()) {
            manager.bookmarks.add(boundary, Entry.newPlaceholder(entries.get(boundary - 1).getDateTime()));
        }

        // To test, uncomment this line
        // manager.bookmarks.add(2, Entry.newPlaceholder(entries.get(1).getDateTime()));
    }

    private static void insertAll(int position, ArrayList<Entry> entries) {
        manager.bookmarks.remove(position);
        manager.bookmarks.addAll(position, entries);
        Integer boundary = manager.bookmarks.size();
        manager.bookmarks = new ArrayList<Entry>(new LinkedHashSet<Entry>(manager.bookmarks));

        // 配列の長さが同じ
        // => 重複がない
        // => 今回と前回のフェッチ間にまだフェッチしていないブックマークがあるかもしれない
        if (boundary == manager.bookmarks.size()) {
            manager.bookmarks.add(position + entries.size(), Entry.newPlaceholder(entries.get(entries.size() - 1)
                    .getDateTime()));
        }
    }

    private static String getAppendUrlFrom(final DateTime dateTime) {
        return UserInfoManager.getUserName() + "?until=" + dateTime.getMillis() / 1000l;
    }

    private static String getPrependUrl() {
        return Constants.HBFAV_BASE_URL + UserInfoManager.getUserName();
    }

    private static String getAppendUrl() {
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
