package com.hbfav.android.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;


public class Entry implements Parcelable {
    private String title = "";
    private String comment = "";
    private Integer count = 0;
    @SerializedName("favicon_url")
    private String faviconUrl = "";
    private String link = "";
    private String permalink = "";
    private ArrayList<String> categories;
    @SerializedName("thumbnail_url")
    private String thumbnailUrl = "";
    private boolean isPlaceholder = false;
    private Date datetime;
    private User user;


    public Entry() { }

    public static Entry newPlaceholder(Date dateTime) {
        Entry entry = new Entry();
        entry.title = "__placeholder_title__";
        entry.link = "__placeholder_link__";
        entry.isPlaceholder = true;
        entry.datetime = dateTime;
        return entry;
    }

    public static Gson gson() {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
    }


    public String getTitle() {
        return title;
    }

    public Date getDateTime() {
        return datetime;
    }

    public CharSequence getRelativeTimeSpanString() {
        return DateUtils.getRelativeTimeSpanString(this.datetime.getTime());
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

    public String getLink() {
        return link;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public boolean getIsPlaceholder() {
        return this.isPlaceholder;
    }

    public User getUser() {
        return user;
    }

    public String getCategory() {
        return this.categories.size() > 0 ? this.categories.get(0) : "";
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
        if (entry.isPlaceholder || this.isPlaceholder) {
            return true;
        }
        return entry.getLink().equals(this.getLink())
                && entry.getUser().getName().equals(this.getUser().getName());
    }

    /* Parcelable implementation */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeString(comment);
        out.writeInt(count);
        out.writeString(faviconUrl);
        out.writeString(link);
        out.writeString(permalink);
        out.writeList(categories);
        out.writeString(thumbnailUrl);
        out.writeByte((byte) (isPlaceholder ? 1 : 0));
        out.writeSerializable(datetime);
        out.writeSerializable(user);
    }

    public static final Parcelable.Creator<Entry> CREATOR = new Parcelable.Creator<Entry>() {
        public Entry createFromParcel(Parcel in) {
            Entry entry = new Entry();

            entry.title = in.readString();
            entry.comment = in.readString();
            entry.count = in.readInt();
            entry.faviconUrl = in.readString();
            entry.link = in.readString();
            entry.permalink = in.readString();
            entry.categories = in.readArrayList(String.class.getClassLoader());
            entry.thumbnailUrl = in.readString();
            entry.isPlaceholder =  (in.readByte() != 0);
            entry.datetime = (Date) in.readSerializable();
            entry.user = (User) in.readSerializable();

            return entry;
        }

        public Entry[] newArray(int size) {
            return new Entry[size];
        }
    };
}
