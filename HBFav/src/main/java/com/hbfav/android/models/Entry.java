package com.hbfav.android.models;


import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
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
    private DateTime datetime;
    private User   user;
    private final DateTimeFormatter isoTimeParser = ISODateTimeFormat.dateTimeNoMillis();

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

    public void setDateTime(final String datetimeStr) {
        this.datetime = isoTimeParser.parseDateTime(datetimeStr);
    }

    public CharSequence getRelativeTimeSpanString() {
        return DateUtils.getRelativeTimeSpanString(this.datetime.getMillis());
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
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

    public void setLink(final String link) {
        this.link = link;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(final String permalink) {
        this.permalink = permalink;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(final String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
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

    public void setUser(final User user) {
        this.user = user;
    }

    public void setCategory(final String category) {
        this.category = category;
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
