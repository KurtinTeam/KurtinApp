package com.kurtin.kurtin.persistence;

import android.content.Context;

import com.kurtin.kurtin.models.KurtinUser;

/**
 * Created by cvar on 1/25/17.
 */

public class LoginPreferences {

    public static final String TAG = "LoginPreferences";
    public static final String PREFERENCES_FILE = "LoginPreferences";
    public static final String PRIMARY_LOGIN_KEY = "primaryLoginKey";


    public static KurtinUser.AuthenticationPlatform getPrimaryKurtinLoginPlatform(Context context){
        String platformString = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .getString(PRIMARY_LOGIN_KEY, KurtinUser.NOT_AVAILABLE);

        return KurtinUser.AuthenticationPlatform.getPlatformFromString(platformString);
    }

    public static void setPrimaryKurtinLoginPlatform(Context context, KurtinUser.AuthenticationPlatform platform){
        context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .edit().putString(PRIMARY_LOGIN_KEY, platform.toString()).apply();
    }
}
