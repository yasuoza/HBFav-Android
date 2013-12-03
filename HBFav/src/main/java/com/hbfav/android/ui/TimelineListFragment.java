package com.hbfav.android.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.hbfav.R;
import com.hbfav.android.BaseListFragment;
import com.hbfav.android.Constants;
import com.hbfav.android.core.FeedResponseHandler;
import com.hbfav.android.core.TimelineFeedManager;
import com.hbfav.android.core.UserInfoManager;
import com.hbfav.android.model.Entry;
import com.hbfav.android.ui.setting.SettingActivity;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class TimelineListFragment extends BaseListFragment implements AbsListView.OnScrollListener, OnRefreshListener {
    private View mFooterView;
    private TimelineEntryAdapter mAdapter;
    private PullToRefreshLayout mPullToRefreshLayout;
    private Toast mTimeoutToast;

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
        listView.setFooterDividersEnabled(false);
        listView.addFooterView(mFooterView, null, false);
        listView.setOnScrollListener(this);

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
            startActivity(new Intent(getActivity(), SettingActivity.class));
            return;
        }
        if (getListView() != null && TimelineFeedManager.getInstance().getList().isEmpty()) {
            getListView().invalidateViews();
        }
        if (getListView().getFooterViewsCount() == 0) {
            mFooterView = LayoutInflater.from(getActivity()).inflate(R.layout.timeline_footer, null);
            getListView().addFooterView(mFooterView, null, false);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mPullToRefreshLayout.setRefreshComplete();
        TimelineFeedManager.getInstance().cancelAllRequest();
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        Entry entry = TimelineFeedManager.getInstance().get(position);

        if (entry.getIsPlaceholder()) {
            view.findViewById(R.id.fragment_timeline_row_placeholder_text).setVisibility(View.GONE);
            view.findViewById(R.id.fragment_timeline_row_placeholder_progressbar).setVisibility(View.VISIBLE);
            TimelineFeedManager.getInstance().fetchFeed(position, new FeedResponseHandler() {
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
        if (totalItemCount > 0 && totalItemCount == firstVisibleItem + visibleItemCount) {
            additionalReading();
        }
    }

    @Override
    public void onRefreshStarted(View view) {
        TimelineFeedManager.getInstance().fetchFeed(true, new FeedResponseHandler() {
            @Override
            public void onSuccess() {
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError() {
                showTimeoutToast();
            }

            @Override
            public void onFinish() {
                mAdapter.notifyDataSetChanged();
                mPullToRefreshLayout.setRefreshComplete();
            }
        });
    }

    @Override
    protected String getPageTitle() {
        return getString(R.string.page_timeline);
    }

    private void additionalReading() {
        if (TimelineFeedManager.getInstance().isAppending()
                || UserInfoManager.getUserName().isEmpty()) {
            return;
        }

        TimelineFeedManager.getInstance().fetchFeed(false, new FeedResponseHandler() {
            @Override
            public void onSuccess() {
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError() {
                getListView().removeFooterView(mFooterView);
                if (TimelineFeedManager.getInstance().getList().isEmpty()) {
                    showTimeoutToast();
                }
            }

            @Override
            public void onFinish() {
                if (getListView() != null && TimelineFeedManager.getInstance().loadedAllBookmarks()) {
                    getListView().removeFooterView(mFooterView);
                }
            }
        });
    }

    private void showTimeoutToast() {
        if (mTimeoutToast != null && mTimeoutToast.getView().isShown()) {
            return;
        }
        Context ctx = getActivity().getApplicationContext();
        mTimeoutToast = Toast.makeText(ctx, ctx.getString(R.string.timeout_error), Toast.LENGTH_LONG);
        mTimeoutToast.show();
    }
}
