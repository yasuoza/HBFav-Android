package com.hbfav.android.core;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;


public class HBFavAPIStringRequest extends StringRequest {
    private final static int TIMEOUT_MS = 20 * 1000;

    private final Response.Listener<String> mListener;


    public HBFavAPIStringRequest(int method, String url, Response.Listener<String> listener,
                                 Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_MS, 1, 1.0f));
        setShouldCache(false);
        mListener = listener;
    }

    public HBFavAPIStringRequest(int method, String url, String tag, Response.Listener<String> listener,
                                 Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_MS, 1, 1.0f));
        setTag(tag);
        setShouldCache(false);
        mListener = listener;
    }
}
