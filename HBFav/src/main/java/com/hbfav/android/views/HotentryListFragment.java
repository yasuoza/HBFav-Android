package com.hbfav.android.views;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hbfav.R;
import com.hbfav.android.Constants;
import com.hbfav.android.controllers.BookmarksFetcher;
import com.hbfav.android.controllers.HotEntryFeedManager;
import com.hbfav.android.interfaces.FeedResponseHandler;
import com.hbfav.android.models.Entry;
import com.loopj.android.http.BinaryHttpResponseHandler;

import java.net.URI;
import java.util.Arrays;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

public class HotentryListFragment extends ListFragment implements PullToRefreshAttacher.OnRefreshListener {
    private View mFooterView;
    private HotEntryListAdapter mAdapter;
    private PullToRefreshAttacher mPullToRefreshAttacher;
    private LayoutInflater mInflater;
    private final Integer[] optionIDs = {
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
    public static HotentryListFragment newInstance(int sectionNumber) {
        HotentryListFragment fragment = new HotentryListFragment();
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
        listView.addFooterView(mFooterView, null, false);
        mPullToRefreshAttacher.addRefreshableView(listView, this);
        mAdapter = new HotEntryListAdapter(
                getActivity(),
                R.layout.fragment_entry_row
        );
        setListAdapter(mAdapter);

        restoreActionBar();
        if (HotEntryFeedManager.getList().isEmpty()) {
            HotEntryFeedManager.replaceFeed(new FeedResponseHandler() {
                @Override
                public void onSuccess() {
                    mAdapter.notifyDataSetChanged();
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
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(HotEntryFeedManager.get(position).getLink())));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.removeItem(R.id.action_settings);
        inflater.inflate(R.menu.hotentry, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == HotEntryFeedManager.getCategory()) {
            return false;
        }

        if (!Arrays.asList(optionIDs).contains(item.getItemId())) {
            return super.onOptionsItemSelected(item);
        }

        ListView listView = getListView();
        if (listView != null) {
            mFooterView = mInflater.inflate(R.layout.listview_footer, null);
            getListView().addFooterView(mFooterView, null, false);
        }

        HotEntryFeedManager.setCategory(item.getItemId());
        HotEntryFeedManager.clearList();
        restoreActionBar();
        HotEntryFeedManager.replaceFeed(new FeedResponseHandler() {
            @Override
            public void onSuccess() {
                mAdapter.notifyDataSetChanged();
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
        HotEntryFeedManager.replaceFeed(new FeedResponseHandler() {
            @Override
            public void onSuccess() {
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFinish() {
                mAdapter.notifyDataSetChanged();
                mPullToRefreshAttacher.setRefreshComplete();
            }
        });
    }

    private void restoreActionBar() {
        MainActivity mainActivity = (MainActivity) getActivity();
        String title = getString(R.string.title_section3);;
        if (mainActivity != null) {
            switch (HotEntryFeedManager.getCategory()) {
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



    private class HotEntryListAdapter extends ArrayAdapter<Entry> {
        private final String[] AllowedImageContentTypes = new String[]{"image/gif", "image/png", "image/jpeg"};
        private LayoutInflater inflater;
        private int layout;


        public HotEntryListAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
            this.inflater = ((Activity) context).getLayoutInflater();
            this.layout = textViewResourceId;
        }

        @Override
        public int getCount() {
            return HotEntryFeedManager.getList().size();
        }

        @Override
        public Entry getItem(int position) {
            return HotEntryFeedManager.getList().get(position);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            final Entry entry = HotEntryFeedManager.get(position);

            if (view == null) {
                view = this.inflater.inflate(this.layout, parent, false);
            }

            ((TextView) view.findViewById(R.id.fragment_entry_list_title))
                    .setText(entry.getTitle());

            if (entry.getThumbnailUrl().isEmpty()) {
                view.findViewById(R.id.fragment_entry_thumb_image_view).setVisibility(View.GONE);
            } else {
                Drawable thumb = entry.getThumbnailImage();
                if (thumb == null) {
                    BookmarksFetcher.getImage(entry.getThumbnailUrl(), new BinaryHttpResponseHandler(AllowedImageContentTypes) {
                        @Override
                        public void onSuccess(byte[] fileData) {
                            Drawable image = new BitmapDrawable(BitmapFactory.decodeByteArray(fileData, 0, fileData.length));
                            entry.setThumbnailImage(image);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }
                view.findViewById(R.id.fragment_entry_thumb_image_view).setVisibility(View.VISIBLE);
                ((ImageView) view.findViewById(R.id.fragment_entry_thumb_image_view)).setImageDrawable(thumb);
            }

            Drawable favicon = entry.getFavicon();
            if (favicon == null) {
                BookmarksFetcher.getImage(entry.getFaviconUrl(), new BinaryHttpResponseHandler(AllowedImageContentTypes) {
                    @Override
                    public void onSuccess(byte[] fileData) {
                        Drawable image = new BitmapDrawable(BitmapFactory.decodeByteArray(fileData, 0, fileData.length));
                        entry.setFavicon(image);
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
            ((ImageView) view.findViewById(R.id.fragment_entry_list_entry_favicon_image_view))
                    .setImageDrawable(favicon);

            ((TextView) view.findViewById(R.id.fragment_entry_list_url))
                    .setText(Uri.parse(entry.getLink()).getHost());

            ((TextView) view.findViewById(R.id.fragment_entry_list_entry_count))
                    .setText(entry.getCount() + " " + getString(R.string.users));

            ((TextView) view.findViewById(R.id.fragment_entry_list_entry_category))
                    .setText(entry.getCategory());

            ((TextView) view.findViewById(R.id.fragment_entry_list_entry_created_at))
                    .setText(entry.getRelativeTimeSpanString());


            return view;
        }
    }
}
