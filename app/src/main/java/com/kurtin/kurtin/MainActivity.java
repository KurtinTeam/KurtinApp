package com.kurtin.kurtin;

import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.kurtin.kurtin.clients.TwitterClient;
import com.kurtin.kurtin.fragments.LoginFragment;
import com.kurtin.kurtin.fragments.TwitterLoginFragment;

public class MainActivity extends AppCompatActivity implements
        LoginFragment.LoginFragmentListener {

    private static final String TAG = "MainActivity";

    private FrameLayout fragmentFrame;
    private FrameLayout fragmentFrameFullScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentFrame = (FrameLayout) findViewById(R.id.fragmentFrame);
        fragmentFrameFullScreen = (FrameLayout) findViewById(R.id.fragmentFrameFullScreen);

        //Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        showFirstFragment();

    }

    private void showFirstFragment(){
        //Debugging
        // Fetch the uri that was passed in (which exists if this is being returned from authorization flow)
        Uri uri = getIntent().getData();
        if (uri == null){
            LoginFragment loginFragment = LoginFragment.newInstance();
            showFragment(loginFragment, "loginFragment", false);
        }else{
            if (uri.getScheme().equals(TwitterClient.SCHEME)){
               if(uri.getHost().equals(TwitterClient.HOST)){
                   TwitterLoginFragment twitterLoginFragment = TwitterLoginFragment.newInstance();
                   showFragment(twitterLoginFragment, TwitterLoginFragment.TAG, false);
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
            viewId = R.id.fragmentFrameFullScreen;
            fragmentFrameFullScreen.setVisibility(View.VISIBLE);
            fragmentFrame.setVisibility(View.GONE);
        }else{
            viewId = R.id.fragmentFrame;
            fragmentFrame.setVisibility(View.VISIBLE);
            fragmentFrameFullScreen.setVisibility(View.GONE);
        }

        Log.v(TAG, "before ft.replace()");
        ft.replace(viewId, fragment, fragmentTag);
//        ft.addToBackStack(fragmentTag);
        Log.v(TAG, "before ft.commit()");
        ft.commit();
        Log.v(TAG, "after ft.commit()");
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
    public void onTwitterLoginRequested(){
        TwitterLoginFragment twitterLoginFragment = TwitterLoginFragment.newInstance();
        showFragment(twitterLoginFragment, TwitterLoginFragment.TAG, false);
//        Log.v(TAG, "Called showFragment for twitterLoginFragmet");
    }
}
