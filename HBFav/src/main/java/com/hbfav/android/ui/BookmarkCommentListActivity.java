package com.hbfav.android.ui;


import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.hbfav.android.R;
import com.hbfav.android.core.HBFavAPIStringRequest;
import com.hbfav.android.model.Entry;
import com.hbfav.android.model.HatenaApi;
import com.hbfav.android.model.HatenaBookmark;
import com.hbfav.android.model.ResultPage;
import com.hbfav.android.util.volley.BitmapLruCache;

public class BookmarkCommentListActivity extends ListActivity {

    private Entry mEntry;

    private CommentListAdapter mAdapter;
    private HatenaBookmark[] mBookmarks = new HatenaBookmark[]{};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEntry = getIntent().getParcelableExtra("entry");

        ActionBar actionBar = getActionBar();
        getActionBar().setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(mEntry.getTitle());

        setContentView(R.layout.activity_bookmark_comment_list);

        mAdapter = new CommentListAdapter(
                this,
                R.layout.timeline_row
        );
        setListAdapter(mAdapter);

        fetchComments();
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        HatenaBookmark bookmark = mBookmarks[position];

        Intent intent = new Intent(this, CommonWebViewActivity.class);
        intent.putExtra("url", "http://b.hatena.ne.jp/" + bookmark.getUserName() + "/touch");
        intent.putExtra("title", bookmark.getUserName());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void fetchComments() {
        MainActivity.getRequestQueue().add(new HBFavAPIStringRequest(Request.Method.GET, HatenaApi.entryDetialUrl(mEntry.getLink()),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ResultPage entryDetail = ResultPage.gson.fromJson(response, ResultPage.class);
                        mBookmarks = entryDetail.getBookmarks();
                        mAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ));
    }


    private class CommentListAdapter extends ArrayAdapter<HatenaBookmark> {
        private LayoutInflater inflater;
        private int layout;
        private ImageLoader mImageLoader;

        public CommentListAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
            this.inflater = ((Activity) context).getLayoutInflater();
            this.layout = textViewResourceId;
            this.mImageLoader = new ImageLoader(MainActivity.getRequestQueue(), new BitmapLruCache());
        }

        @Override
        public int getCount() {
            return mBookmarks.length;
        }

        @Override
        public HatenaBookmark getItem(int position) {
            return mBookmarks[position];
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            final HatenaBookmark bookmark = getItem(position);

            if (view == null) {
                view = this.inflater.inflate(this.layout, parent, false);
            }

            view.findViewById(R.id.fragment_timeline_row_entry).setVisibility(View.VISIBLE);

            ImageView thumbImageView = ((ImageView) view.findViewById(R.id.fragment_entry_list_user_thumb_image_view));
            ImageLoader.ImageListener thumbImageListener = ImageLoader.getImageListener(
                    thumbImageView,
                    android.R.drawable.screen_background_light_transparent,
                    android.R.drawable.screen_background_dark_transparent
            );
            mImageLoader.get(HatenaApi.getThumbUrl(bookmark.getUserName()), thumbImageListener);

            (view.findViewById(R.id.fragment_entry_list_entry_favicon_image_view)).setVisibility(View.GONE);

            ((TextView) view.findViewById(R.id.fragment_entry_user_name))
                    .setText(bookmark.getUserName());

            ((TextView) view.findViewById(R.id.fragment_entry_list_entry_created_at))
                    .setText(DateUtils.getRelativeTimeSpanString(bookmark.getTimeStamp().getTime()));

            if (bookmark.getComment().isEmpty()) {
                view.findViewById(R.id.fragment_entry_list_entry_comment).setVisibility(View.GONE);
            } else {
                view.findViewById(R.id.fragment_entry_list_entry_comment).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.fragment_entry_list_entry_comment))
                        .setText(bookmark.getComment());
            }

            (view.findViewById(R.id.fragment_entry_list_title)).setVisibility(View.GONE);

            return view;
        }
    }

}
