package com.kurtin.kurtin.applications;

import android.app.Application;
import android.content.res.Configuration;

import com.facebook.FacebookSdk;
import com.kurtin.kurtin.R;
import com.parse.Parse;
import com.parse.ParseACL;

/**
 * Created by cvar on 1/23/17.
 */

public class KurtinApplication extends Application {
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!

        //Parse
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        //Make parse objects public
        ParseACL defaultACL = new ParseACL();
        // If you would like all objects to be private by default, remove this line.
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
        //Parse initialization
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.parse_app_id))
                .server(getString(R.string.parse_server_url))
                .clientKey(getString(R.string.parse_client_key))
                .build()
        );
        // Initialization code here
//        Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
        // ParseUser.enableAutomaticUser();

        //Facebook
        // Initialize Facebook Integration
        FacebookSdk.sdkInitialize(getApplicationContext());

    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//    }
}
