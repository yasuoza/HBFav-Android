package com.hbfav.android.views;


import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.hbfav.R;
import com.hbfav.android.Constants;
import com.hbfav.android.controllers.TimelineFeedManager;
import com.hbfav.android.interfaces.FeedResponseHandler;
import com.hbfav.android.models.Entry;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

public class TimelineListFragment extends ListFragment
        implements AbsListView.OnScrollListener, PullToRefreshAttacher.OnRefreshListener {
    private View mFooterView;
    private TimelineEntryAdapter mAdapter;
    private PullToRefreshAttacher mPullToRefreshAttacher;
    private boolean isFetchingBookmarks = false;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TimelineListFragment newInstance(int sectionNumber) {
        TimelineListFragment fragment = new TimelineListFragment();
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
        listView.addFooterView(mFooterView, null, false);
        listView.setOnScrollListener(this);

        mPullToRefreshAttacher.addRefreshableView(listView, this);

        mAdapter = new TimelineEntryAdapter(
                getActivity(),
                R.layout.fragment_timeline_row
        );
        setListAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        mPullToRefreshAttacher.setRefreshComplete();
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        Entry entry = TimelineFeedManager.get(position);

        if (entry.getIsPlaceholder()) {
            view.findViewById(R.id.fragment_timeline_row_placeholder_text).setVisibility(View.GONE);
            view.findViewById(R.id.fragment_timeline_row_placeholder_progressbar).setVisibility(View.VISIBLE);
            TimelineFeedManager.fetchFeed(position, new FeedResponseHandler() {
                @Override
                public void onSuccess() {
                    mAdapter.notifyDataSetChanged();
                }
            });
            return;
        }

        Uri uri = Uri.parse(entry.getLink());
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
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
        TimelineFeedManager.fetchFeed(true, new FeedResponseHandler() {
            @Override
            public void onSuccess() {
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFinish() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }
                mPullToRefreshAttacher.setRefreshComplete();
            }
        });
    }

    private void additionalReading() {
        if (isFetchingBookmarks) {
            return;
        }

        isFetchingBookmarks  = true;
        TimelineFeedManager.fetchFeed(false, new FeedResponseHandler() {
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
}
