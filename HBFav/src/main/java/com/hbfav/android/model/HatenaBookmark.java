package com.hbfav.android.model;

import com.google.gson.annotations.SerializedName;

public class HatenaBookmark {
    private HatenaBookmark[] favorites;
    private String comment;
    @SerializedName("private") private boolean isPrivate;
    @SerializedName("user") private String userName;
    private String[] tags;

    public String getComment() {
        return comment;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public String getUserName() {
        return userName;
    }

    public String[] getTags() {
        return tags;
    }
}
