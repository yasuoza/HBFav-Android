package com.hbfav.android.ui;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.hbfav.android.R;
import com.hbfav.android.core.HatenaApiManager;
import com.hbfav.android.core.UserInfoManager;
import com.hbfav.android.model.Entry;
import com.hbfav.android.model.User;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

public class EntryContentWebViewActivity extends Activity {

    private ProgressBar mProgressBar;
    private WebView mWebView;
    private Button mHistoryBackButton;
    private Button mHistoryForwardButton;
    private Button mBookmarkButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);

        final Entry entry = getIntent().getParcelableExtra("entry");

        setContentView(R.layout.entry_webview);

        mWebView = (WebView) findViewById(R.id.webView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mHistoryBackButton = (Button) findViewById(R.id.historyBackButton);
        mHistoryForwardButton = (Button) findViewById(R.id.historyForwardButton);
        mBookmarkButton = (Button) findViewById(R.id.bookmarkButton);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(entry.getTitle());

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
                registerBookmark(entry);
            }
        });

        startWebView(entry);

        UserInfoManager.refreshMyTagsIfNeeded();
        entry.fetchRecommendTagsIfNeeded();
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
        intent.putExtra("entryUrl", entry.getLink());
        startActivity(intent);
    }
}
