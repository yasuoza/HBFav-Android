package com.hbfav.android.controllers;

public class BookmarksFetcher {
    private static BookmarksFetcher ourInstance = new BookmarksFetcher();

    public static BookmarksFetcher getInstance() {
        return ourInstance;
    }

    private BookmarksFetcher() {
    }

    public static Integer fetchBookmarks(String url) {
        return 200;
    }
}
