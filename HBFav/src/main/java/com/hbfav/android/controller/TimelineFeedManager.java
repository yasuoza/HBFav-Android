package com.hbfav.android.controller;

import com.hbfav.android.interfaces.FeedResponseHandler;
import com.hbfav.android.model.Entry;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class TimelineFeedManager {
    private static TimelineFeedManager manager = new TimelineFeedManager();
    private ArrayList<Entry> bookmarks = new ArrayList<Entry>();

    public static void addAll(ArrayList<Entry> entries) {
        manager.bookmarks.addAll(entries);
        manager.bookmarks = new ArrayList<Entry>(new LinkedHashSet<Entry>(manager.bookmarks));
    }

    public static void prependAll(ArrayList<Entry> entries) {
        entries.addAll(manager.bookmarks);
        Integer boundary = entries.size();
        manager.bookmarks = new ArrayList<Entry>(new LinkedHashSet<Entry>(entries));

        // 配列の長さが同じ
        // => 重複がない
        // => 今回と前回のフェッチ間にまだフェッチしていないブックマークがあるかもしれない
        if (boundary == manager.bookmarks.size()) {
            manager.bookmarks.add(boundary, Entry.newPlaceholder(entries.get(boundary - 1).getDateTime()));
        }

        // To test, uncomment this line
        // manager.bookmarks.add(2, Entry.newPlaceholder(entries.get(1).getDateTime()));
    }

    public static void insertAll(int position, ArrayList<Entry> entries) {
        Integer boundary = entries.size();
        manager.bookmarks.remove(position);
        manager.bookmarks.addAll(position, entries);
        manager.bookmarks = new ArrayList<Entry>(new LinkedHashSet<Entry>(manager.bookmarks));

        // 配列の長さが同じ
        // => 重複がない
        // => 今回と前回のフェッチ間にまだフェッチしていないブックマークがあるかもしれない
        if (boundary == manager.bookmarks.size()) {
            manager.bookmarks.add(boundary, Entry.newPlaceholder(entries.get(boundary - 1).getDateTime()));
        }
    }

    public static Entry get(Integer index) {
        return manager.bookmarks.get(index);
    }

    public static ArrayList<Entry> getList() {
        return manager.bookmarks;
    }

    public static void fetchFeed(final boolean prepend, final FeedResponseHandler feedResponseHandler) {
        String endpoint = prepend ? getUser() : getAppendUrl();
        BookmarksFetcher.get(endpoint, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject jObj) {
                try {
                    ArrayList<Entry> entries = new ArrayList<Entry>();
                    JSONArray bookmarkJsons = jObj.getJSONArray("bookmarks");
                    for (int i = 0; i < bookmarkJsons.length(); i++) {
                        Entry entry = new Entry((JSONObject) bookmarkJsons.get(i));
                        entries.add(entry);
                    }
                    entries = new ArrayList<Entry>(new LinkedHashSet<Entry>(entries));
                    if (prepend) {
                        prependAll(entries);
                    } else {
                        addAll(entries);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                feedResponseHandler.onSuccess();
            }

            @Override
            public void onFinish() {
                feedResponseHandler.onFinish();
            }
        });
    }

    public static void fetchFeed(final Integer position, final FeedResponseHandler feedResponseHandler) {
        BookmarksFetcher.get(getAppendUrlFrom(get(position).getDateTime()), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject jObj) {
                try {
                    ArrayList<Entry> entries = new ArrayList<Entry>();
                    JSONArray bookmarkJsons = jObj.getJSONArray("bookmarks");
                    for (int i = 0; i < bookmarkJsons.length(); i++) {
                        Entry entry = new Entry((JSONObject) bookmarkJsons.get(i));
                        entries.add(entry);
                    }
                    entries = new ArrayList<Entry>(new LinkedHashSet<Entry>(entries));
                    insertAll(position, entries);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                feedResponseHandler.onSuccess();
            }

            @Override
            public void onFinish() {
                feedResponseHandler.onFinish();
            }
        });
    }


    private static String getAppendUrlFrom(final DateTime dateTime) {
        return  getUser() + "?until=" + dateTime.getMillis() / 1000l;
    }

    private static String getAppendUrl() {
        final ArrayList<Entry> entries = getList();
        String user = getUser();
        if (entries.size() > 0) {
            return getAppendUrlFrom(entries.get(entries.size() - 1).getDateTime());
        }
        return user;
    }

    private static String getUser() {
        return "YasuOza";
    }
}
