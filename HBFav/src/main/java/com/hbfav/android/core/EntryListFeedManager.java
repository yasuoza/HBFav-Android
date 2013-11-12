package com.hbfav.android.core;

import com.hbfav.R;
import com.hbfav.android.model.Entry;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class EntryListFeedManager extends BaseListFeedManager {
    private static EntryListFeedManager instance;
    private int category = R.id.option_category_menu_general;
    private ArrayList<Entry> bookmarks = new ArrayList<Entry>();


    public static EntryListFeedManager getInstance() {
        if(instance == null) {
            instance = new EntryListFeedManager();
        }
        return instance;
    }

    @Override
    public ArrayList<Entry> getList() {
        return bookmarks;
    }

    @Override
    public void setList(ArrayList<Entry> entries) {
        this.bookmarks = new ArrayList<Entry>(new LinkedHashSet<Entry>(entries));
    }

    @Override
    public void setCategory(int category) {
        this.category = category;
    }

    @Override
    public int getCategory() {
        return category;
    }

    @Override
    protected String getEndPoint() {
        return "entrylist";
    }
}
