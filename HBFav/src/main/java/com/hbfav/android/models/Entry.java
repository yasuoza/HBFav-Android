package com.hbfav.android.models;


public class Entry {
    private String title;
    private String comment;
    private String link;
    private String permalink;
    private User   user;


    public Entry(String title, String link, String permalink, User user) {
        this.title = title;
        this.link = link;
        this.permalink = permalink;
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setTitle(final String title) {
        this.title = title;
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
}
