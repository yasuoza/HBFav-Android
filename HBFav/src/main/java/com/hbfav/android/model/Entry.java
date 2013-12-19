package com.hbfav.android.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.hbfav.android.Constants;
import com.hbfav.android.core.HBFavAPIStringRequest;
import com.hbfav.android.ui.MainActivity;
import com.hbfav.android.util.IntegerMapComparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;


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

    private String[] mRecommendTags = new String[]{};
    private boolean mFetchedRecommendTags;

    public interface EntryDetailFetchListener {
        abstract void onDetailFetched();
    }

    public Entry() { }

    public Entry(String title, String link, Integer count) {
        this.title = title;
        this.link = link;
        this.count = count;
    }

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

    public String[] getmRecommendTags() {
        if (mRecommendTags == null) {
            mRecommendTags = new String[]{};
        }
        return mRecommendTags;
    }

    public void fetchLatestDetailIfNeeded() {
        if (mRecommendTags == null || mRecommendTags.length == 0) {
            fetchLatestDetail(null);
        }
    }

    public void fetchLatestDetail(final EntryDetailFetchListener fetchListener) {
        MainActivity.getRequestQueue().add(new HBFavAPIStringRequest(Request.Method.GET, HatenaApi.entryDetialUrl(link),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        HashMap<String, Integer> tagsMap = new HashMap<String, Integer>();
                        ResultPage entryDetail = ResultPage.gson.fromJson(response, ResultPage.class);
                        HatenaBookmark[] hatenaBookmarks = entryDetail.getBookmarks();
                        for (HatenaBookmark entry : hatenaBookmarks) {
                            for (String tag : entry.getTags()) {
                                int count = tagsMap.containsKey(tag) ? tagsMap.get(tag) + 1 : 1;
                                tagsMap.put(tag, count);
                            }
                        }

                        TreeMap<String, Integer> tagsTreeMap =
                                new TreeMap<String, Integer>(new IntegerMapComparator(tagsMap));
                        tagsTreeMap.putAll(tagsMap);

                        Set<String> tagsSet = tagsTreeMap.keySet();
                        int length = tagsSet.size() > Constants.MAX_TAG_COUNT ? Constants.MAX_TAG_COUNT : tagsSet.size();

                        title = entryDetail.getTitle();
                        count = entryDetail.getCount();
                        link = entryDetail.getUrl();
                        mRecommendTags = Arrays.copyOfRange(tagsSet.toArray(new String[length]), 0, length);

                        if (fetchListener != null) {
                            fetchListener.onDetailFetched();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ));
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
            entry.isPlaceholder = (in.readByte() != 0);
            entry.datetime = (Date) in.readSerializable();
            entry.user = (User) in.readSerializable();

            return entry;
        }

        public Entry[] newArray(int size) {
            return new Entry[size];
        }
    };
}
