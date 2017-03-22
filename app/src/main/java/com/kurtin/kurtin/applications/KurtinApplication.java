package com.kurtin.kurtin.applications;

import android.app.Application;
import android.content.res.Configuration;

import com.facebook.FacebookSdk;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.kurtin.kurtin.R;
import com.kurtin.kurtin.clients.InstagramClient;
import com.kurtin.kurtin.clients.TwitterClient;
import com.kurtin.kurtin.models.BaseAd;
import com.kurtin.kurtin.models.Category;
import com.kurtin.kurtin.models.CategoryJoin;
import com.kurtin.kurtin.models.School;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;

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
        //Make parse objects public
        ParseACL defaultACL = new ParseACL();
        // If you would like all objects to be private by default, remove this line.
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
        //Parse Objects
        ParseObject.registerSubclass(CategoryJoin.class);
        ParseObject.registerSubclass(BaseAd.class);
        ParseObject.registerSubclass(Category.class);
        ParseObject.registerSubclass(School.class);
        // Enable Local Datastore.
//        Parse.enableLocalDatastore(this);
        //Parse initialization
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.parse_app_id))
                .server(getString(R.string.parse_server_url))
                .clientKey(getString(R.string.parse_client_key))
                .enableLocalDataStore()
                .build()
        );
        // Initialization code here
//        Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
        // ParseUser.enableAutomaticUser();

        //Facebook
        // Initialize Facebook Integration
        FacebookSdk.sdkInitialize(getApplicationContext());

        //Fresco
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true)
                .build();
        Fresco.initialize(this);

        //Twitter Client
        TwitterClient.initialize(this);

        //Instagram Client
        InstagramClient.initialize(this);

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
