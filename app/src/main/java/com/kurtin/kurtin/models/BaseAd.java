package com.kurtin.kurtin.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by cvar on 2/23/17.
 */

@ParseClassName("BaseAd")
public class BaseAd extends ParseObject {

    public static String TAG = "BaseAd";

    // name, categoryItems, caption, title
    private static String NAME_KEY = "name";
    private static String SOURCE_KEY = "source";
    private static String CAPTION_KEY = "caption";
    private static String TITLE_KEY = "title";

    public static ParseQuery<BaseAd> getQuery(){
        return ParseQuery.getQuery(BaseAd.class);
    }

    public String getName(){
        return getString(NAME_KEY);
    }

    public String getSource() {
        return getString(SOURCE_KEY);
    }

    public String getCaption() {
        return getString(CAPTION_KEY);
    }

    public String getTitle() {
        return getString(TITLE_KEY);
    }

    public void setCaption(String caption) {
       put(CAPTION_KEY, caption);
    }

    public void setTitle(String title) {
        put(TITLE_KEY, title);
    }

}
