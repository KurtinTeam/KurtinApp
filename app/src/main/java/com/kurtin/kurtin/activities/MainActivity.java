package com.kurtin.kurtin.activities;

import android.content.res.Configuration;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;


import com.facebook.drawee.view.SimpleDraweeView;
import com.kurtin.kurtin.R;
import com.kurtin.kurtin.clients.InstagramClient;
import com.kurtin.kurtin.clients.TwitterClient;
import com.kurtin.kurtin.fragments.AuthManagerFragment;
import com.kurtin.kurtin.fragments.CategoriesFragment;
import com.kurtin.kurtin.fragments.FacebookLoginFragment;
import com.kurtin.kurtin.fragments.InstagramLoginFragment;
import com.kurtin.kurtin.fragments.KurtinLoginFragment;
import com.kurtin.kurtin.fragments.LoginFragment;
import com.kurtin.kurtin.fragments.SchoolListFragment;
import com.kurtin.kurtin.fragments.TestFragment;
import com.kurtin.kurtin.fragments.TwitterLoginFragment;
import com.kurtin.kurtin.listeners.AuthenticationListener;
import com.kurtin.kurtin.listeners.KurtinNavListener;
import com.kurtin.kurtin.models.AuthPlatform;
import com.kurtin.kurtin.models.KurtinUser;
import com.kurtin.kurtin.models.School;
import com.kurtin.kurtin.persistence.LoginPreferences;
import com.parse.ParseRelation;

import static com.kurtin.kurtin.activities.MainActivity.ScreenType.FullScreen;
import static com.kurtin.kurtin.activities.MainActivity.ScreenType.RegularScreen;


