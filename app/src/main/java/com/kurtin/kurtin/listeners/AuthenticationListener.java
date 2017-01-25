package com.kurtin.kurtin.listeners;

import com.kurtin.kurtin.models.KurtinUser;

/**
 * Created by cvar on 1/19/17.
 */

public interface AuthenticationListener {

    public void onAuthenticationCompleted(KurtinUser.AuthenticationPlatform platform);

    public Boolean loginInProgress();

}
