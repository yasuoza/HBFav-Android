package com.hbfav.android.model;


import android.text.format.DateUtils;

import com.google.gson.annotations.SerializedName;
import com.hbfav.android.util.gson.TimelineExclude;

import java.util.Date;


public class Entry {
    private String title = "";
    private String comment = "";
    private Integer count = 0;
    @SerializedName("favicon_url")
    private String faviconUrl = "";
    private String link = "";
    private String permalink = "";
    @TimelineExclude
    private String category = "";
    @SerializedName("thumbnail_url")
    private String thumbnailUrl = "";
    private boolean isPlaceholder = false;
    private Date datetime;
    private User user;

    public static Entry newPlaceholder(Date dateTime) {
        Entry entry = new Entry();
        entry.title = "__placeholder_title__";
        entry.link = "__placeholder_link__";
        entry.isPlaceholder = true;
        entry.datetime = dateTime;
        return entry;
    }

    public String getTitle() {
        return title;
    }

    public Date getDateTime() {
        return datetime;
    }

    public CharSequence getRelativeTimeSpanString() {
        return DateUtils.getRelativeTimeSpanString(this.datetime.getTime());
    }

    public String getComment() {
        return comment;
    }

    public Integer getCount() {
        return this.count;
    }

    public String getFaviconUrl() {
        return faviconUrl;
    }

    public String getLink() {
        return link;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public boolean getIsPlaceholder() {
        return this.isPlaceholder;
    }

    public User getUser() {
        return user;
    }

    public String getCategory() {
        return this.category;
    }


    @Override
    public int hashCode() {
        return getLink().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Entry)) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        Entry entry = (Entry) obj;
        if (entry.isPlaceholder || this.isPlaceholder) {
            return true;
        }
        return entry.getLink().equals(this.getLink())
                && entry.getUser().getName().equals(this.getUser().getName());
    }
}
