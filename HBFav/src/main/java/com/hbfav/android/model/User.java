package com.hbfav.android.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Parcelable, Serializable {
    private String name;
    @SerializedName("profile_image_url")
    private String profileImageUrl;
    @SerializedName("is_oauth_twitter")
    private Boolean isOauthTwitter;
    @SerializedName("is_oauth_evernote")
    private Boolean isOauthEverNote;
    @SerializedName("is_oauth_mixi_check")
    private Boolean isOauthMixi;
    @SerializedName("is_oauth_facebook")
    private Boolean isOauthFacebook;


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

    public Boolean isOauthTwitter() {
        return isOauthTwitter;
    }

    public Boolean isOauthEverNote() {
        return isOauthEverNote;
    }

    public Boolean isOauthMixi() {
        return isOauthMixi;
    }

    public Boolean isOauthFacebook() {
        return isOauthFacebook;
    }


    /* Parcelable implementation */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(profileImageUrl);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            User user = new User(in.readString(), in.readString());
            return user;
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
