package com.hbfav.android.ui;


import android.app.ActionBar;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.hbfav.R;
import com.hbfav.android.Constants;
import com.hbfav.android.core.BaseListFeedManager;
import com.hbfav.android.core.FeedResponseHandler;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public abstract class BaseEntryListFragment extends ListFragment implements OnRefreshListener {
    private View mFooterView;
    private LayoutInflater mInflater;
    private PullToRefreshLayout mPullToRefreshLayout;
    private Toast mTimeoutToast;


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static BaseEntryListFragment newInstance(int sectionNumber) {
        BaseEntryListFragment fragment;
        switch (sectionNumber) {
            case 2:
                fragment = new EntryListFragment();
                break;
            default:
                fragment = new HotentryListFragment();
                break;
        }
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    /**
     * Abstract methods
     */
    protected abstract void initAdapter();

    protected abstract EntryListAdapter getAdapter();

    protected abstract void reloadListData();

    protected abstract BaseListFeedManager getManager();


    /**
     * Public methods
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(Constants.ARG_SECTION_NUMBER));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.common_entry_list_view, container, false);
        mInflater = inflater;
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
        if (listView == null) {
            return;
        }
        listView.addFooterView(mFooterView, null, false);
        listView.setFooterDividersEnabled(false);
        initAdapter();
        setListAdapter(getAdapter());
        restoreActionBar();
        categorySelected(getManager().getCategory());
    }

    @Override
    public void onStop() {
        super.onStop();
        mPullToRefreshLayout.setRefreshComplete();
        getManager().cancellAllRequest();
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getManager().get(position).getLink())));
    }

    @Override
    public void onRefreshStarted(View view) {
        getManager().replaceFeed(new FeedResponseHandler() {
            @Override
            public void onSuccess() {
                reloadListData();
            }

            @Override
            public void onError() {
                showTimeoutToast();
            }

            @Override
            public void onFinish() {
                mPullToRefreshLayout.setRefreshComplete();
            }
        });
    }


    /**
     * Private methods
     */
    private void restoreActionBar() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity == null) {
            return;
        }

        mainActivity.restoreActionBar(new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                return categorySelected(itemPosition);
            }
        });
    }

    private boolean categorySelected(int itemPosition) {
        ListView listView = getListView();
        if (getManager().getList().size() != 0 && itemPosition == getManager().getCategory()) {
            if (listView.getFooterViewsCount() > 0) {
                listView.removeFooterView(mFooterView);
            }
            return false;
        }
        if (listView.getFooterViewsCount() == 0) {
            mFooterView = mInflater.inflate(R.layout.timeline_footer, null);
            listView.addFooterView(mFooterView, null, false);
        }
        mPullToRefreshLayout.setRefreshComplete();
        getManager().clearList();
        getManager().setCategory(itemPosition);
        getActivity().getActionBar().setSelectedNavigationItem(getManager().getCategory());
        reloadListData();
        getManager().replaceFeed(new FeedResponseHandler() {
            @Override
            public void onSuccess() {
                reloadListData();
            }

            @Override
            public void onError() {
                showTimeoutToast();
            }

            @Override
            public void onFinish() {
                getListView().removeFooterView(mFooterView);
            }
        });
        return true;
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
