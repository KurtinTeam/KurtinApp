package com.kurtin.kurtin.models;

import android.content.Context;
import android.util.Log;

import com.kurtin.kurtin.clients.InstagramClient;
import com.kurtin.kurtin.clients.TwitterClient;
import com.kurtin.kurtin.helpers.JsonHelper;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

/**
 * Created by cvar on 1/25/17.
 */

public class InstagramUser {

    public static final String TAG = "InstagramUser";

    public static final String NOT_AVAILABLE;

    private String mInstagramId;
    private String mFullName;
    private String mUsername;
    private String mProfilePic;

    static {
        NOT_AVAILABLE = KurtinUser.NOT_AVAILABLE;
    }

    public InstagramUser(JSONObject igUserJsonObject){
        JsonHelper jsonHelper = new JsonHelper(igUserJsonObject);

        if (jsonHelper.getJsonObject(IgApiKeys.DATA) instanceof JSONObject) {
            jsonHelper.setRootJsonObject(jsonHelper.getJsonObject(IgApiKeys.DATA));
        }else{
            Log.e(TAG, "Json data could not be parsed correctly");
        }

        mInstagramId = jsonHelper.getString(IgApiKeys.ID, NOT_AVAILABLE);
        mFullName = jsonHelper.getString(IgApiKeys.FULL_NAME, NOT_AVAILABLE);
        mUsername = jsonHelper.getString(IgApiKeys.USERNAME, NOT_AVAILABLE);
        mProfilePic = jsonHelper.getString(IgApiKeys.PROFILE_PICTURE, NOT_AVAILABLE);
    }

    public static void getCurrentUser(Context context, final InstagramUser.InstagramUserCallback callback){
        InstagramClient.getSharedInstance(context).getCurrentUser(context, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(
                    int statusCode, cz.msebera.android.httpclient.Header[] headers,
                    org.json.JSONObject response){
                Log.v(TAG, "Success response: " + response.toString());
                InstagramUser instagramUser = new InstagramUser(response);
                callback.onSuccess(instagramUser);
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

    public String getInstagramId() {
        return mInstagramId;
    }

    public String getFullName() {
        return mFullName;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getProfilePic() {
        return mProfilePic;
    }

    private static final class IgApiKeys{
        static final String DATA = "data";

        static final String ID = "id";
        static final String FULL_NAME = "full_name";
        static final String USERNAME = "username";
        static final String PROFILE_PICTURE = "profile_picture";
    }

    public interface InstagramUserCallback{
        void onSuccess(InstagramUser instagramUser);
        void onFailure(String errorString);
    }

}
