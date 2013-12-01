package com.hbfav.android.model;


import com.google.gson.annotations.SerializedName;

public class User {
    private String name;
    @SerializedName("profile_image_url")
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

}
