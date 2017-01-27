package com.kurtin.kurtin.clients;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.kurtin.kurtin.networking.InstagramApi;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Token;

import static android.R.attr.id;
import static android.R.attr.name;
import static com.kurtin.kurtin.helpers.JsonHelper.TAG;
import static org.scribe.model.OAuthConstants.ACCESS_TOKEN;

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
        String apiUrl = getApiUrl("users/self");
        client.get(apiUrl, null, handler);
    }

    //My functions

    //Get the singleton instance by calling super classes getInstance method
    public static InstagramClient getSharedInstance(Context context){
        return ((InstagramClient) InstagramClient.getInstance(InstagramClient.class, context));
    }

    public void getCurrentUser(Context context, AsyncHttpResponseHandler handler){
        InstagramClient.getSharedInstance(context).getMe(handler);
    }

    public void checkAuthenticationStatus(Context context, final InstagramClient.BooleanCallback callback){
        getSharedInstance(context).getCurrentUser(context, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(
                    int statusCode, cz.msebera.android.httpclient.Header[] headers,
                    org.json.JSONObject response){
                callback.result(true);
            }

            @Override
            public void onFailure(
                    int statusCode, cz.msebera.android.httpclient.Header[] headers,
                    java.lang.Throwable throwable, org.json.JSONObject errorResponse){
                callback.result(false);
            }
        });
    }

    public void getMe(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("users/self");
        client.get(apiUrl, null, handler);
    }

    public void searchUsersWithQuery(String query, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("users/search");
        RequestParams params = new RequestParams();
        params.put(ParamKeys.QUERY, query);
        client.get(apiUrl, params, handler);
    }

    public void getMediaWithUserId(String userId, AsyncHttpResponseHandler handler){
        //Possible parameters: MAX_ID, MIN_ID, COUNT
        String apiUrl = getApiUrl("users/" + userId + "/media/recent");
        RequestParams params = new RequestParams();
        //TODO: initialize and use params properly
        params = null;
        client.get(apiUrl, params, handler);
    }

    public void searchTagsWithQuery(String query, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("tags/search");
        RequestParams params = new RequestParams();
        params.put(ParamKeys.QUERY, query);
        client.get(apiUrl, params, handler);
    }

    public void searchMediaWithTag(String tag, AsyncHttpResponseHandler handler){
        //Possible parameters: MIN_TAG_ID, MAX_TAG_ID, COUNT
        String apiUrl = getApiUrl("tags/" + tag + "/media/recent");
        RequestParams params = new RequestParams();
        //TODO: initialize and use params properly
        params = null;
        client.get(apiUrl, params, handler);
    }


    //Returns null if there is no token
    public static String getAccessToken(Context context){
        Token token = InstagramClient.getInstance(InstagramClient.class, context).checkAccessToken();
        String string;
        if(token != null){
            string = token.getToken();
        }else{
            string = null;
        }
        Log.v(TAG, "Token: " + string);
        return string;
    }

    public interface BooleanCallback{
        void result(boolean booleanResult);
    }

    private class ParamKeys{
        public static final String QUERY = "q";
    }

}
