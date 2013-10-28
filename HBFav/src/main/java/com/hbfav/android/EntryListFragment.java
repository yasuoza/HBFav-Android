package com.hbfav.android;


import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hbfav.R;
import com.hbfav.android.models.Entry;
import com.hbfav.android.models.User;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Random;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

public class EntryListFragment extends ListFragment
        implements AbsListView.OnScrollListener, PullToRefreshAttacher.OnRefreshListener {
    private TextView headerTextView;
    private View mFooterView;
    private ArrayList<Entry> mEntries;
    private EntryListAdapter mAdapter;
    private AsyncTask mTask;
    private PullToRefreshAttacher mPullToRefreshAttacher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEntries = new ArrayList<Entry>();
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

        mAdapter = new EntryListAdapter(
                getActivity(),
                R.layout.fragment_entry_list_row,
                mEntries
        );
        setListAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        headerTextView.setText(mEntries.get(position).getTitle());
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
        User user = new User("Append_user_name", "prepend_thumb_url");
        Entry entry = new Entry(
                "append_entry_title",
                "This is special comment",
                "append_entry_link",
                "entry_permalink",
                "2 hours ago",
                user
        );;
        for (int i = 0; i < 10; i++) {
            mEntries.add(entry);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void prependListData() {
        User user = new User("Prepend_user_name", "prepend_thumb_url");
        Entry entry = new Entry(
                "prepend_entry_title",
                "This is special comment",
                "prepend_entry_link",
                "entry_permalink",
                "1 hours ago",
                user
        );
        for (int i = 0; i < 5; i++) {
            mEntries.add(0, entry);
        }
        mAdapter.notifyDataSetChanged();
    }


    private class EntryListAdapter extends ArrayAdapter<Entry> {
        private LayoutInflater inflater;
        private int layout;

        public EntryListAdapter(Context context, int textViewResourceId, ArrayList<Entry> entries) {
            super(context, textViewResourceId, entries);
            this.inflater = ((Activity) context).getLayoutInflater();
            this.layout = textViewResourceId;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                view = this.inflater.inflate(this.layout, parent, false);
            }

            Entry entry = mEntries.get(position);

            Drawable userThumb = entry.getUser().getProfileImage();
            if (userThumb == null) {
                entry.getUser().setProfileImage(getResources().getDrawable(R.drawable.ic_launcher));
                userThumb = entry.getUser().getProfileImage();
            }

            // Assign entry data to view
            ((ImageView) view.findViewById(R.id.fragment_entry_list_user_thumb_image_view))
                    .setImageDrawable(userThumb);
            ((TextView) view.findViewById(R.id.fragment_entry_user_name))
                    .setText(entry.getUser().getName());
            ((TextView) view.findViewById(R.id.fragment_entry_list_entry_created_at))
                    .setText(entry.getCreatedAt());
            ((TextView) view.findViewById(R.id.fragment_entry_list_entry_comment))
                    .setText(entry.getComment());
            ((ImageView) view.findViewById(R.id.fragment_entry_list_entry_favicon_image_view))
                    .setImageDrawable(userThumb);
            ((TextView) view.findViewById(R.id.fragment_entry_list_title))
                    .setText(entry.getTitle());

            if (position == 2) {
                view.findViewById(R.id.fragment_entry_list_entry_comment).setVisibility(View.GONE);
            }

            return view;
        }
    }
}
