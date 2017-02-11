package com.kurtin.kurtin.models;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.kurtin.kurtin.clients.FacebookClient;
import com.kurtin.kurtin.clients.InstagramClient;
import com.kurtin.kurtin.clients.TwitterClient;
import com.kurtin.kurtin.helpers.SecureIdGenerator;
import com.kurtin.kurtin.persistence.CurrentUserPreferences;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;
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

    private static List<AuthPlatform> userPlatforms;
    private static KurtinUser currentUser;

    private String mUsername;
    private String mPassword;
    private String mEmail;
    private final AuthPlatform.PlatformType mPrimaryPlatform;
    private String mPrimaryPlatformId;
    private String mFacebookId;
    private String mTwitterId;
    private String mInstagramId;
    private String mNickname;
    private String mPrimaryToken;

    //Fields Not saved to DB
    private Boolean mUserIsLoggedIn;


    public KurtinUser(FacebookUser facebookUser){
        mEmail = facebookUser.getEmail();
        mPrimaryPlatform = AuthPlatform.PlatformType.FACEBOOK;
        mPrimaryPlatformId = facebookUser.getFacebookId();
        mFacebookId = facebookUser.getFacebookId();
        mNickname = facebookUser.getName();
        mUsername = mPrimaryPlatform.toString() + mPrimaryPlatformId;
        mPassword = SecureIdGenerator.getGenerator().nextId();
        mPrimaryToken = AccessToken.getCurrentAccessToken().getToken();
        mUserIsLoggedIn = false;
    }

    public KurtinUser(TwitterUser twitterUser){
        mEmail = "NotAvailable@kurtin.herokuapp.com";
        mPrimaryPlatform = AuthPlatform.PlatformType.TWITTER;
        mPrimaryPlatformId = twitterUser.getTwitterId();
        mTwitterId = twitterUser.getTwitterId();
        mNickname = twitterUser.getScreenName();
        mUsername = mPrimaryPlatform.toString() + mPrimaryPlatformId;
        mPassword = SecureIdGenerator.getGenerator().nextId();
        mUserIsLoggedIn = false;
        //TODO: get the access token from shared preferences. or require a context for this flow to retrieve the access token from the TwitterClient
//        mPrimaryToken = TwitterClient.getAccessToken();
    }

    public KurtinUser(InstagramUser instagramUser){
        mEmail = "NotAvailable@kurtin.herokuapp.com";
        mPrimaryPlatform = AuthPlatform.PlatformType.INSTAGRAM;
        mPrimaryPlatformId = instagramUser.getInstagramId();
        mInstagramId = instagramUser.getInstagramId();
        mNickname = instagramUser.getUsername();
        mUsername = mPrimaryPlatform.toString() + mPrimaryPlatformId;
        mPassword = SecureIdGenerator.getGenerator().nextId();
        mUserIsLoggedIn = false;
        //TODO: get the access token from shared preferences. or require a context for this flow to retrieve the access token from the TwitterClient
//        mPrimaryToken = TwitterClient.getAccessToken();
    }

    // Make a KurtinUser object out of data in shared preferences
    private KurtinUser(Context context){
        mEmail = CurrentUserPreferences.getEmail(context);
        mPrimaryPlatform = CurrentUserPreferences.getPrimaryKurtinLoginPlatformType(context);
        mPrimaryPlatformId = CurrentUserPreferences.getPrimaryPlatformId(context);
        mFacebookId = CurrentUserPreferences.getFacebookId(context);
        mTwitterId = CurrentUserPreferences.getTwitterId(context);
        mInstagramId = CurrentUserPreferences.getInstagramId(context);
        mNickname = CurrentUserPreferences.getNickname(context);
        mUsername = CurrentUserPreferences.getUsername(context);
        mUserIsLoggedIn = CurrentUserPreferences.userIsLoggedIn(context);
    }

    public AuthPlatform.PlatformType getPrimaryPlatform() {
        return mPrimaryPlatform;
    }

    public boolean primaryPlatformEqualTo(AuthPlatform.PlatformType platformType){
        return mPrimaryPlatform.equals(platformType);
    }

    public void refreshKurtinLoginStatus(Context context) {
        switch (mPrimaryPlatform) {
            case FACEBOOK:
                FacebookClient.getSharedInstance().checkAuthenticationStatus(new FacebookClient.BooleanCallback() {
                    @Override
                    public void result(boolean booleanResult) {
                        mUserIsLoggedIn = booleanResult;
                    }
                });
                break;
            case TWITTER:
                TwitterClient.getSharedInstance(context).checkAuthenticationStatus(new TwitterClient.BooleanCallback() {
                    @Override
                    public void result(boolean booleanResult) {
                        mUserIsLoggedIn = booleanResult;
                    }
                });
                break;
            case INSTAGRAM:
                InstagramClient.getSharedInstance(context).checkAuthenticationStatus(new InstagramClient.BooleanCallback() {
                    @Override
                    public void result(boolean booleanResult) {
                        mUserIsLoggedIn = booleanResult;
                    }
                });
                break;
        }
    }

    public static void logInToKurtin(Context context, AuthPlatform.PlatformType platformType, KurtinLoginCallback callback){
        switch (platformType){
            case FACEBOOK:
                KurtinLoginViaFacebookAsync(context, callback);
                break;
            case TWITTER:
                KurtinLoginViaTwitterAsync(context, callback);
                break;
            case INSTAGRAM:
                KurtinLoginViaInstagramAsync(context, callback);
                break;
        }
    }

    public void logOutOfKurtin(Context context){
        FacebookClient.getSharedInstance().logOut(context);
        TwitterClient.getSharedInstance(context).logOut();
        InstagramClient.getSharedInstance(context).logOut();
        clearUserFromSharedPreferences(context);
        currentUser = null;
    }

    public void logOut(Context context, AuthPlatform.PlatformType platformType){
        switch (platformType){
            case FACEBOOK:
                FacebookClient.getSharedInstance().logOut(context);
                break;
            case TWITTER:
                TwitterClient.getSharedInstance(context).logOut();
                break;
            case INSTAGRAM:
                InstagramClient.getSharedInstance(context).logOut();
                break;
            default:
                Log.e(TAG, "Unhandled platformType in logOut(AuthPlatform.PlatformType platformType)");
        }
    }

    private static void KurtinLoginViaFacebookAsync(final Context context, final KurtinLoginCallback callback){
        FacebookUser.getCurrentUser(new FacebookUser.FacebookUserCallback() {
            @Override
            public void onSuccess(FacebookUser facebookUser) {
                KurtinUser kurtinUser = new KurtinUser(facebookUser);
                kurtinUser.logInToKurtin(context, callback);
                //TODO: save kurtinUser to shared prefs
            }

            @Override
            public void onFailure(FacebookRequestError error) {
                callback.loginResult(error.toString());
            }
        });
    }

    private static void KurtinLoginViaTwitterAsync(final Context context, final KurtinLoginCallback callback){
        TwitterUser.getCurrentUser(context, new TwitterUser.TwitterUserCallback() {
            @Override
            public void onSuccess(TwitterUser twitterUser) {
                KurtinUser kurtinUser = new KurtinUser(twitterUser);
                kurtinUser.logInToKurtin(context, callback);
                //TODO: save kurtinUser to shared prefs
            }

            @Override
            public void onFailure(String errorString) {
                callback.loginResult(errorString);
            }
        });
    }

    private static void KurtinLoginViaInstagramAsync(final Context context, final KurtinLoginCallback callback){
        InstagramUser.getCurrentUser(context, new InstagramUser.InstagramUserCallback() {
            @Override
            public void onSuccess(InstagramUser instagramUser) {
                KurtinUser kurtinUser = new KurtinUser(instagramUser);
                kurtinUser.logInToKurtin(context, callback);
                //TODO: save kurtinUser to shared prefs
            }

            @Override
            public void onFailure(String errorString) {
                callback.loginResult(errorString);
            }
        });
    }

    private void logInToKurtin(final Context context, final KurtinLoginCallback callback){
        String loginQueryKey = getUserQueryKey(mPrimaryPlatform);
        String loginQueryValue = getUserQueryValue();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(loginQueryKey, loginQueryValue);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null) {
                    // The query was successful.
                    syncUser(parseUser);
                    saveUserToSharedPreferences(context);
                    String resultString = "Returning User. Login successful.";
                    Log.v(TAG, resultString);
                    callback.loginResult(resultString);
                } else {
                    // First time login.
                    if(e.getCode() == ParseException.OBJECT_NOT_FOUND){
                        signUpAsNewUser(context, callback);
                    }else {
                        // Something went wrong.
//                        Log.v(TAG, "e: " + e.getCode());
                        e.printStackTrace();
                        callback.loginResult(e.toString());
                    }
                }
            }
        });
    }

    private void signUpAsNewUser(final Context context, final KurtinLoginCallback callback){
        ParseUser parseUser = new ParseUser();
        parseUser.setUsername(mUsername);
        parseUser.setPassword(mPassword);
        parseUser.setEmail(mEmail);
        parseUser.put(ParseUserKeys.NICKNAME, mNickname);
        parseUser.put(ParseUserKeys.AUTH_PLATFORM_TYPE, mPrimaryPlatform.toString());
        parseUser.put(ParseUserKeys.PLATFORM_ID, mPrimaryPlatformId);

        switch (mPrimaryPlatform){
            case FACEBOOK:
                parseUser.put(ParseUserKeys.FACEBOOK_ID, mFacebookId);
                break;
            case TWITTER:
                parseUser.put(ParseUserKeys.TWITTER_ID, mTwitterId);
                break;
            case INSTAGRAM:
                parseUser.put(ParseUserKeys.INSTAGRAM_ID, mInstagramId);
                break;
            default:
                Log.e(TAG, "Unhandled platformType in signUpAsNewUser(final KurtinLoginCallback callback)");
        }

        parseUser.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    saveUserToSharedPreferences(context);
                    String result = "New User. Sign up successful";
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

    private static String getUserQueryKey(AuthPlatform.PlatformType platformType){
        return ParseUserKeys.USERNAME;
        //TODO: Uncomment code below to search for individual platform ids rather than primary platform id
//        switch (platformType){
//            case FACEBOOK:
//                return ParseUserKeys.FACEBOOK_ID;
//            case TWITTER:
//                return ParseUserKeys.TWITTER_ID;
//            case INSTAGRAM:
//                return ParseUserKeys.INSTAGRAM_ID;
//            default:
//                Log.e(TAG, "Unhandled platformType in signUpAsNewUser(final KurtinLoginCallback callback)");
//                return null;
//        }
    }

    private String getUserQueryValue(){
        return mUsername;
        //TODO: Uncomment code below to pull in other ids besides the primary platform id
//        switch (mPrimaryPlatform){
//            case FACEBOOK:
//                return mFacebookId;
//            case TWITTER:
//                return mTwitterId;
//            case INSTAGRAM:
//                return mInstagramId;
//            default:
//                Log.e(TAG, "Unhandled platformType in signUpAsNewUser(final KurtinLoginCallback callback)");
//                return null;
//        }
    }

    private void syncUser(ParseUser parseUser){
        mFacebookId = parseUser.getString(ParseUserKeys.FACEBOOK_ID);
        mTwitterId = parseUser.getString(ParseUserKeys.TWITTER_ID);
        mInstagramId = parseUser.getString(ParseUserKeys.INSTAGRAM_ID);
        mPrimaryPlatformId = parseUser.getString(ParseUserKeys.PLATFORM_ID);
        mNickname = parseUser.getString(ParseUserKeys.NICKNAME);
        //right now AuthPlatformType is final
//        String platformTypeString = parseUser.getString(ParseUserKeys.AUTH_PLATFORM_TYPE);
//        mAuthPlatformType = AuthPlatform.PlatformType.getTypeFromString(platformTypeString);
    }

    public static void updateKurtinUser(Context context, final AuthPlatform.PlatformType newPlatformType, final String newPlatformId){
        //Check if user is logged in
        if(CurrentUserPreferences.userIsLoggedIn(context)){
            //Pull user file from DB
            AuthPlatform.PlatformType primaryPlatformType = CurrentUserPreferences.getPrimaryKurtinLoginPlatformType(context);
            String userQueryKey = getUserQueryKey(primaryPlatformType);
            String userQueryValue = CurrentUserPreferences.getIdWithPlatformType(context, primaryPlatformType);
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo(userQueryKey, userQueryValue);
            query.getFirstInBackground(new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e == null) {
                        // The query was successful.
                        // Update user file.
                        String key = getUserQueryKey(newPlatformType);
                        user.put(key, newPlatformId);
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e==null){
                                    Log.v(TAG, "Update successful");
                                }else{
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        Log.e(TAG, "Requested a user update but user could not be found in DB");
                    }
                }
            });

        }else{
            Log.e(TAG, "Requested a user update but user is not logged in");
        }
    }

    private void saveUserToSharedPreferences(Context context){
        CurrentUserPreferences.setUserLoginStatus(context, true);
        CurrentUserPreferences.setEmail(context, mEmail);
        CurrentUserPreferences.setFacebookId(context, mFacebookId);
        CurrentUserPreferences.setTwitterId(context, mTwitterId);
        CurrentUserPreferences.setInstagramId(context, mInstagramId);
        CurrentUserPreferences.setNickname(context, mNickname);
        CurrentUserPreferences.setPrimaryKurtinLoginPlatformType(context, mPrimaryPlatform);
        CurrentUserPreferences.setPrimaryPlatformId(context, mPrimaryPlatformId);
    }

    private void clearUserFromSharedPreferences(Context context){
        CurrentUserPreferences.setUserLoginStatus(context, false);
        CurrentUserPreferences.setEmail(context, null);
        CurrentUserPreferences.setFacebookId(context, null);
        CurrentUserPreferences.setTwitterId(context, null);
        CurrentUserPreferences.setInstagramId(context, null);
        CurrentUserPreferences.setNickname(context, null);
        CurrentUserPreferences.setPrimaryKurtinLoginPlatformType(context, null);
        CurrentUserPreferences.setPrimaryPlatformId(context, null);
    }

    public static KurtinUser getCurrentUser(Context context){
        if(currentUser == null){
            if(CurrentUserPreferences.userIsLoggedIn(context)){
                currentUser = getUserFromSharedPrefs(context);
            }
        }
        return currentUser;
    }

    public static boolean currentUserIsLoggedIn(Context context){
        return getCurrentUser(context) != null;
    }

    private static KurtinUser getUserFromSharedPrefs(Context context){
        return new KurtinUser(context);
    }

    public static List<AuthPlatform> getUserPlatforms(final Context context){
        if (userPlatforms == null){
            userPlatforms = new ArrayList<AuthPlatform>(){{
                add(new AuthPlatform(context, AuthPlatform.PlatformType.FACEBOOK));
                add(new AuthPlatform(context, AuthPlatform.PlatformType.TWITTER));
                add(new AuthPlatform(context, AuthPlatform.PlatformType.INSTAGRAM));
            }};
        }
        return userPlatforms;
    }

    public static void setAuthenticationStatus(
            Context context, AuthPlatform.PlatformType platformType, boolean authenticationStatus){
        List<AuthPlatform> userPlatforms = getUserPlatforms(context);
        for (AuthPlatform authPlatform: userPlatforms) {
            if (authPlatform.getPlatformType().equals(platformType)){
                authPlatform.setAuthenticationStatus(authenticationStatus);
                return;
            }
        }
        Log.e(TAG, "Unhandled platformType in setAuthenticationStatus(Context, AuthPlatform.PlatformType, boolean)");
    }

    public class ParseUserKeys{
        public static final String NICKNAME = "nickname";
        public static final String AUTH_PLATFORM_TYPE = "authPlatform";
        public static final String PLATFORM_ID = "platformId";
        public static final String FACEBOOK_ID = "facebookId";
        public static final String TWITTER_ID = "twitterId";
        public static final String INSTAGRAM_ID = "instagramId";

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
