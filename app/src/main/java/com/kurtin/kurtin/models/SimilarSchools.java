package com.kurtin.kurtin.models;

import java.util.List;

/**
 * Created by cvar on 3/3/17.
 */

public class SimilarSchools extends Media {
    List<School> mSchools;

    public SimilarSchools(List<School> schools){
        super(MediaType.SIMILAR_SCHOOLS);
        mSchools = schools;
    }

    public List<School> getSchools() {
        return mSchools;
    }
}
