package com.hbfav.android.model;

import android.graphics.drawable.Drawable;

import java.util.HashMap;

public class ImageCache {
    private static HashMap<String,Drawable> cache = new HashMap<String,Drawable>();

    public static Drawable getImage(String key) {
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        return null;
    }

    public static synchronized void setImage(String key, Drawable image) {
        cache.put(key, image);
    }

    public static void clearCache(){
        cache = null;
        cache = new HashMap<String,Drawable>();
    }
}
