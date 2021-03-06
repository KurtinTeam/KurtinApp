package com.kurtin.kurtin.clients;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.kurtin.kurtin.R;
import com.kurtin.kurtin.models.AuthPlatform;
import com.kurtin.kurtin.models.KurtinUser;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Token;

import java.util.List;

import static com.kurtin.kurtin.helpers.JsonHelper.TAG;

/**
 * Created by cvar on 1/13/17.
 */

public class TwitterClient extends OAuthBaseClient {
    //Added constants
    public static final String SCHEME = "oauth";  //Need to match REST_CALLBACK_URL below
    public static final String HOST = "kurtin.twitter.com"; //Need to match REST_CALLBACK_URL below

    private static final AuthPlatform.PlatformType TWITTER_PLATFORM = AuthPlatform.PlatformType.TWITTER;

    //Class out of the box dependant constants
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
    public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
    public static final String REST_CONSUMER_KEY = "";       // Change this
    public static final String REST_CONSUMER_SECRET = ""; // Change this
    public static final String REST_CALLBACK_URL = "oauth://kurtin.twitter.com"; // Change this (here and in manifest)

    private static String consumerKey;
    private static String consumerSecret;

    public static void initialize(Context context){
        consumerKey = context.getString(R.string.twitter_consumer_key);
        consumerSecret = context.getString(R.string.twitter_consumer_secret);
    }

    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, consumerKey, consumerSecret, REST_CALLBACK_URL);
//        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
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

    //Get the singleton instance by calling super classes getInstance method
    public static TwitterClient getSharedInstance(Context context){
        return ((TwitterClient) TwitterClient.getInstance(TwitterClient.class, context));
    }

    public void logOut(){
        clearAccessToken();
        KurtinUser.setAuthenticationStatus(context, TWITTER_PLATFORM, false);
    }

    public void getCurrentUser(AsyncHttpResponseHandler handler){
        getMe(handler);
//        ((TwitterClient) TwitterClient.getInstance(TwitterClient.class, context)).getMe(handler);
    }

    public void checkAuthenticationStatus(final TwitterClient.BooleanCallback callback){
        if(hasAccessToken()) {
            getCurrentUser(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(
                        int statusCode, cz.msebera.android.httpclient.Header[] headers,
                        org.json.JSONObject response) {
                    callback.result(true);
                }

                @Override
                public void onFailure(
                        int statusCode, cz.msebera.android.httpclient.Header[] headers,
                        java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                    callback.result(false);
                }
            });
        }else{
            callback.result(false);
        }
    }

    public void getMe(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");
        client.get(apiUrl, null, handler);
    }

    public void getUser(String user_id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("users/show.json");
        RequestParams params = new RequestParams();
        params.put(ParamKeys.USER_ID, user_id);
        client.get(apiUrl, params, handler);
    }

    public void getTimelineWithUserId(String user_id, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        params.put(ParamKeys.USER_ID, user_id);
        client.get(apiUrl, params, handler);
    }

    public void getTimelineWithScreenName(String screenName, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        params.put(ParamKeys.SCREEN_NAME, screenName);
        client.get(apiUrl, params, handler);
    }

    public void searchTweetsWithAccount(String account, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("search/tweets.json");
        RequestParams params = new RequestParams();
        String accountParam = "@" + account;
        params.put(ParamKeys.QUERY, accountParam);
        client.get(apiUrl, params, handler);
    }

    public void searchTweetsWithHashtags(List<String> hashtags, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("search/tweets.json");
        RequestParams params = new RequestParams();
        String hashtagsParam = "";
        final String OR = " OR ";

        //Convert list of tags to formated parameter
        for (String hashtag: hashtags){
            hashtagsParam += "#" + hashtag + OR;
        }

        //remove teh trailing " OR "
        int length = hashtagsParam.length();
        int beginIndex = 0;
        int endIndex = length - OR.length();
        hashtagsParam = hashtagsParam.substring(beginIndex, endIndex);

        params.put(ParamKeys.QUERY, hashtagsParam);
        client.get(apiUrl, params, handler);
    }

    //Returns null if there is no token
    public String getAccessToken(){
        Token token = checkAccessToken();
        String string;
        if(token != null){
            string = token.getToken();
        }else{
            string = null;
        }
        Log.v(TAG, "Token: " + string);
        return string;
    }

    public boolean hasAccessToken(){
        return checkAccessToken() != null;
    }

    public interface BooleanCallback{
        void result(boolean booleanResult);
    }

    private class ParamKeys{
        public static final String USER_ID = "user_id";
        public static final String SCREEN_NAME = "screen_name";
        public static final String QUERY = "q";
    }

}
