package com.kurtin.kurtin.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.codepath.oauth.OAuthLoginFragment;
import com.kurtin.kurtin.R;
import com.kurtin.kurtin.clients.TwitterClient;
import com.kurtin.kurtin.listeners.AuthenticationListener;

import org.scribe.model.Token;



public class TwitterLoginFragment extends OAuthLoginFragment<TwitterClient> {

    public static final String TAG = "TwitterLoginFragment";
    public static final AuthenticationListener.Platform PLATFORM = AuthenticationListener.Platform.TWITTER;

    private static final String LOGIN_IN_PROGRESS = "loginInProgress";

    AuthenticationListener mListener;

    public static TwitterLoginFragment newInstance() {

        Bundle args = new Bundle();
        args.putBoolean(LOGIN_IN_PROGRESS, false);

        TwitterLoginFragment fragment = new TwitterLoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static TwitterLoginFragment newInstance(Boolean loginInProgress) {

        Bundle args = new Bundle();
        args.putBoolean(LOGIN_IN_PROGRESS, loginInProgress);

        TwitterLoginFragment fragment = new TwitterLoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_twitter_login, container, false);
    }

    @Override
    public void onActivityCreated(Bundle saved) {
        super.onActivityCreated(saved);

        Bundle args = getArguments();
        Boolean loginIsInProgress = args.getBoolean(LOGIN_IN_PROGRESS);
        if (!loginIsInProgress) {
            authenticate();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AuthenticationListener) {
            mListener = (AuthenticationListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement LoginFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onLoginSuccess() {
        // fires when twitter is authenticated, launch an intent as an example
//        Intent i = new Intent(this, TimelineActivity.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(i);
        showTokenInfo();
        mListener.onAuthenticationCompleted(PLATFORM);
    }

    @Override
    public void onLoginFailure(Exception e) {
        // fires if twitter authentication fails
        e.printStackTrace();
    }

    private void authenticate(){
        if (!getClient().isAuthenticated()) {
            getClient().connect();
        } else {
            // ...client is already authenticated...
            showTokenInfo();
        }
    }

    private void showTokenInfo(){
        TwitterClient twitterClient = (TwitterClient) TwitterClient.getInstance(TwitterClient.class, getContext());
        Token token = twitterClient.checkAccessToken();
        String string = token.getToken();
        Toast.makeText(getContext(), "Token: " + string, Toast.LENGTH_SHORT).show();
        Log.v(TAG, "Token: " + string);
    }

    public static String getTokenInfo(Context context){
        TwitterClient twitterClient = (TwitterClient) TwitterClient.getInstance(TwitterClient.class, context);
        Token token = twitterClient.checkAccessToken();
        String string;
        if(token != null){
            string = token.getToken();
        }else{
            string = "Token is null";
        }
        Log.v(TAG, "Token: " + string);
        return string;
    }

    private class ServiceButtonOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (!getClient().isAuthenticated()) {
                getClient().connect();
            } else {
                // ...client is already authenticated...
            }
        }
    }
}