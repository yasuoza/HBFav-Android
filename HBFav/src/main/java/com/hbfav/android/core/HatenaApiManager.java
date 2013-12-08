package com.hbfav.android.core;


import com.hbfav.android.Constants;
import com.hbfav.android.model.HatenaApi;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

public class HatenaApiManager {
    private static OAuthService mService;
    private static Token mRequestToken;

    public static OAuthService getService() {
        if (mService == null) {
            mService = new ServiceBuilder()
                    .provider(HatenaApi.class)
                    .apiKey(Constants.HATENA_API_KEY)
                    .apiSecret(Constants.HATENA_API_KEY_SECRET)
                    .callback(HatenaApi.CALLBACK_URL)
                    .build();
        }
        return mService;
    }

    public static Token getRequestToken() {
        if (mRequestToken == null) {
            mRequestToken = getService().getRequestToken();
        }
        return mRequestToken;
    }

    public static String getAuthorizationurl() {
        return getService().getAuthorizationUrl(getRequestToken());
    }

    public static Token getAccessToken(Verifier verifier) {
        return getService().getAccessToken(getRequestToken(), verifier);
    }
}
