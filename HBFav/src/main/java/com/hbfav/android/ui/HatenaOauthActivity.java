package com.hbfav.android.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.hbfav.android.R;
import com.hbfav.android.core.HatenaApiManager;
import com.hbfav.android.core.UserInfoManager;
import com.hbfav.android.model.HatenaApi;

import org.scribe.model.Token;
import org.scribe.model.Verifier;


public class HatenaOauthActivity extends Activity {

    private static final String TAG = HatenaOauthActivity.class.getPackage().toString();

    private ProgressBar mProgressBar;
    private WebView mWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();

        setContentView(R.layout.activity_hatena_authorization);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.clearCache(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(mWebChromeClient);

        startAuthorize();
    }

    private void startAuthorize() {
        (new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                return HatenaApiManager.getAuthorizationurl();
            }

            @Override
            protected void onPostExecute(String url) {
                mWebView.loadUrl(url);
            }
        }).execute();
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if ((url != null) && (url.startsWith(HatenaApi.CALLBACK_URL))) {
                mWebView.stopLoading();
                mWebView.setVisibility(View.INVISIBLE);
                Uri uri = Uri.parse(url);
                final Verifier verifier = new Verifier(uri.getQueryParameter(HatenaApi.OAUTH_VERIFIER));
                (new AsyncTask<Void, Void, Token>() {
                    @Override
                    protected Token doInBackground(Void... params) {
                        return HatenaApiManager.getAccessToken(verifier);
                    }

                    @Override
                    protected void onPostExecute(Token accessToken) {
                        UserInfoManager.setAccessToken(accessToken.getToken(), accessToken.getSecret());
                        finish();
                    }
                }).execute();
            } else {
                super.onPageStarted(view, url, favicon);
            }
        }
    };

    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            HatenaOauthActivity.this.mProgressBar.setProgress(newProgress);
            if (newProgress == 100) {
                HatenaOauthActivity.this.mProgressBar.setVisibility(View.GONE);
            }
        }
    };
}
