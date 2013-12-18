package com.hbfav.android.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ResultPage {
    private String title;
    private String url;
    private int count;
    private HatenaBookmark[] bookmarks;

    public static final Gson gson = new GsonBuilder().setDateFormat("yyyy/MM/dd HH:mm:ss").create();

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public int getCount() {
        return count;
    }

    public HatenaBookmark[] getBookmarks() {
        return bookmarks;
    }
}
