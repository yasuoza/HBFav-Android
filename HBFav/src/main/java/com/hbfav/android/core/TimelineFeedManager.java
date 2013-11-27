package com.hbfav.android.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hbfav.android.model.Entry;
import com.hbfav.android.model.Feed;
import com.hbfav.android.util.gson.DateTimeTypeConverter;
import com.hbfav.android.util.gson.TimelineExclusionStrategy;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
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

    public static void fetchFeed(final boolean prepend, final FeedResponseHandler feedResponseHandler) {
        manager.appendingBookmarks = !prepend;
        String endpoint = prepend ? UserInfoManager.getUserName() : getAppendUrl();
        HBFavFetcher.get(endpoint, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] bytes) {
                String json = new String(bytes);
                Feed feed = manager.timelineGson().fromJson(json, Feed.class);
                ArrayList<Entry> entries = new ArrayList<Entry>(new LinkedHashSet<Entry>(feed.getBookmarks()));
                if (prepend) {
                    prependAll(entries);
                } else {
                    addAll(entries);
                }
                feedResponseHandler.onSuccess();
            }

            @Override
            public void onFinish() {
                feedResponseHandler.onFinish();
                manager.appendingBookmarks = false;
            }
        });
    }

    public static void fetchFeed(final Integer position, final FeedResponseHandler feedResponseHandler) {
        HBFavFetcher.get(getAppendUrlFrom(get(position).getDateTime()), null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] bytes) {
                String json = new String(bytes);
                Feed feed = manager.timelineGson().fromJson(json, Feed.class);
                ArrayList<Entry> entries = new ArrayList<Entry>(new LinkedHashSet<Entry>(feed.getBookmarks()));
                insertAll(position, entries);
                feedResponseHandler.onSuccess();
            }

            @Override
            public void onFinish() {
                feedResponseHandler.onFinish();
            }
        });
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

    private static String getAppendUrl() {
        final ArrayList<Entry> entries = getList();
        String user = UserInfoManager.getUserName();
        if (entries.size() > 0) {
            return getAppendUrlFrom(entries.get(entries.size() - 1).getDateTime());
        }
        return user;
    }

    private Gson timelineGson() {
        return new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter())
                .setExclusionStrategies(new TimelineExclusionStrategy())
                .create();
    }
}
