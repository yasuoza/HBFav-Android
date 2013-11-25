package com.hbfav.android.core;

import com.hbfav.android.Constants;
import com.hbfav.android.model.Entry;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        HBFavFetcher.get(endpoint, null, new JsonHttpResponseHandler() {
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
                    setList(entries);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
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
