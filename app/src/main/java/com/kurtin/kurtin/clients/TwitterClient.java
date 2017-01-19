package com.kurtin.kurtin.clients;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/**
 * Created by cvar on 1/13/17.
 */

public class TwitterClient extends OAuthBaseClient {
    //Added constants
    public static final String SCHEME = "oauth";
    public static final String HOST = "kurtin.twitter.com";

    //Class dependant constants
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
    public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
    public static final String REST_CONSUMER_KEY = "SeSJfeLoJez2e5uWiKm3wildX";       // Change this
    public static final String REST_CONSUMER_SECRET = "sc7wk6aCCMUjvyqg7WdqyxQ6bOf2uq4CoEgW79iAB2NFpFIRS3"; // Change this
    public static final String REST_CALLBACK_URL = "oauth://kurtin.twitter.com"; // Change this (here and in manifest)

    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }

    public void getHomeTimeline(AsyncHttpResponseHandler handler) {
        String url = getApiUrl("statuses/home_timeline.json");
        Log.d("DEBUG", "Sending API call to " + url);
        client.get(url, null, handler);
    }

    public void getMentions(AsyncHttpResponseHandler handler) {
        String url = getApiUrl("statuses/mentions_timeline.json");
        client.get(url, null, handler);
    }

    public void getUserTimeline(AsyncHttpResponseHandler handler) {
        String url = getApiUrl("statuses/user_timeline.json");
        client.get(url, null, handler);
    }

    public void getMyInfo(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");
        client.get(apiUrl, null, handler);
    }

}
