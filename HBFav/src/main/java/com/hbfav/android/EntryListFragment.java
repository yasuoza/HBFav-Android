package com.hbfav.android;


import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hbfav.R;

import java.util.ArrayList;
import java.util.Random;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

public class EntryListFragment extends ListFragment
        implements AbsListView.OnScrollListener, PullToRefreshAttacher.OnRefreshListener {
    private TextView headerTextView;
    private View mFooterView;
    private ArrayList<String> mEntries;
    private ArrayAdapter<String> mAdapter;
    private AsyncTask mTask;
    private PullToRefreshAttacher mPullToRefreshAttacher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEntries = new ArrayList<String>();
    }

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

        mAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                mEntries
        );
        setListAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        headerTextView.setText(mEntries.get(position));
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
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                prependListData();
                getListView().invalidateViews();
                mPullToRefreshAttacher.setRefreshComplete();
            }
        }.execute();
    }

    private void additionalReading() {
        if (mTask != null && mTask.getStatus() == AsyncTask.Status.RUNNING) {
            return;
        }
        mTask = new AsyncTask<Long, Void, Void>() {
            @Override
            protected Void doInBackground(Long[] params) {
                try {
                    Thread.sleep(params[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            };

            protected void onPostExecute(Void result) {
                addListData();
                getListView().invalidateViews();
            };
        }.execute(Math.abs(new Random(System.currentTimeMillis()).nextLong() % 3000));
    }

    private void addListData() {
        for (int i = 0; i < 10; i++) {
            mEntries.add("Hello world");
        }
        mAdapter.notifyDataSetChanged();
    }

    private void prependListData() {
        for (int i = 0; i < 5; i++) {
            mEntries.add(0, "Goodbye world");
        }
        mAdapter.notifyDataSetChanged();
    }
}
