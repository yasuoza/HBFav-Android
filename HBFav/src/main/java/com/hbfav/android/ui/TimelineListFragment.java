package com.hbfav.android.ui;


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
import com.hbfav.android.core.FeedResponseHandler;
import com.hbfav.android.core.TimelineFeedManager;
import com.hbfav.android.core.UserInfoManager;
import com.hbfav.android.model.Entry;
import com.hbfav.android.ui.setting.UserRegistrationActivity;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class TimelineListFragment extends ListFragment implements AbsListView.OnScrollListener, OnRefreshListener {
    private View mFooterView;
    private TimelineEntryAdapter mAdapter;
    private PullToRefreshLayout mPullToRefreshLayout;


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
        View rootView = inflater.inflate(R.layout.common_entry_list_view, container, false);
        mFooterView = inflater.inflate(R.layout.timeline_footer, null);

        // PullToRefresh
        mPullToRefreshLayout = (PullToRefreshLayout) rootView.findViewById(R.id.ptr_layout);
        ActionBarPullToRefresh.from(getActivity())
            .allChildrenArePullable()
            .listener(this)
            .setup(mPullToRefreshLayout);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView listView = getListView();
        listView.setFooterDividersEnabled(true);
        if (!TimelineFeedManager.loadedAllBookmarks()) {
            listView.addFooterView(mFooterView, null, false);
            listView.setOnScrollListener(this);
        }

        mAdapter = new TimelineEntryAdapter(
                getActivity(),
                R.layout.timeline_row
        );
        setListAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (UserInfoManager.getUserName().isEmpty()) {
            startActivity(new Intent(getActivity(), UserRegistrationActivity.class));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mPullToRefreshLayout.setRefreshComplete();
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
    public void onScrollStateChanged(AbsListView view, int scrollState) { }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (TimelineFeedManager.loadedAllBookmarks()) {
            return;
        }
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
                mPullToRefreshLayout.setRefreshComplete();
            }
        });
    }

    private void additionalReading() {
        if (TimelineFeedManager.isAppending()
                || UserInfoManager.getUserName().isEmpty()) {
            return;
        }

        TimelineFeedManager.fetchFeed(false, new FeedResponseHandler() {
            @Override
            public void onSuccess() {
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFinish() {
                if (TimelineFeedManager.loadedAllBookmarks() && getListView() != null) {
                    getListView().removeFooterView(mFooterView);
                }
            }
        });
    }
}
