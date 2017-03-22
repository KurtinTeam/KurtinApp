package com.kurtin.kurtin.activities;

import android.content.res.Configuration;
import android.net.Uri;
import android.support.design.widget.BottomNavigationView;
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
import com.kurtin.kurtin.fragments.SchoolDetailFragment;
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

    //Fragment screens
    private FrameLayout flRegularScreen;
    private FrameLayout flFullScreen;
    //Nav Drawer and toolbar
    private DrawerLayout dlDrawerLayout;
    private Toolbar tbToolbar;
    private NavigationView nvNavigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    //Bottom nav for school detail
    BottomNavigationView bnvSchoolDetailNav;
    private String mToolbarTitle;

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

        //set up the UI
        bindUiElements();

        //Setup a back stack listener
        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        refreshUi();
                    }
                });

        //TODO: Look to delete this code
//        mLoginInProgress = false;

        showFirstFragment();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        Log.v(TAG, "Inside mainActivity.onOptionsItemSelected(MenuItem) - MenuItem:" +
                menuItem.toString() +
                "  ID: " + menuItem.getItemId());
        
        if (actionBarDrawerToggle.onOptionsItemSelected(menuItem)) {
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
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


    private void bindUiElements(){
        flRegularScreen = (FrameLayout) findViewById(R.id.flRegularScreen);
        flFullScreen = (FrameLayout) findViewById(R.id.flFullScreen);

        // Set a Toolbar to replace the ActionBar.
        tbToolbar = (Toolbar) findViewById(R.id.tbToolbar);
        setSupportActionBar(tbToolbar);

        // Setup Drawer
        dlDrawerLayout = (DrawerLayout) findViewById(R.id.dlDrawerLayout);
        actionBarDrawerToggle = setupDrawerToggle();
        dlDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        nvNavigationView = (NavigationView) findViewById(R.id.nvNavigationView);
        // Inflate the header view at runtime
        View headerLayout = nvNavigationView.inflateHeaderView(R.layout.header_nav_view);
        // We can now look up items within the header if needed
//        SimpleDraweeView sdvHeaderPhoto = (SimpleDraweeView) headerLayout.findViewById(R.id.sdvProfileImage);
        setupDrawerContent(nvNavigationView);

        bnvSchoolDetailNav = (BottomNavigationView) findViewById(R.id.bnvSchoolDetailNav);
    }


    private void showFirstFragment(){
        //Debugging
        // Fetch the uri that was passed in (which exists if this is being returned from authorization flow)
        Uri uri = getIntent().getData();
        //Check if we are getting a callback from an Oauth service
        if (uri == null){
            //No callback. show home screen.
            tbToolbar.setTitle(CategoriesFragment.TITLE);
            CategoriesFragment categoriesFragment = new CategoriesFragment();
            showFragment(categoriesFragment, CategoriesFragment.TAG, CategoriesFragment.TITLE, RegularScreen);
        }else{
            //Check if callback is from twitter
            if (uri.getScheme().equals(TwitterClient.SCHEME)){
               if(uri.getHost().equals(TwitterClient.HOST)){
                   Boolean loginIsInProgress = true;
                   TwitterLoginFragment twitterLoginFragment = TwitterLoginFragment.newInstance(loginIsInProgress);
                   showFragment(twitterLoginFragment, TwitterLoginFragment.TAG, TwitterLoginFragment.TITLE, RegularScreen);
               }
            }
            //Check if callback is from Instagram
            else if (uri.getScheme().equals(InstagramClient.SCHEME)){
                if (uri.getHost().equals(InstagramClient.HOST)) {
                    Boolean loginIsInProgress = true;
                    InstagramLoginFragment instagramLoginFragment = InstagramLoginFragment.newInstance(loginIsInProgress);
                    showFragment(instagramLoginFragment, InstagramLoginFragment.TAG, InstagramLoginFragment.TITLE, RegularScreen);
                }
            }
        }
    }

    private void showFragment(Fragment fragment, String fragmentTag, String fragmentTitle, ScreenType screenType){
        mToolbarTitle = fragmentTitle;

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        int viewId;

        // Determine which fragment container to use.
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
        ft.addToBackStack(fragmentTitle);
        ft.commit();
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        Log.v(TAG, "OnNavigationItemSelectedListener. menuItem = " + menuItem.toString());
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

    //Create the ActionBarDrawerToggle
    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        ActionBarDrawerToggle actionBarDrawerToggle =
                new ActionBarDrawerToggle(this, dlDrawerLayout, tbToolbar, R.string.drawer_open,  R.string.drawer_close){
            //Implement DrawerLayout.DrawerListener if needed
        };

        // Set a fallback click listener for the 'Home' icon when drawerIndicatorEnabled is set to false
        // via setDrawerIndicatorEnabled(boolean)
        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "Home icon pressed (Back Navigation)");
                if (upNavIsEnabled()){
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    mToolbarTitle = fragmentManager.getBackStackEntryAt(getIndexLastFragment()).getName();
                    getSupportFragmentManager().popBackStack();
                }
            }
        });

        actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_back_chevron);

        return actionBarDrawerToggle;
    }

    //TODO: Finish implmenting the switch between hamburger and back nav
    private void refreshUi(){
        Log.v(TAG, "Inside refreshUi()");
        tbToolbar.setTitle(mToolbarTitle);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(upNavEnabled);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(isDrawerIndicatorEnabled());
//        actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_back_chevron);

    }

    // Determine if back navigation is needed
    private boolean upNavIsEnabled(){
        return getSupportFragmentManager().getBackStackEntryCount() > 1;
    }

    // Determine if drawer menu icon is needed
    private boolean isDrawerIndicatorEnabled(){
        return !upNavIsEnabled();
    }

    private int getIndexCurrentFragment(){
        return getSupportFragmentManager().getBackStackEntryCount() - 1;
    }

    private int getIndexLastFragment(){
        return getSupportFragmentManager().getBackStackEntryCount() - 2;
    }

    public void hideToolbar(){
        tbToolbar.setVisibility(View.GONE);
    }

    public void showToolbar(){
        tbToolbar.setVisibility(View.VISIBLE);
    }

    public void showSchoolDetailBottomNav(boolean animate){
        bnvSchoolDetailNav.setVisibility(View.VISIBLE);
    }

    public void hideSchoolDetailBottomNav(boolean animate){
        bnvSchoolDetailNav.setVisibility(View.GONE);
    }

    public void showSchoolDetailMediaView(){
        hideToolbar();
    bnvSchoolDetailNav.setVisibility(View.GONE);
}

    public void hideSchoolDetailMediaView(){
        showToolbar();
        bnvSchoolDetailNav.setVisibility(View.VISIBLE);
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
        showFragment(testFragment, TestFragment.TAG, TestFragment.TITLE,RegularScreen);
    }

    @Override
    public void onAuthManagerRequested(){
        AuthManagerFragment authManagerFragment = new AuthManagerFragment();
        showFragment(authManagerFragment, AuthManagerFragment.TAG, AuthManagerFragment.TITLE, RegularScreen);
    }

    @Override
    public void onKurtinLoginNavRequested(){
        KurtinLoginFragment kurtinLoginFragment = new KurtinLoginFragment();
        showFragment(kurtinLoginFragment, KurtinLoginFragment.TAG, KurtinLoginFragment.TITLE,FullScreen);
    }

    @Override
    public void onCategoriesNavRequested(){
        CategoriesFragment categoriesFragment = new CategoriesFragment();
        showFragment(categoriesFragment, CategoriesFragment.TAG, CategoriesFragment.TITLE, RegularScreen);
    }

    //KurtinNavListener
    //*****************
    @Override
    public void onTestFragmentRequested(){
        TestFragment testFragment = new TestFragment();
        showFragment(testFragment, TestFragment.TAG, TestFragment.TITLE, RegularScreen);
    }

    @Override
    public void onAuthManagerFragmentRequested(){
        AuthManagerFragment authManagerFragment = new AuthManagerFragment();
        showFragment(authManagerFragment, AuthManagerFragment.TAG, AuthManagerFragment.TITLE, RegularScreen);
    }

    @Override
    public void onKurtinLoginFragmentRequested() {
        KurtinLoginFragment kurtinLoginFragment = new KurtinLoginFragment();
        showFragment(kurtinLoginFragment, KurtinLoginFragment.TAG, KurtinLoginFragment.TITLE, FullScreen);
    }

    @Override
    public void onCategoriesFragmentRequested(){
        CategoriesFragment categoriesFragment = new CategoriesFragment();
        showFragment(categoriesFragment, CategoriesFragment.TAG, CategoriesFragment.TITLE,RegularScreen);
    }

    @Override
    public void onSchoolListFragmentRequested(String categoryTypeObjId){
        SchoolListFragment schoolListFragment = SchoolListFragment.newInstance(categoryTypeObjId);
        showFragment(schoolListFragment, SchoolListFragment.TAG, SchoolListFragment.TITLE, RegularScreen);
    }

    @Override
    public void onSchoolDetailFragmentRequested(String schoolObjId, String categoryObjId, String categoryName){
        SchoolDetailFragment schoolDetailFragment = SchoolDetailFragment.newInstance(schoolObjId, categoryObjId, categoryName);
        showFragment(schoolDetailFragment, SchoolDetailFragment.TAG, SchoolDetailFragment.TITLE, RegularScreen);
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
                showFragment(facebookLoginFragment, FacebookLoginFragment.TAG, FacebookLoginFragment.TITLE, RegularScreen);
                break;
            case TWITTER:
                TwitterLoginFragment twitterLoginFragment = TwitterLoginFragment.newInstance(loginIsInProgress);
                showFragment(twitterLoginFragment, TwitterLoginFragment.TAG, TwitterLoginFragment.TITLE, RegularScreen);
                break;
            case INSTAGRAM:
                InstagramLoginFragment instagramLoginFragment = InstagramLoginFragment.newInstance(loginIsInProgress);
                showFragment(instagramLoginFragment, InstagramLoginFragment.TAG, InstagramLoginFragment.TITLE, RegularScreen);
                break;
        }
    }

    @Override
    public void onAuthenticationRequested(AuthPlatform.PlatformType platform){
        boolean loginIsInProgress = false;
        switch (platform){
            case FACEBOOK:
                FacebookLoginFragment facebookLoginFragment = FacebookLoginFragment.newInstance();
                showFragment(facebookLoginFragment, FacebookLoginFragment.TAG, FacebookLoginFragment.TITLE,RegularScreen);
                break;
            case TWITTER:
                TwitterLoginFragment twitterLoginFragment = TwitterLoginFragment.newInstance(loginIsInProgress);
                showFragment(twitterLoginFragment, TwitterLoginFragment.TAG, TwitterLoginFragment.TITLE,RegularScreen);
                break;
            case INSTAGRAM:
                InstagramLoginFragment instagramLoginFragment = InstagramLoginFragment.newInstance(loginIsInProgress);
                showFragment(instagramLoginFragment, InstagramLoginFragment.TAG, InstagramLoginFragment.TITLE, RegularScreen);
                break;
        }
    }

    @Override
    public void onAuthenticationCompleted(AuthPlatform.PlatformType platformType){
        KurtinUser.setAuthenticationStatus(this, platformType, true);
        LoginFragment loginFragment = LoginFragment.newInstance();
        showFragment(loginFragment, "loginFragment", "Login Fragment", RegularScreen);
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
