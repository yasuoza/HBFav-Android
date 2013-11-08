package com.hbfav.android.views;

import com.hbfav.R;
import com.hbfav.android.controllers.BaseListFeedManager;
import com.hbfav.android.controllers.HotEntryFeedManager;

public class HotentryListFragment extends BaseEntryListFragment {
    private EntryListAdapter mAdapter;


    @Override
    protected void initAdapter() {
        mAdapter = new EntryListAdapter(
                getActivity(),
                R.layout.fragment_entry_row,
                getManager().getList()
        );
    }

    @Override
    protected EntryListAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    protected void reloadListData() {
        mAdapter.updateEntries(getManager().getList());
    }

    @Override
    protected String getSectionBaseTitle() {
        return getString(R.string.title_section3);
    }

    @Override
    protected BaseListFeedManager getManager() {
        return HotEntryFeedManager.getInstance();
    }
}
