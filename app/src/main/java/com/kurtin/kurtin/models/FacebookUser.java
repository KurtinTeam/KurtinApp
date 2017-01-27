package com.kurtin.kurtin.models;

import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.kurtin.kurtin.clients.FacebookClient;
import com.kurtin.kurtin.helpers.JsonHelper;

import org.json.JSONObject;

/**
 * Created by cvar on 1/23/17.
 */

public class FacebookUser {

    public static final String TAG = "FacebookUser";

    public static final String NOT_AVAILABLE = "notAvailable";

    private String mFacebookId;
//    private String mFacebookThirdPartyId;
    private String mFirstName;
    private String mLastName;
    private String mEmail;
    private String mLink;

    public FacebookUser(JSONObject fbUserJsonObject){
        JsonHelper jsonHelper = new JsonHelper(fbUserJsonObject);
        mFacebookId = jsonHelper.getString(FbKeys.ID, NOT_AVAILABLE);
//        mFacebookThirdPartyId = jsonHelper.getString(FbKeys.THIRD_PARTY_ID, NOT_AVAILABLE);
        mFirstName = jsonHelper.getString(FbKeys.FIRST_NAME, NOT_AVAILABLE);
        mLastName = jsonHelper.getString(FbKeys.LAST_NAME, NOT_AVAILABLE);
        mEmail = jsonHelper.getString(FbKeys.EMAIL, NOT_AVAILABLE);
        mLink = jsonHelper.getString(FbKeys.LINK, NOT_AVAILABLE);
    }

    public static void getCurrentUser(final FacebookUser.FacebookUserCallback callback){
        FacebookClient.getSharedInstance().getCurrentUser(new FacebookClient.JsonCallback(){
            @Override
            public void onSuccess(JSONObject fbUserJsonObject) {
                FacebookUser facebookUser = new FacebookUser(fbUserJsonObject);
                callback.onSuccess(facebookUser);
            }

            @Override
            public void onFailure(FacebookRequestError error) {
                callback.onFailure(error);
            }
        });
    }

    public String getFacebookId() {
        return mFacebookId;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getLink() {
        return mLink;
    }

    public String getName(){
        String name = "";
        if(mFirstName != NOT_AVAILABLE){
            name += mFirstName;
        }
        if(mLastName != NOT_AVAILABLE){
            name += mLastName;
        }
        return name;
    }

    private static final class FbKeys{
        static final String ID = "id";
        static final String NAME = "name";
        static final String FIRST_NAME = "first_name";
        static final String LAST_NAME = "last_name";
        static final String LINK = "link";
        static final String EMAIL = "email";
        static final String LOCATION = "location";
        static final String THIRD_PARTY_ID = "third_party_id";
    }

    public interface FacebookUserCallback{
        void onSuccess(FacebookUser facebookUser);
        void onFailure(FacebookRequestError error);
    }
}
