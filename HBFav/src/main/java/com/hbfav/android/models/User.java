package com.hbfav.android.models;


import android.graphics.drawable.Drawable;

import com.hbfav.R;

public class User {
    private String name;
    private String profileImageUrl;
    private Drawable profileImage;

    public User(String name, String profileImageUrl) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(final String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Drawable getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(final Drawable profileImage) {
        this.profileImage = profileImage;
    }
}
