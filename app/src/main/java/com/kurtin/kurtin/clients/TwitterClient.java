package com.kurtin.kurtin.clients;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Token;

import static com.kurtin.kurtin.helpers.JsonHelper.TAG;

/**
 * Created by cvar on 1/13/17.
 */

public class TwitterClient extends OAuthBaseClient {
    //Added constants
    public static final String SCHEME = "oauth";  //Need to match REST_CALLBACK_URL below
    public static final String HOST = "kurtin.twitter.com"; //Need to match REST_CALLBACK_URL below

    //Class out of the box dependant constants
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

    //My functions
    public static void getCurrentUser(Context context, AsyncHttpResponseHandler handler){
        ((TwitterClient) TwitterClient.getInstance(TwitterClient.class, context)).getMe(handler);
    }

    public void getMe(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");
        client.get(apiUrl, null, handler);
    }

    //Returns null if there is no token
    public static String getAccessToken(Context context){
        Token token = TwitterClient.getInstance(TwitterClient.class, context).checkAccessToken();
        String string;
        if(token != null){
            string = token.getToken();
        }else{
            string = null;
        }
        Log.v(TAG, "Token: " + string);
        return string;
    }

}
