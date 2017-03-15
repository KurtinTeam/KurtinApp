package com.kurtin.kurtin.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

/**
 * Created by cvar on 2/22/17.
 */

@ParseClassName("Category")
public class Category extends ParseObject {

    public static String TAG = "Category";

    // name, categoryItems, caption, title
    private static final String NAME_KEY = "name";
    private static final String CATEGORY_ITEMS_KEY = "categoryItems";
    private static final String CAPTION_KEY = "caption";
    private static final String TITLE_KEY = "title";
    private static final String PICTURE_KEY = "picture";
    public static final String DISPLAY_ORDER_KEY = "displayOrder";

    public static ParseQuery<Category> getQuery(){
        return ParseQuery.getQuery(Category.class);
    }

    public String getName(){
        return getString(NAME_KEY);
    }

    public ParseRelation<CategoryJoin> getCategoryItems() {
        return getRelation(CATEGORY_ITEMS_KEY);
    }

    public String getCaption() {
        return getString(CAPTION_KEY);
    }

    public String getTitle() {
        return getString(TITLE_KEY);
    }

    public String getPictureUrl(){
        return getParseFile(PICTURE_KEY).getUrl();
    }

    public int getDisplayOrder(){
        return getInt(DISPLAY_ORDER_KEY);
    }

}
