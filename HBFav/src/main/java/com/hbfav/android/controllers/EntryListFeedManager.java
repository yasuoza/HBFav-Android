package com.hbfav.android.controllers;

import com.hbfav.R;
import com.hbfav.android.interfaces.FeedResponseHandler;
import com.hbfav.android.models.Entry;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class EntryListFeedManager extends BaseListFeedManager {
    private static EntryListFeedManager instance;
    private int category = R.id.option_category_menu_general;
    private ArrayList<Entry> bookmarks = new ArrayList<Entry>();


    public static EntryListFeedManager getInstance() {
        if(instance == null) {
            instance = new EntryListFeedManager();
        }
        return instance;
    }

    @Override
    public void clearList() {
        bookmarks = null;
        bookmarks = new ArrayList<Entry>();
    }

    @Override
    protected void replaceAll(ArrayList<Entry> entries) {
        bookmarks = new ArrayList<Entry>(new LinkedHashSet<Entry>(entries));
    }

    @Override
    public Entry get(Integer index) {
        return bookmarks.get(index);
    }

    @Override
    public ArrayList<Entry> getList() {
        return bookmarks;
    }

    @Override
    public void setCategory(int category) {
        this.category = category;
    }

    @Override
    public int getCategory() {
        return category;
    }

    @Override
    public void replaceFeed(final FeedResponseHandler feedResponseHandler) {
        String endpoint = "entrylist";
        switch (category) {
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
