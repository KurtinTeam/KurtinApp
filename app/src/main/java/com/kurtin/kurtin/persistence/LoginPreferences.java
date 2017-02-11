package com.kurtin.kurtin.persistence;

import android.content.Context;

import com.kurtin.kurtin.models.AuthPlatform;
import com.kurtin.kurtin.models.KurtinUser;

/**
 * Created by cvar on 1/25/17.
 */

public class LoginPreferences {

    public static final String TAG = "LoginPreferences";
    private static final String PREFERENCES_FILE = "LoginPreferences";
    private static final String PRIMARY_LOGIN_KEY = "primaryLoginKey";


    public static AuthPlatform.PlatformType getPrimaryKurtinLoginPlatformType(Context context){
        String platformString = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .getString(PRIMARY_LOGIN_KEY, KurtinUser.NOT_AVAILABLE);

        return AuthPlatform.PlatformType.getTypeFromString(platformString);
    }

    public static void setPrimaryKurtinLoginPlatformType(Context context, AuthPlatform.PlatformType platformType){
        context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .edit().putString(PRIMARY_LOGIN_KEY, platformType.toString()).apply();
    }
}
