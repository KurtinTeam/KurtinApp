package com.kurtin.kurtin.models;

import com.kurtin.kurtin.helpers.JsonHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cvar on 3/14/17.
 */

public class TwitterMedia extends Media {
    private String mPictureUrl;
    private TwitterMediaType mTwitterMediaType;
    private String mFullText;
//    private String mVideoSource;

    private enum TwitterMediaType{
        PHOTO("photo"); //, VIDEO("video");

        String mValue;
        static Map<String,TwitterMediaType> mValueMap = new HashMap<>();

        TwitterMediaType(String value){
            mValue = value;
        }

        String getValue(){
            return mValue;
        }

        static TwitterMediaType valueToType(String value){
            return mValueMap.get(value);
        }

        static{
            for(TwitterMediaType twitterMediaType: TwitterMediaType.values()){
                mValueMap.put(twitterMediaType.getValue(), twitterMediaType);
            }
        }
    }

    public TwitterMedia(String fullText, JSONObject mediaObject){
        super(MediaType.TWITTER);
        mTwitterMediaType = TwitterMediaType.valueToType(JsonHelper.getString(mediaObject, Keys.TYPE_KEY));
        mPictureUrl = JsonHelper.getString(mediaObject, Keys.MEDIA_URL_HTTPS_KEY);
        mFullText = fullText;
    }

    public static List<TwitterMedia> twitterMediaListFromJsonArray(JSONArray tweetObjects){
        List<TwitterMedia> twitterMediaList = new ArrayList<>();
        for(int index=0; index<tweetObjects.length(); index++){
            JSONObject tweetObject = JsonHelper.getObjectFromArray(tweetObjects, index);
            if (tweetObject != null){
                JSONObject mediaObject = getMediaObject(tweetObject);
                String fullText = JsonHelper.getString(tweetObject, Keys.FULL_TEXT_KEY);
                if (mediaObject != null && fullText != null) {
                    twitterMediaList.add(new TwitterMedia(fullText, mediaObject));
                }
            }
        }
        return twitterMediaList;
    }

//    public static String getMediaUrl(JSONObject tweetObject){
//        String mediaUrl = null;
//        try{
//            mediaUrl = tweetObject.
//                    getJSONObject(Keys.ENTITIES_KEY).
//                    getJSONArray(Keys.MEDIA_KEY).
//                    getJSONObject(Keys.FIRST_MEDIA_ITEM_INDEX).
//                    getString(Keys.MEDIA_URL_HTTPS_KEY);
//        }catch (JSONException entitiesJsonException){
//            try{
//                mediaUrl = tweetObject.
//                        getJSONObject(Keys.ENTITIES_KEY).
//                        getJSONArray(Keys.MEDIA_KEY).
//                        getJSONObject(Keys.FIRST_MEDIA_ITEM_INDEX).
//                        getString(Keys.MEDIA_URL_HTTPS_KEY);
//            }catch (JSONException extendedEntitiesJsonException){
//
//            }
//        }
//        return mediaUrl;
//    }

    public static JSONObject getMediaObject(JSONObject tweetObject){
        JSONObject mediaObject = null;
        try{
            mediaObject = tweetObject.
                    getJSONObject(Keys.ENTITIES_KEY).
                    getJSONArray(Keys.MEDIA_KEY).
                    getJSONObject(Keys.FIRST_MEDIA_ITEM_INDEX);
        }catch (JSONException entitiesJsonException){
            try{
                mediaObject = tweetObject.
                        getJSONObject(Keys.EXTENDED_ENTITIES_KEY).
                        getJSONArray(Keys.MEDIA_KEY).
                        getJSONObject(Keys.FIRST_MEDIA_ITEM_INDEX);
            }catch (JSONException extendedEntitiesJsonException){

            }
        }
        return mediaObject;
    }

    public TwitterMediaType getTwitterMediaType() {
        return mTwitterMediaType;
    }

    public String getPictureUrl() {
        return mPictureUrl;
    }

    public String getFullText() {
        return mFullText;
    }

    private static class Keys{
        private static final String TYPE_KEY = "type";
        private static final String MEDIA_URL_HTTPS_KEY = "media_url_https";
        private static final String FULL_TEXT_KEY = "full_text";
        private static final String ENTITIES_KEY = "entities";
        private static final String EXTENDED_ENTITIES_KEY = "extended_entities";
        private static final String MEDIA_KEY = "media";
        private static final Integer FIRST_MEDIA_ITEM_INDEX = 0;
//        private static final String VIDEO_SOURCE_KEY = "source";
    }

}
