package com.hbfav.android.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hbfav.R;
import com.hbfav.android.controllers.BookmarksFetcher;
import com.hbfav.android.controllers.TimelineFeedManager;
import com.hbfav.android.models.Entry;
import com.loopj.android.http.BinaryHttpResponseHandler;

public class TimelineEntryAdapter extends ArrayAdapter<Entry> {
    private final String[] AllowedImageContentTypes = new String[]{"image/gif", "image/png"};
    private LayoutInflater inflater;
    private int layout;


    public TimelineEntryAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.inflater = ((Activity) context).getLayoutInflater();
        this.layout = textViewResourceId;
    }

    @Override
    public int getCount() {
        return TimelineFeedManager.getList().size();
    }

    @Override
    public Entry getItem(int position) {
        return TimelineFeedManager.get(position);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final Entry entry = TimelineFeedManager.get(position);

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

        Drawable userThumb = entry.getUser().getProfileImage();
        if (userThumb == null) {
            BookmarksFetcher.getImage(entry.getUser().getProfileImageUrl(), new BinaryHttpResponseHandler(AllowedImageContentTypes) {
                @Override
                public void onSuccess(byte[] fileData) {
                    Drawable image = new BitmapDrawable(BitmapFactory.decodeByteArray(fileData, 0, fileData.length));
                    entry.getUser().setProfileImage(image);
                    notifyDataSetChanged();
                }
            });
        }
        ((ImageView) view.findViewById(R.id.fragment_entry_list_user_thumb_image_view))
                .setImageDrawable(userThumb);

        Drawable favicon = entry.getFavicon();
        if (favicon == null) {
            BookmarksFetcher.getImage(entry.getFaviconUrl(), new BinaryHttpResponseHandler(AllowedImageContentTypes) {
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

        ((TextView) view.findViewById(R.id.fragment_entry_user_name))
                .setText(entry.getUser().getName());

        ((TextView) view.findViewById(R.id.fragment_entry_list_entry_created_at))
                .setText(entry.getRelativeTimeSpanString());

        if (entry.getComment().equals("")) {
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