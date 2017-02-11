package com.kurtin.kurtin.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kurtin.kurtin.R;
import com.kurtin.kurtin.listeners.AuthenticationListener;
import com.kurtin.kurtin.models.AuthPlatform;
import com.kurtin.kurtin.models.KurtinUser;

import static com.kurtin.kurtin.R.id.btnFacebookLogIn;

/**
 * A simple {@link Fragment} subclass.
 */
public class KurtinLoginFragment extends Fragment {

    public static final String TAG = "KurtinLoginFragment";

    private Button btnFacebook;
    private Button btnTwitter;
    private Button btnInstagram;

    private AuthenticationListener mListener;


    public KurtinLoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_kurtin_login, container, false);
        bindUiElements(view);
        setOnClickListeners();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginFragment.LoginFragmentListener) {
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

    private void bindUiElements(View view){
        btnFacebook = (Button) view.findViewById(R.id.btnFacebook);
        btnTwitter = (Button) view.findViewById(R.id.btnTwitter);
        btnInstagram = (Button) view.findViewById(R.id.btnInstagram);
    }

    private void setOnClickListeners(){
        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onKurtinLoginRequested(AuthPlatform.PlatformType.FACEBOOK);
                }else{
                    Log.e(TAG, "Must implement LoginFragmentListener");
                }
            }
        });

        btnTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onKurtinLoginRequested(AuthPlatform.PlatformType.TWITTER);
                }else{
                    Log.e(TAG, "Must implement LoginFragmentListener");
                }
            }
        });

        btnInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onKurtinLoginRequested(AuthPlatform.PlatformType.INSTAGRAM);
                }else{
                    Log.e(TAG, "Must implement LoginFragmentListener");
                }
            }
        });
    }

}
