package com.kurtin.kurtin.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.kurtin.kurtin.R;
import com.kurtin.kurtin.listeners.AuthenticationListener;
import com.kurtin.kurtin.models.KurtinUser;
import com.kurtin.kurtin.persistence.LoginPreferences;

import java.util.ArrayList;
import java.util.List;

public class FacebookLoginFragment extends Fragment {

    public static final String TAG = "FacebookLoginFragment";

    private static final KurtinUser.AuthenticationPlatform FACEBOOK = KurtinUser.AuthenticationPlatform.FACEBOOK;
    private final List<String> FACEBOOK_PERMISSIONS =
            new ArrayList<String>(){{
                add("email"); add("public_profile"); add("user_photos");}};

    private CallbackManager mCallbackManager;
    private FacebookLoginFragment mThisLoginFragment;
    private boolean mFacebookIsPrimaryLogin;

    private AuthenticationListener mListener;

    public FacebookLoginFragment() {
        // Required empty public constructor
    }

    public static FacebookLoginFragment newInstance() {

        Bundle args = new Bundle();

        FacebookLoginFragment fragment = new FacebookLoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

//    public static FacebookLoginFragment newInstance(boolean facebookIsPrimaryLogin) {
//
//        Bundle args = new Bundle();
//        args.putBoolean(KurtinUser.IS_PRIMARY_LOGIN_KEY, facebookIsPrimaryLogin);
//        FacebookLoginFragment fragment = new FacebookLoginFragment();
//        fragment.setArguments(args);
//        return fragment;
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initialize();
        logIn();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_facebook_login, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AuthenticationListener) {
            mListener = (AuthenticationListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void initialize(){
//        FacebookSdk.sdkInitialize(getContext().getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.v(TAG, "Login successful: " + loginResult.getAccessToken().toString());

                if(mFacebookIsPrimaryLogin){
                    KurtinUser.logInToKurtin(getContext(), FACEBOOK, new KurtinUser.KurtinLoginCallback() {
                        @Override
                        public void loginResult(String result) {
                            Log.v(TAG, "Result of fb primary login: " + result);
                        }
                    });
                }

                if (mListener != null){
                    mListener.onAuthenticationCompleted(FACEBOOK);
                }else{
                    Log.e(TAG, "Must implement AuthenticationListener");
                }
            }

            @Override
            public void onCancel() {
                Log.v(TAG, "Login cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.v(TAG, "Login error");
                error.printStackTrace();
            }
        });
        mThisLoginFragment = this;
        mFacebookIsPrimaryLogin =
                LoginPreferences.getPrimaryKurtinLoginPlatform(getContext())
                        .equals(KurtinUser.AuthenticationPlatform.FACEBOOK);
    }

    private void logIn(){
        LoginManager.getInstance().logInWithReadPermissions(mThisLoginFragment, FACEBOOK_PERMISSIONS);
    }
}
