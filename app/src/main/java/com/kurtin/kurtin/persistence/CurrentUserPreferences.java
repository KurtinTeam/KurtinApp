package com.kurtin.kurtin.persistence;

import android.content.Context;
import android.util.Log;

import com.kurtin.kurtin.models.AuthPlatform;
import com.kurtin.kurtin.models.KurtinUser;

import static com.kurtin.kurtin.models.KurtinUser.PRIMARY_LOGIN_KEY;

/**
 * Created by cvar on 2/9/17.
 */

public class CurrentUserPreferences {
//    private String mUsername;
//    private String mPassword;
//    private String mEmail;
//    private final AuthPlatform.PlatformType mAuthPlatformType;
//    private String mPlatformId;
//    private String mFacebookId;
//    private String mTwitterId;
//    private String mInstagramId;
//    private String mNickName;

    public static final String TAG = "CurrentUserPreferences";

    //Pref filename
    private static final String PREFERENCES_FILE = "CurrentUserPreferences";
    //Pref keys
    private static final String PRIMARY_LOGIN_PLATFORM_KEY = "primaryLoginPlatformKey";
    private static final String USERNAME_KEY = "usernameKey";
    private static final String EMAIL_KEY = "emailKey";
    private static final String PRIMARY_PLATFORM_ID_KEY = "primaryPlatformIdKey";
    private static final String FACEBOOK_ID_KEY = "facebookIdKey";
    private static final String TWITTER_ID_KEY = "twitterIdKey";
    private static final String INSTAGRAM_ID_KEY = "instagramIdKey";
    private static final String NICKNAME_KEY = "nicknameKey";
    private static final String USER_IS_LOGGED_IN_KEY = "userIsLoggedInKey";


    //Platform Type
    public static AuthPlatform.PlatformType getPrimaryKurtinLoginPlatformType(Context context){
        String platformString = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .getString(PRIMARY_LOGIN_PLATFORM_KEY, null);

        return AuthPlatform.PlatformType.getTypeFromString(platformString);
    }
    public static void setPrimaryKurtinLoginPlatformType(Context context, AuthPlatform.PlatformType platformType){
        if(platformType == null){
            context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                    .edit().putString(PRIMARY_LOGIN_PLATFORM_KEY, null).apply();
        }else {
            context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                    .edit().putString(PRIMARY_LOGIN_PLATFORM_KEY, platformType.toString()).apply();
        }
    }

    //Username
    //**********
    public static String getUsername(Context context){
        return context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .getString(USERNAME_KEY, null);
    }
    public static void setUsername(Context context, String username){
        context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .edit().putString(USERNAME_KEY, username).apply();
    }

    //Email
    //**********
    public static String getEmail(Context context){
        return context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .getString(EMAIL_KEY, null);
    }
    public static void setEmail(Context context, String email){
        context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .edit().putString(EMAIL_KEY, email).apply();
    }

    //Primary platform Id
    //**********
    public static String getPrimaryPlatformId(Context context){
        return context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .getString(PRIMARY_PLATFORM_ID_KEY, null);
    }
    public static void setPrimaryPlatformId(Context context, String platformId){
        context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .edit().putString(PRIMARY_PLATFORM_ID_KEY, platformId).apply();
    }

    //Facebook Id
    //**********
    public static String getFacebookId(Context context){
        return context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .getString(FACEBOOK_ID_KEY, null);
    }
    public static void setFacebookId(Context context, String facebookId){
        context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .edit().putString(FACEBOOK_ID_KEY, facebookId).apply();
    }

    //Twitter Id
    //**********
    public static String getTwitterId(Context context){
        return context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .getString(TWITTER_ID_KEY, null);
    }
    public static void setTwitterId(Context context, String twitterId){
        context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .edit().putString(TWITTER_ID_KEY, twitterId).apply();
    }

    //Instagram Id
    //**********
    public static String getInstagramId(Context context){
        return context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .getString(INSTAGRAM_ID_KEY, null);
    }
    public static void setInstagramId(Context context, String instagramId){
        context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .edit().putString(INSTAGRAM_ID_KEY, instagramId).apply();
    }

    //Nickname
    //**********
    public static String getNickname(Context context){
        return context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .getString(NICKNAME_KEY, null);
    }
    public static void setNickname(Context context, String nickname){
        context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .edit().putString(NICKNAME_KEY, nickname).apply();
    }

    //Logged in
    //**********
    public static Boolean userIsLoggedIn(Context context){
        return context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .getBoolean(USER_IS_LOGGED_IN_KEY, false);
    }
    public static void setUserLoginStatus(Context context, boolean loginStatus){
        context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .edit().putBoolean(USER_IS_LOGGED_IN_KEY, loginStatus).apply();
    }

    //Get id with platform type
    public static String getIdWithPlatformType(Context context, AuthPlatform.PlatformType platformType){
        switch (platformType){
            case FACEBOOK:
                return context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                        .getString(FACEBOOK_ID_KEY, null);
            case TWITTER:
                return context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                        .getString(TWITTER_ID_KEY, null);
            case INSTAGRAM:
                return context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                        .getString(INSTAGRAM_ID_KEY, null);
            default:
                Log.e(TAG, "Unhandled platformType in getIdWithPlatformType(Context context, AuthPlatform.PlatformType platformType)");
                return null;
        }
    }
}
