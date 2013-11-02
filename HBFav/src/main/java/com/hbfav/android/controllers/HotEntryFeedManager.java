package com.hbfav.android.controllers;

import android.util.Log;

import com.hbfav.android.Constants;
import com.hbfav.android.interfaces.FeedResponseHandler;
import com.hbfav.android.models.Entry;
import com.hbfav.android.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class HotEntryFeedManager {
    private static HotEntryFeedManager manager = new HotEntryFeedManager();
    private ArrayList<Entry> bookmarks = new ArrayList<Entry>();

    public static void addAll(ArrayList<Entry> entries) {
        manager.bookmarks.addAll(entries);
        manager.bookmarks = new ArrayList<Entry>(new LinkedHashSet<Entry>(manager.bookmarks));
    }

    public static void prependAll(ArrayList<Entry> entries) {
        entries.addAll(manager.bookmarks);
        manager.bookmarks = new ArrayList<Entry>(new LinkedHashSet<Entry>(entries));
        // Shrink bookmarks if needed
        if (manager.bookmarks.size() > Constants.MAX_FEED_CACHE_LENGTH) {
            manager.bookmarks.subList(Constants.MAX_FEED_CACHE_LENGTH, manager.bookmarks.size() - 1).clear();
        }
    }

    public static Entry get(Integer index) {
        return manager.bookmarks.get(index);
    }

    public static ArrayList<Entry> getList() {
        return manager.bookmarks;
    }

    public static void fetchFeed(String user, final boolean prepend, final FeedResponseHandler feedResponseHandler) {
        BookmarksFetcher.get(user, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject jObj) {
                try {
                    ArrayList<Entry> entries = new ArrayList<Entry>();
                    JSONArray bookmarks = jObj.getJSONArray("bookmarks");
                    for (int i = 0; i < bookmarks.length(); i++) {
                        JSONObject bookmark = (JSONObject) bookmarks.get(i);
                        JSONObject jUser = bookmark.getJSONObject("user");
                        User user = new User(jUser.getString("name"), jUser.getString("profile_image_url"));
                        Entry entry = new Entry(
                                bookmark.getString("title"),
                                bookmark.getString("comment"),
                                bookmark.getString("favicon_url"),
                                ISODateTimeFormat.dateTimeNoMillis().parseDateTime(bookmark.getString("datetime")),
                                bookmark.getString("link"),
                                bookmark.getString("permalink"),
                                user
                        );
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
}
