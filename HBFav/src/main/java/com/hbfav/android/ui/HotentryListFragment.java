package com.hbfav.android.ui;

import com.hbfav.R;
import com.hbfav.android.core.BaseListFeedManager;
import com.hbfav.android.core.HotEntryFeedManager;

public class HotentryListFragment extends BaseEntryListFragment {
    private EntryListAdapter mAdapter;


    @Override
    protected void initAdapter() {
        mAdapter = new EntryListAdapter(
                getActivity(),
                R.layout.common_entry_row,
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
