package com.hbfav.android.ui;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.hbfav.android.R;
import com.hbfav.android.model.Entry;
import com.hbfav.android.util.volley.BitmapLruCache;

import java.util.ArrayList;

public class EntryListAdapter extends ArrayAdapter<Entry> {
    private LayoutInflater inflater;
    private int layout;
    private ArrayList<Entry> mEntries;
    private ImageLoader mImageLoader;

    public EntryListAdapter(Context context, int textViewResourceId, ArrayList<Entry> entries) {
        super(context, textViewResourceId);
        this.inflater = ((Activity) context).getLayoutInflater();
        this.layout = textViewResourceId;
        this.mEntries = entries;
        this.mImageLoader = new ImageLoader(MainActivity.getRequestQueue(), new BitmapLruCache());
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

        ImageView thumbImageView = (ImageView) view.findViewById(R.id.fragment_entry_thumb_image_view);
        if (entry.getThumbnailUrl().isEmpty()) {
            thumbImageView.setVisibility(View.GONE);
        } else {
            thumbImageView.setVisibility(View.VISIBLE);
            ImageLoader.ImageListener thumbImageListener = ImageLoader.getImageListener(
                    thumbImageView,
                    android.R.drawable.screen_background_light_transparent,
                    android.R.drawable.screen_background_dark_transparent
            );
            mImageLoader.get(entry.getThumbnailUrl(), thumbImageListener);
        }

        ImageView faviconImageView = ((ImageView) view.findViewById(R.id.fragment_entry_list_entry_favicon_image_view));
        ImageLoader.ImageListener faviconImageListener = ImageLoader.getImageListener(
                faviconImageView,
                android.R.drawable.screen_background_light_transparent,
                android.R.drawable.screen_background_light_transparent
        );
        mImageLoader.get(entry.getFaviconUrl(), faviconImageListener);

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
