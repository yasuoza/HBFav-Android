package com.hbfav.android.model;

public class ResultEntry {
    private String title;
    private String url;
    private int count;
    private HatenaEntry[] bookmarks;

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public int getCount() {
        return count;
    }

    public HatenaEntry[] getEntries() {
        return bookmarks;
    }
}
