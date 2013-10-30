package com.hbfav.android.models;


import android.graphics.drawable.Drawable;

public class Entry {
    private String title;
    private String comment;
    private String faviconUrl;
    private Drawable favicon;
    private String link;
    private String permalink;
    private String createdAt;
    private User   user;


    public Entry(String title, String comment, String faviconUrl, String link, String permalink, String createdAt, User user) {
        this.title = title;
        this.comment = comment;
        this.faviconUrl = faviconUrl;
        this.link = link;
        this.permalink = permalink;
        this.createdAt = createdAt;
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final String createdAt) {
        this.createdAt = createdAt;
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
