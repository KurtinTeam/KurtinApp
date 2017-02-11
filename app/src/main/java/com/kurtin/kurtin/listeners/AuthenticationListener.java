package com.kurtin.kurtin.listeners;

import com.kurtin.kurtin.models.AuthPlatform;
import com.kurtin.kurtin.models.KurtinUser;

/**
 * Created by cvar on 1/19/17.
 */

public interface AuthenticationListener {

    void onAuthenticationCompleted(AuthPlatform.PlatformType platformType);

    void onKurtinLoginRequested(AuthPlatform.PlatformType platformType);

    void onAuthenticationRequested(AuthPlatform.PlatformType platform);

    void onKurtinLogOutRequested();

    void logOut(AuthPlatform.PlatformType platformType);

    Boolean loginInProgress();

}
