package com.kurtin.kurtin.models;

import android.content.Context;
import android.util.Log;

import com.kurtin.kurtin.clients.FacebookClient;
import com.kurtin.kurtin.clients.InstagramClient;
import com.kurtin.kurtin.clients.TwitterClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cvar on 2/8/17.
 */

public class AuthPlatform {

    public static final String TAG = "AuthPlatform";

    private PlatformType mPlatformType;
    private Boolean mAuthenticated;

    public AuthPlatform(PlatformType platformType, Boolean authenticated){
        mPlatformType = platformType;
        mAuthenticated = authenticated;
    }

    public AuthPlatform(Context context, PlatformType platformType){
        mPlatformType = platformType;
        mAuthenticated = false;
        this.initializeAuthStatus(context);
    }

    public PlatformType getPlatformType() {
        return mPlatformType;
    }

    public Boolean isAuthenticated() {
        return mAuthenticated;
    }

    public void setAuthenticationStatus(boolean authenticationStatus){
        mAuthenticated = authenticationStatus;
    }

    public void initializeAuthStatus(Context context){
        switch (mPlatformType){
            case FACEBOOK:
                FacebookClient.getSharedInstance().checkAuthenticationStatus(new FacebookClient.BooleanCallback() {
                    @Override
                    public void result(boolean booleanResult) {
                        mAuthenticated = booleanResult;
                    }
                });
                return;
            case TWITTER:
                TwitterClient.getSharedInstance(context).checkAuthenticationStatus(new TwitterClient.BooleanCallback() {
                    @Override
                    public void result(boolean booleanResult) {
                        mAuthenticated = booleanResult;
                    }
                });
                return;
            case INSTAGRAM:
                InstagramClient.getSharedInstance(context).checkAuthenticationStatus(new InstagramClient.BooleanCallback() {
                    @Override
                    public void result(boolean booleanResult) {
                        mAuthenticated = booleanResult;
                    }
                });
                return;
            default:
                Log.e(TAG, "Unhandled PlatformType in initializeAuthStatus()");
        }
    }

    public enum PlatformType{
        FACEBOOK, TWITTER, INSTAGRAM;

        public static PlatformType getTypeFromString(String platformString){
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

        public static List<PlatformType> getPlatformList(){
            return new ArrayList<PlatformType>(){{
                add(FACEBOOK);
                add(TWITTER);
                add(INSTAGRAM);
            }};
        }


        public static final class AuthenticationPlatformStrings{
            public static String FACEBOOK = PlatformType.FACEBOOK.toString();
            public static String TWITTER = PlatformType.TWITTER.toString();
            public static String INSTAGRAM = PlatformType.INSTAGRAM.toString();
        }
    }
}
