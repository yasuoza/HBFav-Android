package com.hbfav.android.models;


import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;

public class Entry {
    private String title = "";
    private String comment = "";
    private Integer count = 0;
    private String faviconUrl = "";
    private String link = "";
    private String permalink = "";
    private String category = "";
    private String thumbnailUrl = "";
    private boolean isPlaceholder = false;
    private DateTime datetime;
    private User   user;

    public static Entry newPlaceholder() {
        Entry entry = new Entry();
        entry.title = "__placeholder_title__";
        entry.link  = "__placeholder_link__";
        entry.isPlaceholder = true;
        return entry;
    }


    private Entry() { }

    public Entry(JSONObject json) {
        try {
            JSONObject jUser = json.getJSONObject("user");
            User user = new User(jUser.getString("name"), jUser.getString("profile_image_url"));
            this.title = json.getString("title");
            this.comment = json.getString("comment");
            this.count = json.getInt("count");
            this.faviconUrl = json.getString("favicon_url");
            this.datetime = ISODateTimeFormat.dateTimeNoMillis().parseDateTime(json.getString("datetime"));
            this.link = json.getString("link");
            this.permalink = json.getString("permalink");
            this.user = user;
            if (json.has("thumbnail_url")) {
                this.thumbnailUrl = json.getString("thumbnail_url");
            }
            if (json.has("category")) {
                this.category = json.getString("category");
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public DateTime getDateTime() {
        return datetime;
    }

    public CharSequence getRelativeTimeSpanString() {
        return DateUtils.getRelativeTimeSpanString(this.datetime.getMillis());
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

    public Drawable getFavicon() {
        return ImageCache.getImage(faviconUrl);
    }

    public void setFavicon(Drawable favicon) {
        ImageCache.setImage(faviconUrl, favicon);
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

    public Drawable getThumbnailImage() {
        return ImageCache.getImage(thumbnailUrl);
    }

    public void setThumbnailImage(Drawable thumbnailImage) {
        ImageCache.setImage(thumbnailUrl, thumbnailImage);
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
        return entry.getLink().equals(this.getLink())
                && entry.getUser().getName().equals(this.getUser().getName());
    }
}
