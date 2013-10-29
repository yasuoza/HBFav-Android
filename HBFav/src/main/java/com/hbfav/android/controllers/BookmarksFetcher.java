package com.hbfav.android.controllers;

import com.loopj.android.http.*;

public class BookmarksFetcher {
    private static final String BASE_URL = "http://feed.hbfav.com/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void getImage(String url, BinaryHttpResponseHandler binaryHandler) {
        client.get(url, binaryHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
