package com.hbfav.android.core;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class BookmarksFetcher {
    public static final String[] ALLOWED_IMAGE_CONTENT_TYPE = new String[]{"image/gif", "image/png", "image/jpeg"};
    private static final String BASE_URL = "http://feed.hbfav.com/";
    private static AsyncHttpClient client = new AsyncHttpClient();
    private static ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();


    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        // Override AsyncHttpClient's thread pool
        // https://github.com/loopj/android-async-http/issues/374
        client.setThreadPool(threadPool);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void getImage(String url, BinaryHttpResponseHandler binaryHandler) {
        client.setThreadPool(threadPool);
        client.get(url, binaryHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
