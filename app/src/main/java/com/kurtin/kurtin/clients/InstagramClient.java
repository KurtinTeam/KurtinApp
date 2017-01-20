package com.kurtin.kurtin.clients;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.kurtin.kurtin.networking.InstagramApi;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/**
 * Created by cvar on 1/19/17.
 */

public class InstagramClient extends OAuthBaseClient {

    //Added constants
    public static final String SCHEME = "http";
    public static final String HOST = "kurtin.college.app.instagram.oauth.com";

    //Class dependant constants
    public static final Class<? extends Api> REST_API_CLASS = InstagramApi.class; // Change this
    public static final String REST_URL = "https://api.instagram.com/v1"; // Change this, base API URL
    public static final String REST_CONSUMER_KEY = "32b79b5450c041f692335f0f7ef07e14";       // Change this
    public static final String REST_CONSUMER_SECRET = "a383802ab54f4536967b52f471b2fff9"; // Change this
    public static final String REST_CALLBACK_URL = "http://kurtin.college.app.instagram.oauth.com"; // Change this (here and in manifest)

    public InstagramClient(Context context) {
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
