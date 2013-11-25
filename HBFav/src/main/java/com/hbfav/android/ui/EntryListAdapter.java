package com.hbfav.android.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hbfav.R;
import com.hbfav.android.core.HBFavFetcher;
import com.hbfav.android.model.Entry;
import com.loopj.android.http.BinaryHttpResponseHandler;

import java.util.ArrayList;

public class EntryListAdapter extends ArrayAdapter<Entry> {
    private LayoutInflater inflater;
    private int layout;
    private ArrayList<Entry> mEntries;

    public EntryListAdapter(Context context, int textViewResourceId, ArrayList<Entry> entries) {
        super(context, textViewResourceId);
        this.inflater = ((Activity) context).getLayoutInflater();
        this.layout = textViewResourceId;
        this.mEntries = entries;
    }

    @Override
    public int getCount() {
        return mEntries.size();
    }

    @Override
    public Entry getItem(int position) {
        return mEntries.get(position);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final Entry entry = mEntries.get(position);

        if (view == null) {
            view = this.inflater.inflate(this.layout, parent, false);
        }

        ((TextView) view.findViewById(R.id.fragment_entry_list_title))
                .setText(entry.getTitle());

        if (entry.getThumbnailUrl().isEmpty()) {
            view.findViewById(R.id.fragment_entry_thumb_image_view).setVisibility(View.GONE);
        } else {
            Drawable thumb = entry.getThumbnailImage();
            if (thumb == null) {
                HBFavFetcher.getImage(entry.getThumbnailUrl(), new BinaryHttpResponseHandler(HBFavFetcher
                        .ALLOWED_IMAGE_CONTENT_TYPE) {
                    @Override
                    public void onSuccess(byte[] fileData) {
                        Drawable image = new BitmapDrawable(BitmapFactory.decodeByteArray(fileData, 0,
                                fileData.length));
                        entry.setThumbnailImage(image);
                        notifyDataSetChanged();
                    }
                });
            }
            view.findViewById(R.id.fragment_entry_thumb_image_view).setVisibility(View.VISIBLE);
            ((ImageView) view.findViewById(R.id.fragment_entry_thumb_image_view)).setImageDrawable(thumb);
        }

        Drawable favicon = entry.getFavicon();
        if (favicon == null) {
            HBFavFetcher.getImage(entry.getFaviconUrl(), new BinaryHttpResponseHandler(HBFavFetcher
                    .ALLOWED_IMAGE_CONTENT_TYPE) {
                @Override
                public void onSuccess(byte[] fileData) {
                    Drawable image = new BitmapDrawable(BitmapFactory.decodeByteArray(fileData, 0, fileData.length));
                    entry.setFavicon(image);
                    notifyDataSetChanged();
                }
            });
        }
        ((ImageView) view.findViewById(R.id.fragment_entry_list_entry_favicon_image_view))
                .setImageDrawable(favicon);

        ((TextView) view.findViewById(R.id.fragment_entry_list_url))
                .setText(Uri.parse(entry.getLink()).getHost());

        ((TextView) view.findViewById(R.id.fragment_entry_list_entry_count))
                .setText(entry.getCount() + " users");

        ((TextView) view.findViewById(R.id.fragment_entry_list_entry_category))
                .setText(entry.getCategory());

        ((TextView) view.findViewById(R.id.fragment_entry_list_entry_created_at))
                .setText(entry.getRelativeTimeSpanString());

        return view;
    }

    public void updateEntries(ArrayList<Entry> entries) {
        this.mEntries = entries;
        notifyDataSetChanged();
    }
}
