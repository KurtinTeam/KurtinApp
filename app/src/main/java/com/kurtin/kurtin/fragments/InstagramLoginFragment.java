package com.kurtin.kurtin.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.oauth.OAuthLoginFragment;
import com.kurtin.kurtin.R;
import com.kurtin.kurtin.clients.InstagramClient;
import com.kurtin.kurtin.clients.TwitterClient;
import com.kurtin.kurtin.listeners.AuthenticationListener;
import com.kurtin.kurtin.models.KurtinUser;

import org.scribe.model.Token;

import static com.kurtin.kurtin.models.KurtinUser.AuthenticationPlatform.INSTAGRAM;
import static java.lang.Boolean.getBoolean;


public class InstagramLoginFragment extends OAuthLoginFragment<InstagramClient> {

    public static final String TAG = "InstagramLoginFragment";
    public static final KurtinUser.AuthenticationPlatform PLATFORM = KurtinUser.AuthenticationPlatform.INSTAGRAM;

    private static final String LOGIN_IN_PROGRESS = "loginInProgress";

    AuthenticationListener mListener;

    public static InstagramLoginFragment newInstance() {

        Bundle args = new Bundle();
        args.putBoolean(LOGIN_IN_PROGRESS, false);

        InstagramLoginFragment fragment = new InstagramLoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static InstagramLoginFragment newInstance(Boolean loginInProgress) {

        Bundle args = new Bundle();
        args.putBoolean(LOGIN_IN_PROGRESS, loginInProgress);

        InstagramLoginFragment fragment = new InstagramLoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_instagram_login, container, false);
    }

    @Override
    public void onActivityCreated(Bundle saved) {
        super.onActivityCreated(saved);

        // Fetch the uri that was passed in (which exists if this is being returned from authorization flow)
//        Uri uri = getActivity().getIntent().getData();
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
        // fires if authentication succeeds
        showTokenInfo();
        mListener.onAuthenticationCompleted(PLATFORM);
    }

    @Override
    public void onLoginFailure(Exception e) {
        // fires if authentication fails
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
        InstagramClient instagramClient = (InstagramClient) InstagramClient.getInstance(InstagramClient.class, getContext());
        Token token = instagramClient.checkAccessToken();
        String string = token.getToken();
        Toast.makeText(getContext(), "Token: " + string, Toast.LENGTH_SHORT).show();
        Log.v(TAG, "Token: " + string);
    }

    public static String getTokenInfo(Context context){
        InstagramClient instagramClient = (InstagramClient) InstagramClient.getInstance(InstagramClient.class, context);
        Token token = instagramClient.checkAccessToken();
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

//
//    private OnFragmentInteractionListener mListener;
//
//    public InstagramLoginFragment() {
//        // Required empty public constructor
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_instagram_login, container, false);
//    }
//
//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
