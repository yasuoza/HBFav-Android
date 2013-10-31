package com.hbfav.android.models;


import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class Entry {
    private String title;
    private String comment;
    private String faviconUrl;
    private Drawable favicon;
    private String link;
    private String permalink;
    private DateTime datetime;
    private User   user;
    private DateTimeFormatter isoTimeParser = ISODateTimeFormat.dateTimeNoMillis();

    public Entry(String title, String comment, String faviconUrl, DateTime datetime, String link, String permalink, User user) {
        this.title = title;
        this.comment = comment;
        this.faviconUrl = faviconUrl;
        this.datetime = datetime;
        this.link = link;
        this.permalink = permalink;
        this.user = user;
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

    public String getFaviconUrl() {
        return faviconUrl;
    }

    public Drawable getFavicon() {
        return favicon;
    }

    public void setFavicon(Drawable favicon) {
        this.favicon = favicon;
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

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
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
