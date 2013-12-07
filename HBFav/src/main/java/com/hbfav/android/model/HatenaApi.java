package com.hbfav.android.model;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;

public class HatenaApi extends DefaultApi10a {

    public static final String CALLBACK_URL = "http://b.hatena.ne.jp/";
    public static final String OAUTH_VERIFIER = "oauth_verifier";

    private static final String AUTHORIZE_URL = "https://www.hatena.ne.jp/touch/oauth/authorize?oauth_token=%s";
    private static final String REQUEST_TOKEN_RESOURCE = "www.hatena.com/oauth/initiate?scope=%s";
    private static final String ACCESS_TOKEN_RESOURCE = "www.hatena.com/oauth/token";
    private static final String ACCESS_SCOPE = "read_public,write_public";

    @Override
    public String getAccessTokenEndpoint() {
        return "https://" + ACCESS_TOKEN_RESOURCE;
    }

    @Override
    public String getRequestTokenEndpoint() {
        return String.format("https://" + REQUEST_TOKEN_RESOURCE, ACCESS_SCOPE);
    }

    @Override
    public String getAuthorizationUrl(Token requestToken) {
        return String.format(AUTHORIZE_URL, requestToken.getToken());
    }
}
