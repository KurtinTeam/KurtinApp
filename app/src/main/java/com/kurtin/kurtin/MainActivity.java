package com.kurtin.kurtin;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.kurtin.kurtin.fragments.LoginFragment;

public class MainActivity extends AppCompatActivity implements
        LoginFragment.LoginFragmentListener {

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

        LoginFragment loginFragment = LoginFragment.newInstance();
        showFragment(loginFragment, "loginFragment", false);
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

        ft.replace(viewId, fragment, fragmentTag);
//        ft.addToBackStack(fragmentTag);
        ft.commit();
    }
}
