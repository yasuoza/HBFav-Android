package com.hbfav.android.views;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hbfav.R;
import com.hbfav.android.Constants;
import com.hbfav.android.controllers.BookmarksFetcher;
import com.hbfav.android.controllers.HotEntryFeedManager;
import com.hbfav.android.controllers.TimelineFeedManager;
import com.hbfav.android.interfaces.FeedResponseHandler;
import com.hbfav.android.models.Entry;
import com.loopj.android.http.BinaryHttpResponseHandler;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

public class HotentryListFragment extends ListFragment implements PullToRefreshAttacher.OnRefreshListener {
    private View mFooterView;
    private HotEntryListAdapter mAdapter;
    private PullToRefreshAttacher mPullToRefreshAttacher;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static HotentryListFragment newInstance(int sectionNumber) {
        HotentryListFragment fragment = new HotentryListFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(Constants.ARG_SECTION_NUMBER));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_entry_list_view_main, container, false);
        mFooterView = inflater.inflate(R.layout.listview_footer, null);
        mPullToRefreshAttacher = ((MainActivity) getActivity()).getPullToRefreshAttacher();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView listView = getListView();
        listView.addFooterView(mFooterView);
        mPullToRefreshAttacher.addRefreshableView(listView, this);
        mAdapter = new HotEntryListAdapter(
                getActivity(),
                R.layout.fragment_entry_list_row
        );
        setListAdapter(mAdapter);

        if (HotEntryFeedManager.getList().isEmpty()) {
            String endpoint = "hotentry";
            HotEntryFeedManager.fetchFeed(endpoint, false, new FeedResponseHandler() {
                @Override
                public void onSuccess() {
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFinish() {
                    mFooterView.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        Uri uri = Uri.parse(HotEntryFeedManager.get(position).getLink());
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    @Override
    public void onRefreshStarted(View view) {
        TimelineFeedManager.fetchFeed("hotentry", true, new FeedResponseHandler() {
            @Override
            public void onSuccess() {
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFinish() {
                if (getListView() == null) {
                    return;
                }
                mAdapter.notifyDataSetChanged();
                mPullToRefreshAttacher.setRefreshComplete();
            }
        });
    }



    private class HotEntryListAdapter extends ArrayAdapter<Entry> {
        private final String[] AllowedImageContentTypes = new String[]{"image/gif", "image/png"};
        private LayoutInflater inflater;
        private int layout;


        public HotEntryListAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
            this.inflater = ((Activity) context).getLayoutInflater();
            this.layout = textViewResourceId;
        }

        @Override
        public int getCount() {
            return HotEntryFeedManager.getList().size();
        }

        @Override
        public Entry getItem(int position) {
            return HotEntryFeedManager.getList().get(position);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            final Entry entry = HotEntryFeedManager.get(position);

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
            }
            ((ImageView) view.findViewById(R.id.fragment_entry_list_entry_favicon_image_view))
                    .setImageDrawable(favicon);

            ((TextView) view.findViewById(R.id.fragment_entry_user_name))
                    .setText(entry.getUser().getName());

            ((TextView) view.findViewById(R.id.fragment_entry_list_entry_created_at))
                    .setText(entry.getRelativeTimeSpanString());

            view.findViewById(R.id.fragment_entry_list_entry_comment).setVisibility(View.GONE);

            ((TextView) view.findViewById(R.id.fragment_entry_list_title))
                    .setText(entry.getTitle());

            return view;
        }
    }
}
