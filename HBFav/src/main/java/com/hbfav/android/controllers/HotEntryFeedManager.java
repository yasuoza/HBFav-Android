package com.hbfav.android.controllers;

import com.hbfav.R;
import com.hbfav.android.interfaces.FeedResponseHandler;
import com.hbfav.android.models.Entry;
import com.hbfav.android.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class HotEntryFeedManager {
    private static HotEntryFeedManager manager = new HotEntryFeedManager();
    private int category = R.id.option_category_menu_general;
    private ArrayList<Entry> bookmarks = new ArrayList<Entry>();

    public static void clearList() {
        manager.bookmarks = null;
        manager.bookmarks = new ArrayList<Entry>();
    }

    public static void replaceAll(ArrayList<Entry> entries) {
        manager.bookmarks = new ArrayList<Entry>(new LinkedHashSet<Entry>(entries));
    }

    public static Entry get(Integer index) {
        return manager.bookmarks.get(index);
    }

    public static ArrayList<Entry> getList() {
        return manager.bookmarks;
    }

    public static void setCategory(int category) {
        manager.category = category;
    }

    public static int getCategory() {
        return manager.category;
    }

    public static void replaceFeed(final FeedResponseHandler feedResponseHandler) {
        String endpoint = "hotentry";
        switch (manager.category) {
            case R.id.option_category_menu_social:
                endpoint += "?category=social";
                break;
            case R.id.option_category_menu_it:
                endpoint += "?category=it";
                break;
            case R.id.option_category_menu_economics:
                endpoint += "?category=economics";
                break;
            case R.id.option_category_menu_life:
                endpoint += "?category=life";
                break;
            case R.id.option_category_menu_entertainment:
                endpoint += "?category=entertainment";
                break;
            case R.id.option_category_menu_knowledge:
                endpoint += "?category=knowledge";
                break;
            case R.id.option_category_menu_game:
                endpoint += "?category=geme";
                break;
            case R.id.option_category_menu_fun:
                endpoint += "?category=fun";
                break;
        }
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
                    replaceAll(entries);
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
