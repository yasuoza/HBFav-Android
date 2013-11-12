package com.hbfav.android.ui;


import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hbfav.R;
import com.hbfav.android.Constants;
import com.hbfav.android.core.BaseListFeedManager;
import com.hbfav.android.core.FeedResponseHandler;

import java.util.Arrays;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

public abstract class BaseEntryListFragment extends ListFragment implements PullToRefreshAttacher.OnRefreshListener {
    private View mFooterView;
    private PullToRefreshAttacher mPullToRefreshAttacher;
    private LayoutInflater mInflater;
    private final Integer[] optionIDs = {
            R.id.option_category_menu_general,
            R.id.option_category_menu_social,
            R.id.option_category_menu_it,
            R.id.option_category_menu_economics,
            R.id.option_category_menu_life,
            R.id.option_category_menu_entertainment,
            R.id.option_category_menu_knowledge,
            R.id.option_category_menu_game,
            R.id.option_category_menu_fun
    };


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

    protected abstract String getSectionBaseTitle();

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
        View rootView = inflater.inflate(R.layout.fragment_entry_list_view_main, container, false);
        mInflater = inflater;
        mFooterView = inflater.inflate(R.layout.listview_footer, null);
        mPullToRefreshAttacher = ((MainActivity) getActivity()).getPullToRefreshAttacher();
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
        mPullToRefreshAttacher.addRefreshableView(listView, this);
        initAdapter();
        setListAdapter(getAdapter());

        restoreActionBar();
        if (getManager().getList().isEmpty()) {
            getManager().replaceFeed(new FeedResponseHandler() {
                @Override
                public void onSuccess() {
                    reloadListData();
                }

                @Override
                public void onFinish() {
                    if (getActivity() == null) {
                        return;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ListView listView = getListView();
                            if (listView != null) {
                                listView.removeFooterView(mFooterView);
                            }

                        }
                    });
                }
            });
        } else {
            getListView().removeFooterView(mFooterView);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mPullToRefreshAttacher.setRefreshComplete();
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getManager().get(position).getLink())));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.removeItem(R.id.action_settings);
        inflater.inflate(R.menu.hotentry, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == getManager().getCategory()) {
            return false;
        }

        if (!Arrays.asList(optionIDs).contains(item.getItemId())) {
            return super.onOptionsItemSelected(item);
        }

        ListView listView = getListView();
        if (listView == null) {
            return false;
        }

        if (listView.getFooterViewsCount() == 0) {
            mFooterView = mInflater.inflate(R.layout.listview_footer, null);
            listView.addFooterView(mFooterView, null, false);
        }

        mPullToRefreshAttacher.setRefreshComplete();

        getManager().setCategory(item.getItemId());
        getManager().clearList();
        reloadListData();
        restoreActionBar();
        getManager().replaceFeed(new FeedResponseHandler() {
            @Override
            public void onSuccess() {
                reloadListData();
            }

            @Override
            public void onFinish() {
                removeListFooter();
            }
        });
        return true;
    }

    @Override
    public void onRefreshStarted(View view) {
        getManager().replaceFeed(new FeedResponseHandler() {
            @Override
            public void onSuccess() {
                reloadListData();
            }

            @Override
            public void onFinish() {
                mPullToRefreshAttacher.setRefreshComplete();
            }
        });
    }

    
    /**
     * Private methods
     */
    private void restoreActionBar() {
        MainActivity mainActivity = (MainActivity) getActivity();
        String title = getSectionBaseTitle();
        if (mainActivity != null) {
            switch (getManager().getCategory()) {
                case R.id.option_category_menu_social:
                    title += " - " +getString(R.string.option_category_menu_social);
                    break;
                case R.id.option_category_menu_it:
                    title += " - " +getString(R.string.option_category_menu_it);
                    break;
                case R.id.option_category_menu_economics:
                    title += " - " +getString(R.string.option_category_menu_economics);
                    break;
                case R.id.option_category_menu_life:
                    title += " - " +getString(R.string.option_category_menu_life);
                    break;
                case R.id.option_category_menu_entertainment:
                    title += " - " +getString(R.string.option_category_menu_entertainment);
                    break;
                case R.id.option_category_menu_knowledge:
                    title += " - " +getString(R.string.option_category_menu_knowledge);
                    break;
                case R.id.option_category_menu_game:
                    title += " - " +getString(R.string.option_category_menu_game);
                    break;
                case R.id.option_category_menu_fun:
                    title += " - " +getString(R.string.option_category_menu_fun);
                    break;
            }
            mainActivity.setActionBarTitle(title);
        }
    }

    private void removeListFooter() {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (getListView() != null) {
                    getListView().removeFooterView(mFooterView);
                }
            }
        });
    }
}
