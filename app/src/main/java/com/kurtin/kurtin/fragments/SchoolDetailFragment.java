package com.kurtin.kurtin.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kurtin.kurtin.R;
import com.kurtin.kurtin.adapters.SchoolDetailAdapter;
import com.kurtin.kurtin.models.Category;
import com.kurtin.kurtin.models.CategoryJoin;
import com.kurtin.kurtin.models.Media;
import com.kurtin.kurtin.models.School;
import com.kurtin.kurtin.persistence.ParseLocal;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.ArrayList;
import java.util.List;

import static com.kurtin.kurtin.R.id.rvCategories;
import static java.security.AccessController.getContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class SchoolDetailFragment extends Fragment {

    public static final String TAG = "SchoolDetailFragment";
    public static final String TITLE = "School";

    private static final String SCHOOL_OBJ_ID_KEY = "schoolObjectId";
    private static final String CATEGORY_OBJ_ID_KEY = "categoryObjectId";

    private RecyclerView rvMedia;
    private SchoolDetailAdapter mSchoolDetailAdapter;
    GridLayoutManager.SpanSizeLookup mSpanSizeLookup;
    private LinearLayout llProgress;
    private List<Media> mMediaList;
    private List<School> mSimilarSchools;
    private School mSchool;

    private boolean mSchoolReceived;
    private boolean mSimilarSchoolsReceived;

    public SchoolDetailFragment() {
        // Required empty public constructor
    }

    public static SchoolDetailFragment newInstance(String schoolObjId, String categoryObjId) {

        Bundle args = new Bundle();
        args.putString(SCHOOL_OBJ_ID_KEY, schoolObjId);
        args.putString(CATEGORY_OBJ_ID_KEY, categoryObjId);
        SchoolDetailFragment fragment = new SchoolDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_school_detail, container, false);
        bindUiElements(view);
        getContent();

        return view;
    }

    private void bindUiElements(View view){
        llProgress = (LinearLayout) view.findViewById(R.id.llProgress);
        rvMedia = (RecyclerView) view.findViewById(R.id.rvMedia);
        mMediaList = new ArrayList<>();
        mSimilarSchools = new ArrayList<>();

        initSpanLookup();
        int numberOfColumns = 6;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), numberOfColumns);
        gridLayoutManager.setSpanSizeLookup(mSpanSizeLookup);

        mSchoolDetailAdapter = new SchoolDetailAdapter(mMediaList, numberOfColumns, getContext());
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvMedia.setAdapter(mSchoolDetailAdapter);
        rvMedia.setLayoutManager(gridLayoutManager);
    }

    private void getContent(){
        mSchoolReceived = false;
        mSimilarSchoolsReceived = false;
        getSchool();
        getSimilarSchools();
    }

    private void getSchool(){
        llProgress.setVisibility(View.VISIBLE);
        mSchoolReceived = false;

        String schoolObjId = getArguments().getString(SCHOOL_OBJ_ID_KEY);
        Log.v(TAG, "School objId: " + schoolObjId);
        ParseQuery<School> schoolQuery = School.getQuery();
        schoolQuery.fromLocalDatastore();
        schoolQuery.getInBackground(
                schoolObjId,
                new GetCallback<School>() {
                    @Override
                    public void done(School school, ParseException e) {
                        if(e==null){
                            mSchool = school;
                            mSchoolReceived = true;
                            makeMediaList();
                        }else {
                            e.printStackTrace();
                        }

                    }
                });
    }

    private void getSimilarSchools(){
        mSimilarSchoolsReceived = false;
        ParseQuery<Category> categoryQuery = Category.getQuery();
        categoryQuery.fromLocalDatastore();
        categoryQuery.getInBackground(
                getArguments().getString(CATEGORY_OBJ_ID_KEY),
                new GetCallback<Category>() {
                    @Override
                    public void done(Category category, ParseException e) {
                        ParseRelation<CategoryJoin> schoolsRelation = category.getCategoryItems();
                        ParseQuery<CategoryJoin> categoryJoinQuery = schoolsRelation.getQuery();
                        categoryJoinQuery.include(CategoryJoin.SCHOOL_KEY);
                        categoryJoinQuery.findInBackground(new FindCallback<CategoryJoin>() {
                            @Override
                            public void done(List<CategoryJoin> categoryJoinList, ParseException e) {
                                // Remove this if statement once all categories have schools attached to them
                                if(categoryJoinList.isEmpty()){
                                    Log.v(TAG, "categoryJoinList is empty " + categoryJoinList.toString());
                                    int numSchools = 12;
                                    ParseQuery<School> schoolQuery = School.getQuery();
                                    schoolQuery.setLimit(numSchools);
                                    schoolQuery.findInBackground(new FindCallback<School>() {
                                        @Override
                                        public void done(List<School> schools, ParseException e) {
                                            mSimilarSchools.clear();
                                            mSimilarSchools.addAll(schools);
                                            mSimilarSchoolsReceived = true;
                                            makeMediaList();
//                                            ParseObject.pinAllInBackground(ParseLocal.CurrentSessionKey, schools);

                                            Toast.makeText(getContext(), "Showing generic School list.\nCategory coming soon.", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }else {
                                    Log.v(TAG, "categoryJoinList is not empty " + categoryJoinList.toString());
                                    List<School> schools = new ArrayList<>();
                                    for (CategoryJoin categoryJoin : categoryJoinList) {
                                        schools.add(categoryJoin.getSchool());
                                    }
                                    mSimilarSchools.clear();
                                    mSimilarSchools.addAll(schools);
                                    mSimilarSchoolsReceived = true;
                                    makeMediaList();
                                    // ParseObject.pinAllInBackground(ParseLocal.CurrentSessionKey, schools);
                                }
                            }
                        });

                    }
                });
    }

    private void makeMediaList(){
        if(mSchoolReceived && mSimilarSchoolsReceived){
            mMediaList.clear();
            mMediaList.addAll(Media.getMediaListFromSchool(mSchool, mSimilarSchools));
            Log.v(TAG, "mMediaList returned.  Size: " + mMediaList.size());
            mSchoolDetailAdapter.notifyDataSetChanged();
            llProgress.setVisibility(View.GONE);
        }
    }

    private void initSpanLookup(){
        mSpanSizeLookup = new GridLayoutManager.SpanSizeLookup(){
            @Override
            public int getSpanSize(int position){
                return mSchoolDetailAdapter.getSpanSize(position);
            }
        };
    }

}
