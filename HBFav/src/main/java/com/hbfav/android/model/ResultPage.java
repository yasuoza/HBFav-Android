package com.hbfav.android.model;

public class ResultPage {
    private String title;
    private String url;
    private int count;
    private HatenaBookmark[] bookmarks;

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
