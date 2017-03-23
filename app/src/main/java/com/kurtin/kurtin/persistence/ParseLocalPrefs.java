package com.kurtin.kurtin.persistence;

import android.content.Context;

import java.util.List;
import java.util.Set;


/**
 * Created by cvar on 2/23/17.
 */

public class ParseLocalPrefs {

    public static final String TAG = "ParseLocalPrefs";

    //Pref filename
    private static final String PREFERENCES_FILE = "ParseLocalPrefs";
    //Pref keys
    private static final String PRIMARY_LOGIN_PLATFORM_KEY = "primaryLoginPlatformKey";

    //Pinning keys
    public static final String SESSION_PIN = "sessionPin";
    public static final String CATEGORY_SCHOOLS_PIN = "categorySchoolsPin";
    public static final String CATEGORY_PIN = "categoryPin";

    //Data Keys
    private static final String CATEGORIES_ARE_SAVED_KEY = "categoriesAreSaved";
    private static final String CATEGORY_SCHOOLS_ARE_SAVED_KEY = "categorySchoolsAreSaved";
    private static final String CATEGORY_IS_SAVED_KEY = "categoryIsSaved";
    private static final String CATEGORY_SCHOOLS_KEY = "categorySchools";
    private static final String CATEGORY_NAME_KEY = "categoryName";
    private static final String CATEGORY_ID_KEY = "categoryId";
    private static final String SCHOOL_IS_SAVED_KEY = "schoolIsSaved";
    private static final String SCHOOL_NAME_KEY = "schoolName";
    private static final String SCHOOL_ID_KEY = "schoolId";

    //Generic data
    public static boolean categoriesAreSaved(Context context){
        return context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .getBoolean(CATEGORIES_ARE_SAVED_KEY, false);
    }
    public static void setCategoriesAreSaved(Context context, boolean categoriesSavedStatus){
        context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .edit().putBoolean(CATEGORIES_ARE_SAVED_KEY, categoriesSavedStatus).apply();
    }

    //Selected Category Schools
    //**********
    public static boolean categorySchoolsAreSaved(Context context){
        return context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .getBoolean(CATEGORY_SCHOOLS_ARE_SAVED_KEY, false);
    }
    public static void setCategorySchoolsAreSaved(Context context, boolean schoolsSavedStatus){
        context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .edit().putBoolean(CATEGORY_SCHOOLS_ARE_SAVED_KEY, schoolsSavedStatus).apply();
    }
    public static Set<String> getSelectedCategorySchoolNames(Context context){
        return context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .getStringSet(CATEGORY_SCHOOLS_KEY, null);
    }
    public static void setSelectedCategorySchoolNames(Context context, Set<String> schoolNames){
        context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .edit().putStringSet(CATEGORY_SCHOOLS_KEY, schoolNames).apply();
    }

    //Selected Category
    //=================
    public static boolean selectedCategoryIsSaved(Context context){
        return context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .getBoolean(CATEGORY_IS_SAVED_KEY, false);
    }
    public static void setSelectedCategoryIsSaved(Context context, boolean schoolsSavedStatus){
        context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .edit().putBoolean(CATEGORY_IS_SAVED_KEY, schoolsSavedStatus).apply();
    }
    public static String getSelectedCategoryName(Context context){
        return context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .getString(CATEGORY_NAME_KEY, null);
    }
    public static void setSelectedCategoryName(Context context, String categoryName){
        context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .edit().putString(CATEGORY_NAME_KEY, categoryName).apply();
    }
    public static String getSelectedCategoryId(Context context){
        return context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .getString(CATEGORY_ID_KEY, null);
    }
    public static void setSelectedCategoryId(Context context, String categoryId){
        context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .edit().putString(CATEGORY_ID_KEY, categoryId).apply();
    }

    //Selected School
    //===============
    public static boolean selectedSchoolIsSaved(Context context){
        return context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .getBoolean(SCHOOL_IS_SAVED_KEY, false);
    }
    public static void setSelectedSchoolIsSaved(Context context, boolean schoolsSavedStatus){
        context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .edit().putBoolean(SCHOOL_IS_SAVED_KEY, schoolsSavedStatus).apply();
    }
    public static String getSelectedSchoolName(Context context){
        return context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .getString(SCHOOL_NAME_KEY, null);
    }
    public static void setSelectedSchoolName(Context context, String categoryName){
        context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .edit().putString(SCHOOL_NAME_KEY, categoryName).apply();
    }
    public static String getSelectedSchoolId(Context context){
        return context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .getString(SCHOOL_ID_KEY, null);
    }
    public static void setSelectedSchoolId(Context context, String schoolId){
        context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                .edit().putString(SCHOOL_ID_KEY, schoolId).apply();
    }
}
