package com.kurtin.kurtin.listeners;

/**
 * Created by cvar on 1/19/17.
 */

public interface AuthenticationListener {

    public enum Platform{
        FACEBOOK, TWITTER, INSTAGRAM
    }

    public void onAuthenticationCompleted(Platform platform);

    public Boolean loginInProgress();

}
