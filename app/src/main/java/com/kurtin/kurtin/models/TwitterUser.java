package com.kurtin.kurtin.models;

import android.content.Context;
import android.util.Log;

import com.facebook.FacebookRequestError;
import com.kurtin.kurtin.clients.FacebookClient;
import com.kurtin.kurtin.clients.TwitterClient;
import com.kurtin.kurtin.helpers.JsonHelper;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

/**
 * Created by cvar on 1/25/17.
 */

public class TwitterUser {

    public static final String TAG = "TwitterUser";

    public static final String NOT_AVAILABLE = "notAvailable";

    private String mTwitterId;
    private String mName;
    private String mScreenName;
//    private String mEmail;
    private String mProfilePic;
    private String mBackgroundPic;

    public TwitterUser(JSONObject twUserJsonObject){
        JsonHelper jsonHelper = new JsonHelper(twUserJsonObject);
        mTwitterId = jsonHelper.getString(TwApiKeys.ID, NOT_AVAILABLE);
        mName = jsonHelper.getString(TwApiKeys.NAME, NOT_AVAILABLE);
        mScreenName = jsonHelper.getString(TwApiKeys.SCREEN_NAME, NOT_AVAILABLE);
        mProfilePic = jsonHelper.getString(TwApiKeys.PROFILE_PIC, NOT_AVAILABLE);
        mBackgroundPic = jsonHelper.getString(TwApiKeys.BACKGROUND_PIC, NOT_AVAILABLE);
//        mEmail = jsonHelper.getString(FacebookUser.FbKeys.EMAIL, NOT_AVAILABLE);
    }

    public static void getCurrentUser(Context context, final TwitterUser.TwitterUserCallback callback){
        TwitterClient.getSharedInstance(context).getCurrentUser(context, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(
                    int statusCode, cz.msebera.android.httpclient.Header[] headers,
                    org.json.JSONObject response){
                Log.v(TAG, "Success response: " + response.toString());
                TwitterUser twitterUser = new TwitterUser(response);
                callback.onSuccess(twitterUser);
            }

            @Override
            public void onFailure(
                    int statusCode, cz.msebera.android.httpclient.Header[] headers,
                    java.lang.Throwable throwable, org.json.JSONObject errorResponse){
                Log.v(TAG, "Failure errorResponse: " + errorResponse.toString());
                callback.onFailure(errorResponse.toString());
            }
        });
    }

    public String getTwitterId() {
        return mTwitterId;
    }

    public String getName() {
        return mName;
    }

    public String getScreenName() {
        return mScreenName;
    }

    public String getProfilePic() {
        return mProfilePic;
    }

    public String getBackgroundPic() {
        return mBackgroundPic;
    }

    private static final class TwApiKeys{
        static final String ID = "id_str";
        static final String NAME = "name";
        static final String SCREEN_NAME = "screen_name";
        static final String PROFILE_PIC = "profile_image_url_https";
        static final String BACKGROUND_PIC = "profile_background_image_url_https";
//        static final String EMAIL = "email";
//        static final String LOCATION = "location";
    }

    public interface TwitterUserCallback{
        void onSuccess(TwitterUser twitterUser);
        void onFailure(String errorString);
    }
}
