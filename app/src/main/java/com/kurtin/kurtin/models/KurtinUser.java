package com.kurtin.kurtin.models;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.kurtin.kurtin.clients.TwitterClient;
import com.kurtin.kurtin.helpers.SecureIdGenerator;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.parse.ParseException;

import java.util.List;

import static com.kurtin.kurtin.helpers.JsonHelper.TAG;
import static com.loopj.android.http.AsyncHttpClient.log;

/**
 * Created by cvar on 1/24/17.
 */

public class KurtinUser {

    public static final String TAG = "KurtinUser";
    public static final String IS_PRIMARY_LOGIN_KEY = "isPrimaryLoginKey";
    public static final String PRIMARY_LOGIN_KEY = "primaryLoginKey";
    public static final String NOT_AVAILABLE = "notAvailable";

    private String mUsername;
    private String mPassword;
    private String mEmail;
    private final AuthenticationPlatform mAuthenticationPlatform;
    private String mPlatformId;
    private String mNickName;
    private String mPrimaryToken;

    public KurtinUser(FacebookUser facebookUser){
        mEmail = facebookUser.getEmail();
        mAuthenticationPlatform = AuthenticationPlatform.FACEBOOK;
        mPlatformId = facebookUser.getFacebookId();
        mNickName = facebookUser.getName();
        mUsername = mAuthenticationPlatform.toString() + mPlatformId;
        mPassword = SecureIdGenerator.getGenerator().nextId();
        mPrimaryToken = AccessToken.getCurrentAccessToken().getToken();
    }

    public KurtinUser(TwitterUser twitterUser){
        mEmail = "NotAvailable@kurtin.herokuapp.com";
        mAuthenticationPlatform = AuthenticationPlatform.TWITTER;
        mPlatformId = twitterUser.getTwitterId();
        mNickName = twitterUser.getScreenName();
        mUsername = mAuthenticationPlatform.toString() + mPlatformId;
        mPassword = SecureIdGenerator.getGenerator().nextId();
        //TODO: get the access token from shared preferences. or require a context for this flow to retrieve the access token from the TwitterClient
//        mPrimaryToken = TwitterClient.getAccessToken();
    }

    public KurtinUser(InstagramUser instagramUser){
        mEmail = "NotAvailable@kurtin.herokuapp.com";
        mAuthenticationPlatform = AuthenticationPlatform.INSTAGRAM;
        mPlatformId = instagramUser.getInstagramId();
        mNickName = instagramUser.getUsername();
        mUsername = mAuthenticationPlatform.toString() + mPlatformId;
        mPassword = SecureIdGenerator.getGenerator().nextId();
        //TODO: get the access token from shared preferences. or require a context for this flow to retrieve the access token from the TwitterClient
//        mPrimaryToken = TwitterClient.getAccessToken();
    }

    public static void logInToKurtin(Context context, AuthenticationPlatform platform, KurtinLoginCallback callback){
        switch (platform){
            case FACEBOOK:
                KurtinLoginViaFacebookAsync(callback);
                break;
            case TWITTER:
                KurtinLoginViaTwitterAsync(context, callback);
                break;
            case INSTAGRAM:
                KurtinLoginViaInstagramAsync(context, callback);
                break;
        }
    }

    private static void KurtinLoginViaFacebookAsync(final KurtinLoginCallback callback){
        FacebookUser.getCurrentUser(new FacebookUser.FacebookUserCallback() {
            @Override
            public void onSuccess(FacebookUser facebookUser) {
                KurtinUser kurtinUser = new KurtinUser(facebookUser);
                kurtinUser.logInToKurtin(callback);
                //TODO: save kurtinUser to shared prefs
            }

            @Override
            public void onFailure(FacebookRequestError error) {
                callback.loginResult(error.toString());
            }
        });
    }

    private static void KurtinLoginViaTwitterAsync(Context context, final KurtinLoginCallback callback){
        TwitterUser.getCurrentUser(context, new TwitterUser.TwitterUserCallback() {
            @Override
            public void onSuccess(TwitterUser twitterUser) {
                KurtinUser kurtinUser = new KurtinUser(twitterUser);
                kurtinUser.logInToKurtin(callback);
                //TODO: save kurtinUser to shared prefs
            }

            @Override
            public void onFailure(String errorString) {
                callback.loginResult(errorString);
            }
        });
    }

    private static void KurtinLoginViaInstagramAsync(Context context, final KurtinLoginCallback callback){
        InstagramUser.getCurrentUser(context, new InstagramUser.InstagramUserCallback() {
            @Override
            public void onSuccess(InstagramUser instagramUser) {
                KurtinUser kurtinUser = new KurtinUser(instagramUser);
                kurtinUser.logInToKurtin(callback);
                //TODO: save kurtinUser to shared prefs
            }

            @Override
            public void onFailure(String errorString) {
                callback.loginResult(errorString);
            }
        });
    }

    private void logInToKurtin(final KurtinLoginCallback callback){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(ParseUserKeys.USERNAME, mUsername);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    // The query was successful.
                    //Do something with User
                    //TODO: Save user into shared preferences
                    Log.v(TAG, "Returning, user is already signed up with Kurtin");

                } else {
                    // Something went wrong.
                    if(e.getCode() == ParseException.OBJECT_NOT_FOUND){
                        signUpAsNewUser(callback);
                    }else {
//                        Log.v(TAG, "e: " + e.getCode());
                        e.printStackTrace();
                        callback.loginResult(e.toString());
                    }
                }
            }
        });
    }

    private void signUpAsNewUser(final KurtinLoginCallback callback){
        ParseUser parseUser = new ParseUser();
        parseUser.setUsername(mUsername);
        parseUser.setPassword(mPassword);
        parseUser.setEmail(mEmail);
        parseUser.put(ParseUserKeys.NICK_NAME, mNickName);
        parseUser.put(ParseUserKeys.AUTH_PLATFORM, mAuthenticationPlatform.toString());
        parseUser.put(ParseUserKeys.PLATFORM_ID, mPlatformId);

        parseUser.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    String result = "User signed up successfully";
                    Log.v(TAG, result);
                    callback.loginResult(result);
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    String result = "Error when signing up new user";
                    Log.v(TAG, result);
                    e.printStackTrace();
                    result += "\n" + e.toString();
                    callback.loginResult(result);
                }
            }
        });
    }

    public enum AuthenticationPlatform{
        FACEBOOK, TWITTER, INSTAGRAM;

        public static AuthenticationPlatform getPlatformFromString(String platformString){
            switch (platformString) {
                case "FACEBOOK":
                    return FACEBOOK;
                case "TWITTER":
                    return TWITTER;
                case "INSTAGRAM":
                    return INSTAGRAM;
                default:
                    return null;
            }
        }
    }

    public static final class AuthenticationPlatformStrings{
        public static String FACEBOOK = AuthenticationPlatform.FACEBOOK.toString();
        public static String TWITTER = AuthenticationPlatform.TWITTER.toString();
        public static String INSTAGRAM = AuthenticationPlatform.INSTAGRAM.toString();
    }

    public class ParseUserKeys{
        public static final String NICK_NAME = "nickName";
        public static final String AUTH_PLATFORM = "authPlatform";
        public static final String PLATFORM_ID = "platformId";

        public static final String USERNAME = "username";
    }

    public interface KurtinUserCallback{
        void onSuccess(KurtinUser kurtinUser);
        void onFailure(String errorString);
    }

    public interface KurtinLoginCallback{
        void loginResult(String result);
    }
}
