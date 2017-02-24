package com.kurtin.kurtin.listeners;

import com.kurtin.kurtin.models.School;
import com.parse.ParseRelation;

/**
 * Created by cvar on 2/10/17.
 */

public interface KurtinNavListener {
    void onTestFragmentRequested();
    void onAuthManagerFragmentRequested();
    void onKurtinLoginFragmentRequested();
    void onCategoriesFragmentRequested();
    void onSchoolListFragmentRequested(String categoryTypeObjId);
}
