package com.kurtin.kurtin.models;

/**
 * Created by cvar on 3/3/17.
 */

public class PhotoMedia extends Media {
    String mSourceUrl;

    public PhotoMedia(String sourceUrl){
        super(MediaType.PHOTO);
        mSourceUrl = sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.mSourceUrl = sourceUrl;
    }

    public String getSourceUrl() {
        return mSourceUrl;
    }
}
