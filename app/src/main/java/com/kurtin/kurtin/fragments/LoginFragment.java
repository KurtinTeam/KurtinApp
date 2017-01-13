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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.kurtin.kurtin.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.LoginFragmentListener} interface
 * to handle interaction events.
 */
public class LoginFragment extends Fragment {

    private final String TAG = "LoginFragment";
    private final List<String> FACEBOOK_PERMISSIONS =
            new ArrayList<String>(){{
                add("email"); add("public_profile"); add("user_photos");}};

    private LoginFragmentListener mListener;

    private View mParentView;
    private Button btnFacebookLogIn;
    private Button btnFacebookLogOut;
    private Button btnAccessToken;
    private LoginButton btnFacebook;

    private CallbackManager mCallbackManager;
    private LoginFragment mThisLoginFragment;


    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {

        Bundle args = new Bundle();

        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mParentView = inflater.inflate(R.layout.fragment_login, container, false);

        initializations();
        bindUiElements();
        setClickListeners();

        return mParentView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginFragmentListener) {
            mListener = (LoginFragmentListener) context;
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

    private void bindUiElements(){
        btnFacebook = (LoginButton) mParentView.findViewById(R.id.btnFacebook);
        btnFacebookLogIn = (Button) mParentView.findViewById(R.id.btnFacebookLogIn);
        btnFacebookLogOut = (Button) mParentView.findViewById(R.id.btnFacebookLogOut);
        btnAccessToken = (Button) mParentView.findViewById(R.id.btnAccessToken);

        setupDefaultFbButton();
    }

    private void initializations(){
        FacebookSdk.sdkInitialize(getContext().getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.v(TAG, "Login successful: " + loginResult.getAccessToken().toString());
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
    }

    private void setClickListeners(){
        btnFacebookLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(mThisLoginFragment, FACEBOOK_PERMISSIONS);
            }
        });

        btnFacebookLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
            }
        });

        btnAccessToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                if (accessToken == null){
                    Toast.makeText(getContext(), "Null access token", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(), accessToken.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupDefaultFbButton(){
        btnFacebook.setReadPermissions("email");
        btnFacebook.setReadPermissions("public_profile");
        btnFacebook.setReadPermissions("user_photos");
        // If using in a fragment
        btnFacebook.setFragment(this);
        // Other app specific specialization

        // Callback registration
        btnFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.v(TAG, "Login successful: " + loginResult.getAccessToken().toString());
            }

            @Override
            public void onCancel() {
                // App code
                Log.v(TAG, "Login cancelled");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.v(TAG, "Login error");
                exception.printStackTrace();
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface LoginFragmentListener {
        // TODO: Update argument type and name
        //void onFragmentInteraction(Uri uri);
    }
}
