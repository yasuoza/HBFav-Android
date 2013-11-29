package com.hbfav.android.model;

import android.graphics.drawable.Drawable;

public class NavigationItem {
    private Drawable icon;
    private String title;

    public NavigationItem(Drawable icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }
}
