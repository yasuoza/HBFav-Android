package com.hbfav.android.controllers;

import com.hbfav.android.interfaces.FeedResponseHandler;
import com.hbfav.android.models.Entry;
import com.hbfav.android.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineFeedManager {
    private static TimelineFeedManager manager = new TimelineFeedManager();
    private ArrayList<Entry> bookmarks;

    public TimelineFeedManager() {
        bookmarks = new ArrayList<Entry>();
    }

    public static void add(Entry entry) {
        manager.bookmarks.add(entry);
    }

    public static void prepend(Entry entry) {
        manager.bookmarks.add(0, entry);
    }

    public static Entry get(Integer index) {
        return manager.bookmarks.get(index);
    }

    public static ArrayList<Entry> getList() {
        return manager.bookmarks;
    }

    public static void fetchFeed(String user, final FeedResponseHandler feedResponseHandler) {
        BookmarksFetcher.get(user, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject jObj) {
                try {
                    JSONArray bookmarks = jObj.getJSONArray("bookmarks");
                    for (int i = 0; i < bookmarks.length(); i++) {
                        JSONObject bookmark = (JSONObject) bookmarks.get(i);
                        String title = bookmark.getString("title");
                        String comment = bookmark.getString("comment");
                        String faviconUrl = bookmark.getString("favicon_url");
                        String created_at = bookmark.getString("created_at");
                        JSONObject jUser = bookmark.getJSONObject("user");
                        String uName = jUser.getString("name");
                        String uThumbUrl = jUser.getString("profile_image_url");
                        User user = new User(uName, uThumbUrl);
                        Entry entry = new Entry(
                                title,
                                comment,
                                faviconUrl,
                                "append_entry_link",
                                "entry_permalink",
                                created_at,
                                user
                        );
                        add(entry);
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
