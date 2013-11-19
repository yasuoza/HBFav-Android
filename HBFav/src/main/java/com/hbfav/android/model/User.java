package com.hbfav.android.model;


import android.graphics.drawable.Drawable;

public class User {
    private String name;
    private String profileImageUrl;

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

    public Drawable getProfileImage() {
        return ImageCache.getImage(profileImageUrl);
    }

    public void setProfileImage(final Drawable profileImage) {
        ImageCache.setImage(profileImageUrl, profileImage);
    }
}
