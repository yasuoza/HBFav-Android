package com.hbfav.android.controllers;

import com.hbfav.android.interfaces.FeedResponseHandler;
import com.hbfav.android.models.Entry;

import java.util.ArrayList;

public abstract class BaseListFeedManager {
    protected abstract void replaceAll(ArrayList<Entry> entries);

    public abstract void replaceFeed(final FeedResponseHandler feedResponseHandler);

    public abstract void setCategory(int category);

    public abstract void clearList();

    public abstract ArrayList<Entry> getList();

    public abstract Entry get(Integer position);

    public abstract int getCategory();
}
