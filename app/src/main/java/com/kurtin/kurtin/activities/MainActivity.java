package com.kurtin.kurtin.activities;

import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.kurtin.kurtin.R;
import com.kurtin.kurtin.clients.InstagramClient;
import com.kurtin.kurtin.clients.TwitterClient;
import com.kurtin.kurtin.fragments.FacebookLoginFragment;
import com.kurtin.kurtin.fragments.InstagramLoginFragment;
import com.kurtin.kurtin.fragments.LoginFragment;
import com.kurtin.kurtin.fragments.TestFragment;
import com.kurtin.kurtin.fragments.TwitterLoginFragment;
import com.kurtin.kurtin.listeners.AuthenticationListener;
import com.kurtin.kurtin.models.KurtinUser;
import com.kurtin.kurtin.persistence.LoginPreferences;


public class MainActivity extends AppCompatActivity implements
        LoginFragment.LoginFragmentListener,
        AuthenticationListener{

    private static final String TAG = "MainActivity";

    private FrameLayout flFragmentFrame;
    private FrameLayout flFragmentFrameFullScreen;

    private Boolean mLoginInProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        FacebookSdk.sdkInitialize(getApplicationContext());

        flFragmentFrame = (FrameLayout) findViewById(R.id.flFragmentFrame);
        flFragmentFrameFullScreen = (FrameLayout) findViewById(R.id.flFragmentFrameFullScreen);

        mLoginInProgress = false;

        //Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        showFirstFragment();

    }

    private void showFirstFragment(){
        //Debugging
        // Fetch the uri that was passed in (which exists if this is being returned from authorization flow)
        Uri uri = getIntent().getData();
        //Check if we are getting a callback from an Oauth service
        if (uri == null){
            //No callback. show home screen.
            LoginFragment loginFragment = LoginFragment.newInstance();
            showFragment(loginFragment, "loginFragment", false);
        }else{
            //Check if callback is from twitter
            if (uri.getScheme().equals(TwitterClient.SCHEME)){
               if(uri.getHost().equals(TwitterClient.HOST)){
                   Boolean loginIsInProgress = true;
                   TwitterLoginFragment twitterLoginFragment = TwitterLoginFragment.newInstance(loginIsInProgress);
                   showFragment(twitterLoginFragment, TwitterLoginFragment.TAG, false);
               }
            }
            //Check if callback is from Instagram
            else if (uri.getScheme().equals(InstagramClient.SCHEME)){
                if (uri.getHost().equals(InstagramClient.HOST)) {
                    Boolean loginIsInProgress = true;
                    InstagramLoginFragment instagramLoginFragment = InstagramLoginFragment.newInstance(loginIsInProgress);
                    showFragment(instagramLoginFragment, InstagramLoginFragment.TAG, false);
                }
            }
//            Log.v(TAG, "intent data: " + uri.toString());
//            Log.v(TAG, "intent data scheme: " + uri.getScheme());
//            Log.v(TAG, "intent data host: " + uri.getHost());
//            Log.v(TAG, "intent data authority: " + uri.getAuthority());
//            Log.v(TAG, "intent data path: " + uri.getPath());
        }

//        LoginFragment loginFragment = LoginFragment.newInstance();
//        showFragment(loginFragment, "loginFragment", false);
    }

    private void showFragment(Fragment fragment, String fragmentTag, Boolean showFullScreen){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        int viewId;

        if(showFullScreen){
            viewId = R.id.flFragmentFrameFullScreen;
            flFragmentFrameFullScreen.setVisibility(View.VISIBLE);
            flFragmentFrame.setVisibility(View.GONE);
        }else{
            viewId = R.id.flFragmentFrame;
            flFragmentFrame.setVisibility(View.VISIBLE);
            flFragmentFrameFullScreen.setVisibility(View.GONE);
        }

//        Log.v(TAG, "before ft.replace()");
        ft.replace(viewId, fragment, fragmentTag);
//        ft.addToBackStack(fragmentTag);
//        Log.v(TAG, "before ft.commit()");
        ft.commit();
//        Log.v(TAG, "after ft.commit()");
    }

    //*
    //**
    //***
    //Interface implementations
    //***
    //**
    //*

    //LoginFragment.Listener
    @Override
    public void onKurtinLoginRequested(KurtinUser.AuthenticationPlatform platform){
        //Save primary login platform data
        LoginPreferences.setPrimaryKurtinLoginPlatform(this, platform);

        boolean loginIsInProgress = false;
        switch (platform){
            case FACEBOOK:
                FacebookLoginFragment facebookLoginFragment = FacebookLoginFragment.newInstance();
                showFragment(facebookLoginFragment, FacebookLoginFragment.TAG, false);
                break;
            case TWITTER:
                TwitterLoginFragment twitterLoginFragment = TwitterLoginFragment.newInstance(loginIsInProgress);
                showFragment(twitterLoginFragment, TwitterLoginFragment.TAG, false);
                break;
            case INSTAGRAM:
                InstagramLoginFragment instagramLoginFragment = InstagramLoginFragment.newInstance(loginIsInProgress);
                showFragment(instagramLoginFragment, InstagramLoginFragment.TAG, false);
                break;
        }
    }

    @Override
    public void onAuthenticationRequested(KurtinUser.AuthenticationPlatform platform){
        boolean loginIsInProgress = false;
        switch (platform){
            case FACEBOOK:
                FacebookLoginFragment facebookLoginFragment = FacebookLoginFragment.newInstance();
                showFragment(facebookLoginFragment, FacebookLoginFragment.TAG, false);
                break;
            case TWITTER:
                TwitterLoginFragment twitterLoginFragment = TwitterLoginFragment.newInstance(loginIsInProgress);
                showFragment(twitterLoginFragment, TwitterLoginFragment.TAG, false);
                break;
            case INSTAGRAM:
                InstagramLoginFragment instagramLoginFragment = InstagramLoginFragment.newInstance(loginIsInProgress);
                showFragment(instagramLoginFragment, InstagramLoginFragment.TAG, false);
                break;
        }
    }

//    @Override
//    public void onTwitterLoginRequested(){
//        TwitterLoginFragment twitterLoginFragment = TwitterLoginFragment.newInstance();
//        showFragment(twitterLoginFragment, TwitterLoginFragment.TAG, false);
//    }
//
//    @Override
//    public void onInstagramLoginRequested(){
//        InstagramLoginFragment instagramLoginFragment = InstagramLoginFragment.newInstance();
//        showFragment(instagramLoginFragment, InstagramLoginFragment.TAG, false);
//    }
//
//    @Override
//    public void onFacebookLoginRequested(){
//        FacebookLoginFragment facebookLoginFragment = FacebookLoginFragment.newInstance();
//        showFragment(facebookLoginFragment, FacebookLoginFragment.TAG, false);
//    }

    @Override
    public void onTestRequested(){
        TestFragment testFragment = new TestFragment();
        showFragment(testFragment, TestFragment.TAG, false);
    }

    //AuthenticationListener
    @Override
    public void onAuthenticationCompleted(KurtinUser.AuthenticationPlatform platform){
        LoginFragment loginFragment = LoginFragment.newInstance();
        showFragment(loginFragment, "loginFragment", false);
    }

    @Override
    public Boolean loginInProgress(){
        return mLoginInProgress;
    }
}
