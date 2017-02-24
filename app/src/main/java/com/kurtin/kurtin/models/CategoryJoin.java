package com.kurtin.kurtin.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseRelation;

/**
 * Created by cvar on 2/23/17.
 *
 * Category.java is a model for a join table in the back end that connects a category type to a school
 *
 */

@ParseClassName("CategoryJoin")
public class CategoryJoin extends ParseObject {

    private static final String CATEGORY_NAME_KEY = "categoryName";
    private static final String SCHOOL_NAME_KEY = "schoolName";
    private static final String SCHOOL_RANK_KEY = "schoolRank";
    public static final String SCHOOL_KEY = "school";

    public String getCategoryName() {
        return getString(CATEGORY_NAME_KEY);
    }

    public String getSchoolName() {
        return getString(SCHOOL_NAME_KEY);
    }

    public String getSchoolRank() {
        return getString(SCHOOL_RANK_KEY);
    }

    public School getSchool(){
        return (School) getParseObject(SCHOOL_KEY);
    }

    public void setCategoryName(String categoryName) {
        put(CATEGORY_NAME_KEY, categoryName);
    }

    public void setSchoolName(String schoolName) {
        put(SCHOOL_NAME_KEY, schoolName);
    }

    public void setSchoolRank(String schoolRank) {
        put(SCHOOL_RANK_KEY, schoolRank);
    }
}
