package com.hbfav.android.ui;


import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.hbfav.android.R;
import com.hbfav.android.core.FetchBookmarkStatusTask;
import com.hbfav.android.core.UserInfoManager;
import com.hbfav.android.model.Entry;
import com.hbfav.android.model.HatenaBookmark;

public class EntryContentWebViewActivity extends BaseActivity {

    private Entry mEntry;

    private ProgressBar mProgressBar;
    private WebView mWebView;
    private ImageButton mHistoryBackButton;
    private ImageButton mHistoryForwardButton;
    private ImageButton mBookmarkButton;
    private ImageButton mBookmarkCountButton;
    private TextView mBookmarkCountText;
    private ShareActionProvider mShareActionProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);

        super.onCreate(savedInstanceState);

        mEntry = getIntent().getParcelableExtra("entry");

        setContentView(R.layout.entry_webview);

        mWebView = (WebView) findViewById(R.id.webView);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mHistoryBackButton = (ImageButton) findViewById(R.id.historyBackButton);
        mHistoryForwardButton = (ImageButton) findViewById(R.id.historyForwardButton);

        mBookmarkButton = (ImageButton) findViewById(R.id.bookmarkButton);
        mBookmarkCountButton = (ImageButton) findViewById(R.id.entry_webview_bookmark_count_button);

        ActionBar actionBar = getActionBar();
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
                startBookmarkEntryActivity(mEntry);
            }
        });

        mBookmarkCountText = (TextView) findViewById(R.id.entry_webview_bookmark_count_text);
        mBookmarkCountText.setText(mEntry.getCount() + "\nusers");
        mBookmarkCountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBookmarkCommentActivity(mEntry);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.share_menu, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mEntry.getLink());
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, mEntry.getTitle());
        sendIntent.setType("text/plain");
        setShareIntent(Intent.createChooser(sendIntent, "Share '" +mEntry.getTitle()+ "' via"));

        // Return true to display menu
        return true;
    }

    // Call to update the share_menu intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private void updateEntryDetail() {
        mEntry.fetchLatestDetail(new Entry.EntryDetailFetchListener() {
            @Override
            public void onDetailFetched() {
                mBookmarkCountText.setText(mEntry.getCount() + "\nusers");
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
                    mBookmarkButton.setImageDrawable(getResources().getDrawable(R.drawable.btn_bookmark_checked));
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
            mHistoryBackButton.setImageDrawable(getResources().getDrawable(R.drawable.btn_navigation_back_on));
        } else {
            mHistoryBackButton.setImageDrawable(getResources().getDrawable(R.drawable.btn_navigation_back_off));
        }
    }

    private void setHistoryForwardButtonClickable(boolean clickable) {
        mHistoryForwardButton.setClickable(clickable);
        if (clickable) {
            mHistoryForwardButton.setImageDrawable(getResources().getDrawable(R.drawable.btn_navigation_forward_on));
        } else {
            mHistoryForwardButton.setImageDrawable(getResources().getDrawable(R.drawable.btn_navigation_forward_off));
        }
    }

    private void startBookmarkEntryActivity(final Entry entry) {
        Intent intent = new Intent(this, BookmarkEntryActivity.class);
        intent.putExtra("entry", entry);
        startActivity(intent);
    }

    private void startBookmarkCommentActivity(final Entry entry) {
        Intent intent = new Intent(this, BookmarkCommentListActivity.class);
        intent.putExtra("entry", entry);
        startActivity(intent);
    }
}
