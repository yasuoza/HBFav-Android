package com.hbfav.android.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.hbfav.R;
import com.hbfav.android.core.TimelineFeedManager;
import com.hbfav.android.model.Entry;
import com.hbfav.android.util.volley.BitmapLruCache;

public class TimelineEntryAdapter extends ArrayAdapter<Entry> {
    private LayoutInflater inflater;
    private int layout;
    private ImageLoader mImageLoader;


    public TimelineEntryAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.inflater = ((Activity) context).getLayoutInflater();
        this.layout = textViewResourceId;
        this.mImageLoader = new ImageLoader(MainActivity.getRequestQueue(), new BitmapLruCache());
    }

    @Override
    public int getCount() {
        return TimelineFeedManager.getInstance().getList().size();
    }

    @Override
    public Entry getItem(int position) {
        return TimelineFeedManager.getInstance().get(position);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final Entry entry = TimelineFeedManager.getInstance().get(position);

        if (view == null) {
            view = this.inflater.inflate(this.layout, parent, false);
        }

        if (entry.getIsPlaceholder()) {
            view.findViewById(R.id.fragment_timeline_row_entry).setVisibility(View.GONE);
            view.findViewById(R.id.fragment_timeline_row_placeholder).setVisibility(View.VISIBLE);
            view.findViewById(R.id.fragment_timeline_row_placeholder_text).setVisibility(View.VISIBLE);
            view.findViewById(R.id.fragment_timeline_row_placeholder_progressbar).setVisibility(View.GONE);
            return view;
        }

        view.findViewById(R.id.fragment_timeline_row_entry).setVisibility(View.VISIBLE);
        view.findViewById(R.id.fragment_timeline_row_placeholder).setVisibility(View.GONE);

        ImageView thumbImageView = ((ImageView) view.findViewById(R.id.fragment_entry_list_user_thumb_image_view));
        ImageLoader.ImageListener thumbImageListener = ImageLoader.getImageListener(
                thumbImageView,
                android.R.drawable.screen_background_light_transparent,
                android.R.drawable.screen_background_dark_transparent
        );
        mImageLoader.get(entry.getUser().getProfileImageUrl(), thumbImageListener);

        ImageView faviconImageView = ((ImageView) view.findViewById(R.id.fragment_entry_list_entry_favicon_image_view));
        ImageLoader.ImageListener faviconImageListener = ImageLoader.getImageListener(
                faviconImageView,
                android.R.drawable.screen_background_light_transparent,
                android.R.drawable.screen_background_light_transparent
        );
        mImageLoader.get(entry.getFaviconUrl(), faviconImageListener);


        ((TextView) view.findViewById(R.id.fragment_entry_user_name))
                .setText(entry.getUser().getName());

        ((TextView) view.findViewById(R.id.fragment_entry_list_entry_created_at))
                .setText(entry.getRelativeTimeSpanString());

        if (entry.getComment().isEmpty()) {
            view.findViewById(R.id.fragment_entry_list_entry_comment).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.fragment_entry_list_entry_comment).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.fragment_entry_list_entry_comment))
                    .setText(entry.getComment());
        }

        ((TextView) view.findViewById(R.id.fragment_entry_list_title))
                .setText(entry.getTitle());

        return view;
    }
}