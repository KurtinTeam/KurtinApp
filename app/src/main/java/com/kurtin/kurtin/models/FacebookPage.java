package com.kurtin.kurtin.models;

import com.kurtin.kurtin.helpers.JsonHelper;

import org.json.JSONObject;

/**
 * Created by cvar on 1/27/17.
 */

public class FacebookPage {

    private String mId;
    private String mName;
    private String mAbout;
    private JSONObject mCover;
    private JSONObject mFeaturedVideo;
    private String mFoundedDate;
    private String mGeneralInfo;
    private JSONObject mLocation;
    private String mMission;
    private String mOverallStarRating;
    private String mPlaceType;
    private String mWebsite;

    private class FbPageFieldKeys{
        private static final String ID = "id";
        private static final String NAME = "name";
        private static final String ABOUT = "about";
        private static final String BIO = "bio";
        private static final String AFFILIATION = "affiliation";
        private static final String COVER = "cover";
        private static final String DESCRIPTION = "description";
        private static final String FEAT_VIDEO = "featured_video";
        private static final String FOUNDED = "founded";
        private static final String GEN_INFO = "general_info";
        private static final String LOCATION = "location";
        private static final String MISSION = "mission";
        private static final String OVERALL_STAR_RATING = "overall_star_rating";
        private static final String PLACE_TYPE = "place_type";
        private static final String WEBSITE = "website";
    }

    public FacebookPage(JSONObject fbPageJsonObj){
        JsonHelper jsonHelper = new JsonHelper(fbPageJsonObj);

        mId = jsonHelper.getString(FbPageFieldKeys.ID);
        mName = jsonHelper.getString(FbPageFieldKeys.NAME);
        mAbout = jsonHelper.getString(FbPageFieldKeys.ABOUT);
        mCover = jsonHelper.getJsonObject(FbPageFieldKeys.COVER);
        mFeaturedVideo = jsonHelper.getJsonObject(FbPageFieldKeys.FEAT_VIDEO);
        mFoundedDate = jsonHelper.getString(FbPageFieldKeys.FOUNDED);
        mGeneralInfo = jsonHelper.getString(FbPageFieldKeys.GEN_INFO);
        mLocation = jsonHelper.getJsonObject(FbPageFieldKeys.LOCATION);
        mMission = jsonHelper.getString(FbPageFieldKeys.MISSION);
        mOverallStarRating = jsonHelper.getString(FbPageFieldKeys.OVERALL_STAR_RATING);
        mPlaceType = jsonHelper.getString(FbPageFieldKeys.PLACE_TYPE);
        mWebsite = jsonHelper.getString(FbPageFieldKeys.WEBSITE);
    }

}
