package com.hbfav.android.controllers;

import com.hbfav.android.interfaces.FeedResponseHandler;
import com.hbfav.android.models.Entry;
import com.hbfav.android.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class TimelineFeedManager {
    private static TimelineFeedManager manager = new TimelineFeedManager();
    private ArrayList<Entry> bookmarks;

    public TimelineFeedManager() {
        bookmarks = new ArrayList<Entry>();
    }

    public static void addAll(ArrayList<Entry> entries) {
        manager.bookmarks.addAll(entries);
        manager.bookmarks = new ArrayList<Entry>(new LinkedHashSet<Entry>(manager.bookmarks));
    }

    public static void prependAll(ArrayList<Entry> entries) {
        entries.addAll(manager.bookmarks);
        manager.bookmarks = new ArrayList<Entry>(new LinkedHashSet<Entry>(entries));
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
                        String title = bookmark.getString("title");
                        String comment = bookmark.getString("comment");
                        String faviconUrl = bookmark.getString("favicon_url");
                        String link = bookmark.getString("link");
                        String permaLink = bookmark.getString("permalink");
                        String created_at = bookmark.getString("created_at");
                        JSONObject jUser = bookmark.getJSONObject("user");
                        String uName = jUser.getString("name");
                        String uThumbUrl = jUser.getString("profile_image_url");
                        User user = new User(uName, uThumbUrl);
                        Entry entry = new Entry(
                                title,
                                comment,
                                faviconUrl,
                                link,
                                permaLink,
                                created_at,
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
