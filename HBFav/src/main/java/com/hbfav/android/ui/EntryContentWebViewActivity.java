package com.hbfav.android.ui;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import com.hbfav.android.R;
import com.hbfav.android.core.FetchBookmarkStatusTask;
import com.hbfav.android.core.UserInfoManager;
import com.hbfav.android.model.Entry;
import com.hbfav.android.model.HatenaBookmark;

public class EntryContentWebViewActivity extends Activity {

    private Entry mEntry;

    private ProgressBar mProgressBar;
    private WebView mWebView;
    private Button mHistoryBackButton;
    private Button mHistoryForwardButton;
    private Button mBookmarkButton;
    private Button mBookmarkCountButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);

        mEntry = getIntent().getParcelableExtra("entry");

        setContentView(R.layout.entry_webview);

        mWebView = (WebView) findViewById(R.id.webView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mHistoryBackButton = (Button) findViewById(R.id.historyBackButton);
        mHistoryForwardButton = (Button) findViewById(R.id.historyForwardButton);
        mBookmarkButton = (Button) findViewById(R.id.bookmarkButton);
        mBookmarkCountButton = (Button) findViewById(R.id.entry_webview_bookmark_count_button);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(mEntry.getTitle());

        setHistoryBackButtonClickable(false);
        mHistoryBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackHistory();
            }
        });

        setHistoryForwardButtonClickable(false);
        mHistoryForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goForwardHistory();
            }
        });

        mBookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerBookmark(mEntry);
            }
        });

        mBookmarkCountButton.setText(mEntry.getCount() + "\nusers");
        mBookmarkCountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCommentActivity(mEntry);
            }
        });

        startWebView(mEntry);

        UserInfoManager.refreshMyTagsIfNeeded();
    }

    @Override
    public void onStart() {
        super.onStart();

        updateBookmarkButton();

        updateEntryDetail();
    }

    private void updateEntryDetail() {
        mEntry.fetchLatestDetail(new Entry.EntryDetailFetchListener() {
            @Override
            public void onDetailFetched() {
                mBookmarkCountButton.setText(mEntry.getCount() + "\nusers");
            }
        });
    }

    private void startWebView(Entry entry) {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.loadUrl(entry.getLink());
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            setHistoryBackButtonClickable(mWebView.canGoBack());
            setHistoryForwardButtonClickable(mWebView.canGoForward());
        }
    };

    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);

            if (newProgress == 100) {
                EntryContentWebViewActivity.this.mProgressBar.setVisibility(View.GONE);
            } else {
                EntryContentWebViewActivity.this.mProgressBar.setVisibility(View.VISIBLE);
                EntryContentWebViewActivity.this.mProgressBar.setProgress(newProgress);
            }
        }
    };

    private void updateBookmarkButton() {
        new FetchBookmarkStatusTask() {
            @Override
            protected void onPostExecute(HatenaBookmark bookmark) {
                super.onPostExecute(bookmark);

                if (bookmark != null) {
                    mBookmarkButton.setText("Bed!");
                }
            }
        }.execute(mEntry);
    }

    private void goBackHistory() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        }
    }

    private void goForwardHistory() {
        if (mWebView.canGoForward()) {
            mWebView.goForward();
        }
    }

    private void setHistoryBackButtonClickable(boolean clickable) {
        mHistoryBackButton.setClickable(clickable);
        if (clickable) {
            mHistoryBackButton.setTextColor(getResources().getColor(R.color.hatena_color));
        } else {
            mHistoryBackButton.setTextColor(getResources().getColor(R.color.light_gray_color));
        }
    }

    private void setHistoryForwardButtonClickable(boolean clickable) {
        mHistoryForwardButton.setClickable(clickable);
        if (clickable) {
            mHistoryForwardButton.setTextColor(getResources().getColor(R.color.hatena_color));
        } else {
            mHistoryForwardButton.setTextColor(getResources().getColor(R.color.light_gray_color));
        }
    }

    private void registerBookmark(final Entry entry) {
        Intent intent = new Intent(this, BookmarkEntryActivity.class);
        intent.putExtra("entry", entry);
        startActivity(intent);
    }

    private void startCommentActivity(final Entry entry) {
        Intent intent = new Intent(this, BookmarkCommentListActivity.class);
        intent.putExtra("entry", entry);
        startActivity(intent);
    }
}
