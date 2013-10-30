package com.hbfav.android;


import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hbfav.R;
import com.hbfav.android.controllers.BookmarksFetcher;
import com.hbfav.android.controllers.TimelineFeedManager;
import com.hbfav.android.interfaces.FeedResponseHandler;
import com.hbfav.android.models.Entry;
import com.loopj.android.http.BinaryHttpResponseHandler;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

public class EntryListFragment extends ListFragment
        implements AbsListView.OnScrollListener, PullToRefreshAttacher.OnRefreshListener {
    private TextView headerTextView;
    private View mFooterView;
    private EntryListAdapter mAdapter;
    private PullToRefreshAttacher mPullToRefreshAttacher;
    private boolean isFetchingBookmarks = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_entry_list_view_main, container, false);
        headerTextView = (TextView) rootView.findViewById(R.id.list_header_text);
        mFooterView = inflater.inflate(R.layout.listview_footer, null);
        mPullToRefreshAttacher = ((MainActivity) getActivity()).getPullToRefreshAttacher();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView listView = getListView();
        listView.addFooterView(mFooterView);
        listView.setOnScrollListener(this);

        mPullToRefreshAttacher.addRefreshableView(listView, this);

        mAdapter = new EntryListAdapter(
                getActivity(),
                R.layout.fragment_entry_list_row,
                TimelineFeedManager.getList()
        );
        setListAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        headerTextView.setText(TimelineFeedManager.get(position).getTitle());
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (totalItemCount == firstVisibleItem + visibleItemCount) {
            additionalReading();
        }
    }

    @Override
    public void onRefreshStarted(View view) {
        TimelineFeedManager.fetchFeed("YasuOza", new FeedResponseHandler() {
            @Override
            public void onSuccess() {
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFinish() {
                if (getListView() == null) {
                    return;
                }
                getListView().invalidateViews();
                mPullToRefreshAttacher.setRefreshComplete();
            }
        });
    }

    private void additionalReading() {
        if (isFetchingBookmarks) {
            return;
        }

        isFetchingBookmarks  = true;
        TimelineFeedManager.fetchFeed("YasuOza", new FeedResponseHandler() {
            @Override
            public void onSuccess() {
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFinish() {
                isFetchingBookmarks = false;
            }
        });
    }



    private class EntryListAdapter extends ArrayAdapter<Entry> {
        private final String[] AllowedImageContentTypes = new String[]{"image/gif", "image/png"};
        private LayoutInflater inflater;
        private int layout;


        public EntryListAdapter(Context context, int textViewResourceId, ArrayList<Entry> entries) {
            super(context, textViewResourceId, entries);
            this.inflater = ((Activity) context).getLayoutInflater();
            this.layout = textViewResourceId;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            final Entry entry = TimelineFeedManager.get(position);

            if (view == null) {
                view = this.inflater.inflate(this.layout, parent, false);
            }

            Drawable userThumb = entry.getUser().getProfileImage();
            if (userThumb == null) {
                BookmarksFetcher.getImage(entry.getUser().getProfileImageUrl(), new BinaryHttpResponseHandler(AllowedImageContentTypes) {
                    @Override
                    public void onSuccess(byte[] fileData) {
                        Drawable image = new BitmapDrawable(BitmapFactory.decodeByteArray(fileData, 0, fileData.length));
                        entry.getUser().setProfileImage(image);
                        mAdapter.notifyDataSetChanged();
                    }
                });
                userThumb = getResources().getDrawable(R.drawable.ic_launcher);
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
                        mAdapter.notifyDataSetChanged();
                    }
                });
                favicon = getResources().getDrawable(R.drawable.ic_launcher);
            }
            ((ImageView) view.findViewById(R.id.fragment_entry_list_entry_favicon_image_view))
                    .setImageDrawable(favicon);

            ((TextView) view.findViewById(R.id.fragment_entry_user_name))
                    .setText(entry.getUser().getName());

            ((TextView) view.findViewById(R.id.fragment_entry_list_entry_created_at))
                    .setText(entry.getCreatedAt());

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
}
