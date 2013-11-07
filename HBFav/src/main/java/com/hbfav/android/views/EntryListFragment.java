package com.hbfav.android.views;

import com.hbfav.R;
import com.hbfav.android.controllers.BaseListFeedManager;
import com.hbfav.android.controllers.EntryListFeedManager;

public class EntryListFragment extends BaseEntryListFragment {

    @Override
    protected void reloadListData() {
        mAdapter.updateEntries(getManager().getList());
    }

    @Override
    protected String getSectionBaseTitle() {
        return getString(R.string.title_section2);
    }

    @Override
    protected BaseListFeedManager getManager() {
        return EntryListFeedManager.getInstance();
    }
}
