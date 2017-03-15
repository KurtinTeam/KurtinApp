package com.kurtin.kurtin.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cvar on 3/3/17.
 */

public class Media {

    MediaType mMediaType;

    public enum MediaType{
        PHOTO(0), INSTAGRAM(1), TWITTER(2), FACEBOOK(3), SIMILAR_SCHOOLS(4);

        private int mValue;
        private static Map<Integer, MediaType> mValueMap = new HashMap<Integer, MediaType>();

        private MediaType(int value){
            mValue = value;
        }

        static{
            for(MediaType mediaType: MediaType.values()){
                mValueMap.put(mediaType.getValue(), mediaType);
            }
        }

        public static MediaType toMediaType(int value){
            return mValueMap.get(value);
        }

        public int getValue(){
            return mValue;
        }
    }

    public static List<Media> getMediaListFromSchool(School school, List<School> schools){
        List<Media> mediaList = new ArrayList<>();
        mediaList.add(new SimilarSchools(schools));
        mediaList.add(new PhotoMedia(school.getCampusImage().getUrl()));
//        mediaList.addAll(FacebookMedia.facebookMediaListFromJsonArray(school.getFbPosts()));

        List<FacebookMedia> facebookMediaList = FacebookMedia.facebookMediaListFromJsonArray(school.getFbPosts());
        List<TwitterMedia> twitterMediaList = TwitterMedia.twitterMediaListFromJsonArray(school.getTweets());
        int fbCount = facebookMediaList.size();
        int twCount = twitterMediaList.size();
        int shuffleCount = Math.min(fbCount, twCount);

        //Alternate media
        for (int index=0; index<shuffleCount; index++){
            mediaList.add(facebookMediaList.get(index));
            mediaList.add(twitterMediaList.get(index));
        }

        //Add remaining fb posts if any
        if (shuffleCount < fbCount){
            //More fb posts to add
            for (int index=shuffleCount; index<facebookMediaList.size(); index++){
                mediaList.add(facebookMediaList.get(index));
            }
        }
        //Add remaining tweets if any
        if (shuffleCount < twCount){
            //More tweets to add
            for (int index=shuffleCount; index<twitterMediaList.size(); index++){
                mediaList.add(twitterMediaList.get(index));
            }
        }

        return mediaList;
    }

    public static List<Media> changeSchool(List<Media> mediaList, School school){
        mediaList.set(1, new PhotoMedia(school.getCampusImage().getUrl()));
        return mediaList;
    }

    public static int getSchoolIndexFromList(List<Media> mediaList){
        return 0;
    }

    public Media(MediaType mediaType){
        mMediaType = mediaType;
    }

    public MediaType getMediaType() {
        return mMediaType;
    }


}