public class MainActivity extends AppCompatActivity implements
        LoginFragment.LoginFragmentListener,
        AuthenticationListener,
        KurtinNavListener{

    private static final String TAG = "MainActivity";

    private static final Boolean FULL_SCREEN = true;
    private static final Boolean REG_SCREEN = false;

    private FrameLayout flRegularScreen;
    private FrameLayout flFullScreen;

    //Nav Drawer and toolbar
    private DrawerLayout dlDrawerLayout;
    private Toolbar tbToolbar;
    private NavigationView nvNavigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private Boolean mLoginInProgress;

    public enum ScreenType{
        FullScreen, RegularScreen;

        public boolean isRegularScreen(){
            return this.equals(RegularScreen);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize a shared list of Authentication Platforms
        KurtinUser.getUserPlatforms(this);

        flRegularScreen = (FrameLayout) findViewById(R.id.flRegularScreen);
        flFullScreen = (FrameLayout) findViewById(R.id.flFullScreen);

        // Set a Toolbar to replace the ActionBar.
        tbToolbar = (Toolbar) findViewById(R.id.tbToolbar);
        setSupportActionBar(tbToolbar);

        // Setup Drawer
        dlDrawerLayout = (DrawerLayout) findViewById(R.id.dlDrawerLayout);
        actionBarDrawerToggle = setupDrawerToggle();
        nvNavigationView = (NavigationView) findViewById(R.id.nvNavigationView);
        // Inflate the header view at runtime
        View headerLayout = nvNavigationView.inflateHeaderView(R.layout.header_nav_view);
        // We can now look up items within the header if needed
//        SimpleDraweeView sdvHeaderPhoto = (SimpleDraweeView) headerLayout.findViewById(R.id.sdvProfileImage);
        setupDrawerContent(nvNavigationView);


        mLoginInProgress = false;

        showFirstFragment();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }


    private void showFirstFragment(){
        //Debugging
        // Fetch the uri that was passed in (which exists if this is being returned from authorization flow)
        Uri uri = getIntent().getData();
        //Check if we are getting a callback from an Oauth service
        if (uri == null){
            //No callback. show home screen.
            LoginFragment loginFragment = LoginFragment.newInstance();
            showFragment(loginFragment, "loginFragment", RegularScreen);
        }else{
            //Check if callback is from twitter
            if (uri.getScheme().equals(TwitterClient.SCHEME)){
               if(uri.getHost().equals(TwitterClient.HOST)){
                   Boolean loginIsInProgress = true;
                   TwitterLoginFragment twitterLoginFragment = TwitterLoginFragment.newInstance(loginIsInProgress);
                   showFragment(twitterLoginFragment, TwitterLoginFragment.TAG, RegularScreen);
               }
            }
            //Check if callback is from Instagram
            else if (uri.getScheme().equals(InstagramClient.SCHEME)){
                if (uri.getHost().equals(InstagramClient.HOST)) {
                    Boolean loginIsInProgress = true;
                    InstagramLoginFragment instagramLoginFragment = InstagramLoginFragment.newInstance(loginIsInProgress);
                    showFragment(instagramLoginFragment, InstagramLoginFragment.TAG, RegularScreen);
                }
            }
        }
    }

    private void showFragment(Fragment fragment, String fragmentTag, ScreenType screenType){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        int viewId;

        if(screenType.isRegularScreen()){
            viewId = R.id.flRegularScreen;
            flRegularScreen.setVisibility(View.VISIBLE);
            flFullScreen.setVisibility(View.GONE);
        }else{
            viewId = R.id.flFullScreen;
            flFullScreen.setVisibility(View.VISIBLE);
            flRegularScreen.setVisibility(View.GONE);
        }

        ft.replace(viewId, fragment, fragmentTag);
//        ft.addToBackStack(fragmentTag);
        ft.commit();
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    private void selectDrawerItem(MenuItem menuItem) {
        //Handle menu selections here
        switch(menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                break;
            case R.id.nav_second_fragment:
                break;
            case R.id.nav_third_fragment:
                break;
            default:
        }

//        // Highlight the selected item has been done by NavigationView
//        menuItem.setChecked(true);
//        // Set action bar title
//        setTitle(menuItem.getTitle());

        // Close the navigation drawer
        dlDrawerLayout.closeDrawers();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, dlDrawerLayout, tbToolbar, R.string.drawer_open,  R.string.drawer_close);
    }


    //*
    //**
    //***
    //Interface implementations
    //***
    //**
    //*

    //TODO: Delete LoginFragment Listener
    //LoginFragment.Listener
    @Override
    public void onTestRequested(){
        TestFragment testFragment = new TestFragment();
        showFragment(testFragment, TestFragment.TAG, RegularScreen);
    }

    @Override
    public void onAuthManagerRequested(){
        AuthManagerFragment authManagerFragment = new AuthManagerFragment();
        showFragment(authManagerFragment, AuthManagerFragment.TAG, RegularScreen);
    }

    @Override
    public void onKurtinLoginNavRequested(){
        KurtinLoginFragment kurtinLoginFragment = new KurtinLoginFragment();
        showFragment(kurtinLoginFragment, KurtinLoginFragment.TAG, FullScreen);
    }

    @Override
    public void onCategoriesNavRequested(){
        CategoriesFragment categoriesFragment = new CategoriesFragment();
        showFragment(categoriesFragment, CategoriesFragment.TAG, RegularScreen);
    }

    //KurtinNavListener
    //*****************
    @Override
    public void onTestFragmentRequested(){
        TestFragment testFragment = new TestFragment();
        showFragment(testFragment, TestFragment.TAG, RegularScreen);
    }

    @Override
    public void onAuthManagerFragmentRequested(){
        AuthManagerFragment authManagerFragment = new AuthManagerFragment();
        showFragment(authManagerFragment, AuthManagerFragment.TAG, RegularScreen);
    }

    @Override
    public void onKurtinLoginFragmentRequested() {
        KurtinLoginFragment kurtinLoginFragment = new KurtinLoginFragment();
        showFragment(kurtinLoginFragment, KurtinLoginFragment.TAG, FullScreen);
    }

    @Override
    public void onCategoriesFragmentRequested(){
        CategoriesFragment categoriesFragment = new CategoriesFragment();
        showFragment(categoriesFragment, CategoriesFragment.TAG, RegularScreen);
    }

    @Override
    public void onSchoolListFragmentRequested(String categoryTypeObjId){
        SchoolListFragment schoolListFragment = SchoolListFragment.newInstance(categoryTypeObjId);
        showFragment(schoolListFragment, SchoolListFragment.TAG, RegularScreen);
    }

    //AuthenticationListener
    //**********************
    @Override
    public void onKurtinLoginRequested(AuthPlatform.PlatformType platformType){
        //Save primary login platform data
        LoginPreferences.setPrimaryKurtinLoginPlatformType(this, platformType);

        boolean loginIsInProgress = false;
        switch (platformType){
            case FACEBOOK:
                FacebookLoginFragment facebookLoginFragment = FacebookLoginFragment.newInstance();
                showFragment(facebookLoginFragment, FacebookLoginFragment.TAG, RegularScreen);
                break;
            case TWITTER:
                TwitterLoginFragment twitterLoginFragment = TwitterLoginFragment.newInstance(loginIsInProgress);
                showFragment(twitterLoginFragment, TwitterLoginFragment.TAG, RegularScreen);
                break;
            case INSTAGRAM:
                InstagramLoginFragment instagramLoginFragment = InstagramLoginFragment.newInstance(loginIsInProgress);
                showFragment(instagramLoginFragment, InstagramLoginFragment.TAG, RegularScreen);
                break;
        }
    }

    @Override
    public void onAuthenticationRequested(AuthPlatform.PlatformType platform){
        boolean loginIsInProgress = false;
        switch (platform){
            case FACEBOOK:
                FacebookLoginFragment facebookLoginFragment = FacebookLoginFragment.newInstance();
                showFragment(facebookLoginFragment, FacebookLoginFragment.TAG, RegularScreen);
                break;
            case TWITTER:
                TwitterLoginFragment twitterLoginFragment = TwitterLoginFragment.newInstance(loginIsInProgress);
                showFragment(twitterLoginFragment, TwitterLoginFragment.TAG, RegularScreen);
                break;
            case INSTAGRAM:
                InstagramLoginFragment instagramLoginFragment = InstagramLoginFragment.newInstance(loginIsInProgress);
                showFragment(instagramLoginFragment, InstagramLoginFragment.TAG, RegularScreen);
                break;
        }
    }

    @Override
    public void onAuthenticationCompleted(AuthPlatform.PlatformType platformType){
        KurtinUser.setAuthenticationStatus(this, platformType, true);
        LoginFragment loginFragment = LoginFragment.newInstance();
        showFragment(loginFragment, "loginFragment", RegularScreen);
    }

    @Override
    public void logOut(AuthPlatform.PlatformType platformType){
        if(KurtinUser.currentUserIsLoggedIn(this)) {
            KurtinUser.getCurrentUser(this).logOut(this, platformType);
        }else{
            Log.e(TAG, "ERROR: logOut(AuthPlatform.PlatformType platformType) requested but no user is logged in");
        }
    }

    @Override
    public void onKurtinLogOutRequested(){
        if(KurtinUser.currentUserIsLoggedIn(this)) {
            KurtinUser.getCurrentUser(this).logOutOfKurtin(this);
            onKurtinLoginFragmentRequested();
        }else{
            Log.e(TAG, "ERROR: onKurtinLogOutRequested() requested but no user is logged in");
        }
    }

    @Override
    public Boolean loginInProgress(){
        return mLoginInProgress;
    }
}
