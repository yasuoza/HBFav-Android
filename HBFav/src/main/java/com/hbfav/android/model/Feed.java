package com.hbfav.android.model;

import java.util.List;

public class Feed {
    private String title;
    private String link;
    private String description;
    private List<Entry> bookmarks;

    public List<Entry> getBookmarks() {
        return bookmarks;
    }
}
