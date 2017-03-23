package com.kurtin.kurtin.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cvar on 2/23/17.
 */

@ParseClassName("School")
public class School extends ParseObject {

    private static final String NAME_KEY = "name";
    private static final String FACEBOOK_URL_KEY = "facebookUrl";
    private static final String ROOM_AND_BOARD_KEY = "roomAndBoard";
    private static final String TUITION_IN_STATE_KEY = "tuitionInState";
    private static final String STATE_KEY = "state";
    private static final String FB_OBJ_KEY = "fbObject";
    private static final String CAMPUS_IMAGE_KEY = "campusImage";
    private static final String STUDENT_IMAGE_KEY = "studentImage";
    private static final String LOGO_IMAGE_KEY = "logoImage";
    private static final String ADMISSIONS_PHONE_KEY = "admissionsPhone";
    private static final String ADMISSIONS_URL_KEY = "admissionsUrl";
    private static final String TUITION_OUT_OF_STATE_KEY = "tuitionOutOfState";
    private static final String CITY_KEY = "city";
    private static final String FACEBOOK_AlT_URL_KEY = "facebookAltUrl";
    private static final String IG_SCREENNAME_KEY = "igScreenname";
    private static final String FB_ID_KEY = "fbId";
    private static final String PUBLIC_PRIVATE_KEY = "publicPrivate";
    private static final String TW_SCREEN_NAME_KEY = "twScreenName";
    private static final String TWITTER_URL_KEY = "twitterUrl";
    private static final String INSTAGRAM_URL_KEY = "instagramUrl";
    private static final String STUDENT_BODY_KEY = "studentBody";
    private static final String TWITTER_ALT_URL_KEY = "twitterAltUrl";
    private static final String INSTAGRAM_ALT_URL_KEY = "instagramAltUrl";
    private static final String FB_POSTS_KEY = "fbPosts";
    private static final String TWEETS_KEY = "tweets";
    private static final String STATS_KEY = "stats";
    public static final List<String> BASIC_DISPLAY_KEYS = Arrays.asList("name", "state", "city", "studentImage", "logoImage");

    public static final String OBJECT_ID = "objectId";



    //Getters
    public static ParseQuery<School> getQuery(){
        return ParseQuery.getQuery(School.class);
    }

    public String getAdmissionsPhone() {
        return getString(ADMISSIONS_PHONE_KEY);
    }

    public String getAdmissionsUrl() {
        return getString(ADMISSIONS_URL_KEY);
    }

    public ParseFile getCampusImage() {
        return getParseFile(CAMPUS_IMAGE_KEY);
    }

    public String getCity() {
        return getString(CITY_KEY);
    }

    public String getFacebookAltUrl() {
        return getString(FACEBOOK_AlT_URL_KEY);
    }

    public String getFacebookUrl() {
        return getString(FACEBOOK_URL_KEY);
    }

    public String getFbId() {
        return getString(FB_ID_KEY);
    }

    public String getFbObj() {
        return getString(FB_OBJ_KEY);
    }

    public String getIgScreenname() {
        return getString(IG_SCREENNAME_KEY);
    }

    public String getInstagramAltUrl() {
        return getString(INSTAGRAM_ALT_URL_KEY);
    }

    public String getInstagramUrl() {
        return getString(INSTAGRAM_URL_KEY);
    }

    public ParseFile getLogoImage() {
        return getParseFile(LOGO_IMAGE_KEY);
    }

    public String getName() {
        return getString(NAME_KEY);
    }

    public String getPublicPrivate() {
        return getString(PUBLIC_PRIVATE_KEY);
    }

    public int getRoomAndBoard() {
        return getInt(ROOM_AND_BOARD_KEY);
    }

    public String getState() {
        return getString(STATE_KEY);
    }

    public int getStudentBody() {
        return getInt(STUDENT_BODY_KEY);
    }

    public ParseFile getStudentImage() {
        return getParseFile(STUDENT_IMAGE_KEY);
    }

    public int getTuitionInState() {
        return getInt(TUITION_IN_STATE_KEY);
    }

    public int getTuitionOutOfState() {
        return getInt(TUITION_OUT_OF_STATE_KEY);
    }

    public String getTwitterAltUrl() {
        return getString(TWITTER_ALT_URL_KEY);
    }

    public String getTwitterUrl() {
        return getString(TWITTER_URL_KEY);
    }

    public String getTwScreenName() {
        return getString(TW_SCREEN_NAME_KEY);
    }

    public JSONArray getFbPosts(){
        return getJSONArray(FB_POSTS_KEY);
    }

    public JSONArray getTweets(){
        return getJSONArray(TWEETS_KEY);
    }

    public JSONObject getStats() {
        return getJSONObject(STATS_KEY);
    }

}
