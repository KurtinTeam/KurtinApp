package com.kurtin.kurtin.models;

import com.kurtin.kurtin.helpers.JsonHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.value;
import static android.os.Build.VERSION_CODES.M;

/**
 * Created by cvar on 3/14/17.
 */

public class FacebookMedia extends Media {
    private String mFullPictureUrl;
    private String mThumbnailUrl;
    private String mVideoSource;
    private FacebookMediaType mFacebookMediaType;
    private String mMessage;

    private enum FacebookMediaType{
        PHOTO("photo"), VIDEO("video");

        String mValue;
        static Map<String,FacebookMediaType> mValueMap = new HashMap<>();

        FacebookMediaType(String value){
            mValue = value;
        }

        String getValue(){
            return mValue;
        }

        static FacebookMediaType valueToType(String value){
            return mValueMap.get(value);
        }

        static{
            for(FacebookMediaType facebookMediaType: FacebookMediaType.values()){
                mValueMap.put(facebookMediaType.getValue(), facebookMediaType);
            }
        }
    }

    public FacebookMedia(JSONObject fbPostObject){
        super(MediaType.FACEBOOK);
        mFacebookMediaType = FacebookMediaType.valueToType(JsonHelper.getString(fbPostObject, Keys.TYPE_KEY));
        mFullPictureUrl = JsonHelper.getString(fbPostObject, Keys.FULL_PICTURE_KEY);
        mThumbnailUrl = JsonHelper.getString(fbPostObject, Keys.THUMBNAIL_KEY);
        mVideoSource = JsonHelper.getString(fbPostObject, Keys.VIDEO_SOURCE_KEY);
        mMessage = JsonHelper.getString(fbPostObject, Keys.MESSAGE_KEY);
    }

    public static List<FacebookMedia> facebookMediaListFromJsonArray(JSONArray fbPosts){
        List<FacebookMedia> facebookMediaList = new ArrayList<>();
        for(int index=0; index<fbPosts.length(); index++){
            JSONObject fbPost = JsonHelper.getObjectFromArray(fbPosts, index);
            if (fbPost != null){
                if (JsonHelper.getString(fbPost, Keys.FULL_PICTURE_KEY) != null) {
                    facebookMediaList.add(new FacebookMedia(fbPost));
                }
            }
        }
        return facebookMediaList;
    }

    public FacebookMediaType getFacebookMediaType() {
        return mFacebookMediaType;
    }

    public String getFullPictureUrl() {
        return mFullPictureUrl;
    }

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getVideoSource() {
        return mVideoSource;
    }

    private static class Keys{
        private static final String TYPE_KEY = "type";
        private static final String FULL_PICTURE_KEY = "full_picture";
        private static final String THUMBNAIL_KEY = "picture";
        private static final String MESSAGE_KEY = "message";
        private static final String VIDEO_SOURCE_KEY = "source";
    }
}
