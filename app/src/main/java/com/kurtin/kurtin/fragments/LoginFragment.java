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
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.kurtin.kurtin.R;
import com.kurtin.kurtin.clients.InstagramClient;
import com.kurtin.kurtin.clients.TwitterClient;
import com.kurtin.kurtin.models.KurtinUser;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.LoginFragmentListener} interface
 * to handle interaction events.
 */
public class LoginFragment extends Fragment {

    public final String TAG = "LoginFragment";

    private final List<String> FACEBOOK_PERMISSIONS =
            new ArrayList<String>(){{
                add("email"); add("public_profile"); add("user_photos");}};

    private LoginFragmentListener mListener;

    private View mParentView;
    private Button btnFacebookLogIn;
    private Button btnFacebookLogOut;
    private Button btnFacebookAccessToken;
    private Button btnFacebookMe;
    private Button btnTwitterLogIn;
    private Button btnTwitterLogOut;
    private Button btnTwitterAccessToken;
    private Button btnTwitterMe;
    private Button btnInstagramLogIn;
    private Button btnInstagramLogOut;
    private Button btnInstagramAccessToken;
    private Button btnInstagramMe;
    private Button btnKLogFb;
    private Button btnKLogTw;
    private Button btnKLogIg;
    private Button btnGoToTest;
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
        btnFacebookAccessToken = (Button) mParentView.findViewById(R.id.btnFacebookAccessToken);
        btnFacebookMe = (Button) mParentView.findViewById(R.id.btnFacebookMe);
        btnTwitterLogIn = (Button) mParentView.findViewById(R.id.btnTwitterLogIn);
        btnTwitterLogOut = (Button) mParentView.findViewById(R.id.btnTwitterLogOut);
        btnTwitterAccessToken = (Button) mParentView.findViewById(R.id.btnTwitterAccessToken);
        btnTwitterMe = (Button) mParentView.findViewById(R.id.btnTwitterMe);
        btnInstagramLogIn = (Button) mParentView.findViewById(R.id.btnInstagramLogIn);
        btnInstagramLogOut = (Button) mParentView.findViewById(R.id.btnInstagramLogOut);
        btnInstagramAccessToken = (Button) mParentView.findViewById(R.id.btnInstagramAccessToken);
        btnInstagramMe = (Button) mParentView.findViewById(R.id.btnInstagramMe);
        btnKLogFb = (Button) mParentView.findViewById(R.id.btnKLogFB);
        btnKLogTw = (Button) mParentView.findViewById(R.id.btnKLogTW);
        btnKLogIg = (Button) mParentView.findViewById(R.id.btnKLogIG);
        btnGoToTest = (Button) mParentView.findViewById(R.id.btnGoToTest);

        setupDefaultFbButton();
    }

    private void initializations(){
//        FacebookSdk.sdkInitialize(getContext().getApplicationContext());
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
//                LoginManager.getInstance().logInWithReadPermissions(mThisLoginFragment, FACEBOOK_PERMISSIONS);
                if(mListener != null){
                    mListener.onAuthenticationRequested(KurtinUser.AuthenticationPlatform.FACEBOOK);
                }else{
                    Log.e(TAG, "Must implement LoginFragmentListener");
                }
            }
        });

        btnFacebookLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
            }
        });

        btnFacebookAccessToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                if (accessToken == null){
                    Toast.makeText(getContext(), "Null access token", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(), "FB Token: " + accessToken.getToken(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnFacebookMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v(TAG, "Me object: " + object);
                        Log.v(TAG, "Me response: " + response.toString());
//                        Log.v(TAG, "Me response error: " + response.getError());
//                        Log.v(TAG, "Me response connection: " + response.getConnection());
//                        Log.v(TAG, "Me response raw response: " + response.getRawResponse());
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, first_name, last_name, link, email, location, third_party_id");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }
        });

        btnTwitterLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onAuthenticationRequested(KurtinUser.AuthenticationPlatform.TWITTER);
                }else{
                    Log.e(TAG, "Must implement LoginFragmentListener");
                }
            }
        });

        btnTwitterLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    TwitterClient.getInstance(TwitterClient.class, getContext()).clearAccessToken();
                }else{
                    Log.e(TAG, "Must implement LoginFragmentListener");
                }
            }
        });

        btnTwitterAccessToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = TwitterLoginFragment.getTokenInfo(getContext());
                Toast.makeText(getContext(), "Twitter Token: " + string, Toast.LENGTH_SHORT).show();
            }
        });

        btnTwitterMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwitterClient.getSharedInstance(getContext()).getCurrentUser(getContext(), new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(
                            int statusCode, cz.msebera.android.httpclient.Header[] headers,
                            org.json.JSONObject response){
                        Log.v(TAG, "Success response: " + response.toString());
                    }

                    @Override
                    public void onFailure(
                            int statusCode, cz.msebera.android.httpclient.Header[] headers,
                            java.lang.Throwable throwable, org.json.JSONObject errorResponse){
                        Log.v(TAG, "Failure errorResponse: " + errorResponse.toString());
                    }
                });
            }
        });

        btnInstagramLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onAuthenticationRequested(KurtinUser.AuthenticationPlatform.INSTAGRAM);
                }else{
                    Log.e(TAG, "Must implement LoginFragmentListener");
                }
            }
        });

        btnInstagramLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    InstagramClient.getInstance(InstagramClient.class, getContext()).clearAccessToken();
                }else{
                    Log.e(TAG, "Must implement LoginFragmentListener");
                }
            }
        });

        btnInstagramAccessToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = InstagramLoginFragment.getTokenInfo(getContext());
                Toast.makeText(getContext(), "Instagram Token: " + string, Toast.LENGTH_SHORT).show();
            }
        });

        btnInstagramMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InstagramClient.getSharedInstance(getContext()).getCurrentUser(getContext(), new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(
                            int statusCode, cz.msebera.android.httpclient.Header[] headers,
                            org.json.JSONObject response){
                        Log.v(TAG, "Success response: " + response.toString());
                    }

                    @Override
                    public void onFailure(
                            int statusCode, cz.msebera.android.httpclient.Header[] headers,
                            java.lang.Throwable throwable, org.json.JSONObject errorResponse){
                        Log.v(TAG, "Failure errorResponse: " + errorResponse.toString());
                    }
                });
            }
        });

        btnKLogFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onKurtinLoginRequested(KurtinUser.AuthenticationPlatform.FACEBOOK);
                }else{
                    Log.e(TAG, "Must implement LoginFragmentListener");
                }
            }
        });

        btnKLogTw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onKurtinLoginRequested(KurtinUser.AuthenticationPlatform.TWITTER);
                }else{
                    Log.e(TAG, "Must implement LoginFragmentListener");
                }
            }
        });

        btnKLogIg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onKurtinLoginRequested(KurtinUser.AuthenticationPlatform.INSTAGRAM);
                }else{
                    Log.e(TAG, "Must implement LoginFragmentListener");
                }
            }
        });

        btnGoToTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onTestRequested();
                }else{
                    Log.e(TAG, "Must implement LoginFragmentListener");
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
//        void onTwitterLoginRequested();
//        void onInstagramLoginRequested();
//        void onFacebookLoginRequested();
        void onKurtinLoginRequested(KurtinUser.AuthenticationPlatform platform);
        void onAuthenticationRequested(KurtinUser.AuthenticationPlatform platform);

        void onTestRequested();
    }
}
