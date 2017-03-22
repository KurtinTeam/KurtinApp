package com.kurtin.kurtin.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kurtin.kurtin.R;
import com.kurtin.kurtin.activities.MainActivity;
import com.kurtin.kurtin.adapters.SchoolDetailAdapter;
import com.kurtin.kurtin.helpers.ImageHelper;
import com.kurtin.kurtin.helpers.ItemClickSupport;
import com.kurtin.kurtin.helpers.ScreenUtils;
import com.kurtin.kurtin.models.Category;
import com.kurtin.kurtin.models.CategoryJoin;
import com.kurtin.kurtin.models.FacebookMedia;
import com.kurtin.kurtin.models.Media;
import com.kurtin.kurtin.models.School;
import com.kurtin.kurtin.models.TwitterMedia;
import com.kurtin.kurtin.persistence.ParseLocalPrefs;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SchoolDetailFragment extends Fragment {

    public static final String TAG = "SchoolDetailFragment";
    public static final String TITLE = "School";

    private static final String SCHOOL_OBJ_ID_KEY = "schoolObjectId";
    private static final String CATEGORY_OBJ_ID_KEY = "categoryObjectId";
    private static final String CATEGORY_NAME_KEY = "categoryName";

    private RecyclerView rvMedia;
    private SchoolDetailAdapter mSchoolDetailAdapter;
    GridLayoutManager.SpanSizeLookup mSpanSizeLookup;
    private LinearLayout llProgress;
    private List<Media> mMediaList;
    private List<School> mSimilarSchools;
    private School mSchool;

    private boolean mSchoolReceived;
    private boolean mSimilarSchoolsReceived;
    private String mSelectedSchoolId;

    //Detail Popup
    //============
    RelativeLayout rlMediaDetail;
    // Header
    private SimpleDraweeView sdvLogo;
    private TextView tvSchoolName;
    // Main content
    private SimpleDraweeView sdvMediaImage;
    // Content info
    private ImageView ivPlatformIcon;
    private TextView tvPlatformName;
    private TextView tvMediaText;
    private Button btnCloseMediaDetail;
//    Float mStatusBarHeight = null;

    public SchoolDetailFragment() {
        // Required empty public constructor
    }

    public static SchoolDetailFragment newInstance(String schoolObjId, String categoryObjId, String categoryName) {

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
        ((MainActivity) getActivity()).hideSchoolDetailMediaView();
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
        ItemClickSupport.addTo(rvMedia).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                showMediaDetail(position);
            }
        });

        Log.d(TAG, "Inside bindUiElements() about to bind Detail Popup");
        //Detail Popup
        //=============
        rlMediaDetail = (RelativeLayout) view.findViewById(R.id.rlMediaDetail);
        sdvLogo = (SimpleDraweeView) view.findViewById(R.id.sdvLogo);
        tvSchoolName = (TextView) view.findViewById(R.id.tvSchoolName);
        sdvMediaImage = (SimpleDraweeView) view.findViewById(R.id.sdvMediaImage);
        ivPlatformIcon = (ImageView) view.findViewById(R.id.ivPlatformIcon);
        tvPlatformName = (TextView) view.findViewById(R.id.tvPlatformName);
        tvMediaText = (TextView) view.findViewById(R.id.tvMediaText);
        btnCloseMediaDetail = (Button) view.findViewById(R.id.btnCloseMediaDetail);
        //Set up fresco hierarchies
        sdvLogo.setHierarchy(ImageHelper.getCircleHierarchy(getResources()));
        sdvMediaImage.setHierarchy(ImageHelper.getBasicHierarchy(getResources()));
        //Click listener
        btnCloseMediaDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideMediaDetail();
            }
        });
        Log.d(TAG, "Leaving bindUiElements()");
    }

    private void getContent(){
        Log.d(TAG, "Inside getContent()");
        getSchools();
//        getSchool();
//        getSimilarSchools();
    }

    private void getSchools(){
        mSchoolReceived = false;
        mSimilarSchoolsReceived = false;
        llProgress.setVisibility(View.VISIBLE);
//        mSchoolReceived = false;

//        final String schoolObjId = getArguments().getString(SCHOOL_OBJ_ID_KEY);
        mSelectedSchoolId = ParseLocalPrefs.getSelectedSchoolId(getContext());
        Log.v(TAG, "School objId: " + mSelectedSchoolId);

        if (ParseLocalPrefs.categorySchoolsAreSaved(getContext())) {
            Log.d(TAG, "Getting schools from local data store");
            ParseQuery<School> schoolQuery = School.getQuery();
            schoolQuery.fromPin(ParseLocalPrefs.CATEGORY_SCHOOLS_PIN);
            schoolQuery.findInBackground(new FindCallback<School>() {
                @Override
                public void done(List<School> schools, ParseException e) {
                    if (e == null) {
                        if(schools.size() != 0) {
                            Log.d(TAG, "number of schools: " + schools.size());
                            mSimilarSchools = schools;
                            for (School school : schools) {
                                if (school.getObjectId().equals(mSelectedSchoolId)) {
                                    Log.d(TAG, "Found selected school in list");
                                    mSchool = school;
                                    if (mSchool.isDataAvailable()){
                                        Log.d(TAG, "Data Available");
                                        mSchoolReceived = true;
                                        makeMediaList();
                                    }else{
                                        Log.d(TAG, "Data Not Available");
                                    }
                                    mSchool.fetchIfNeededInBackground(new GetCallback<School>() {
                                        @Override
                                        public void done(School school, ParseException e) {
                                            Log.d(TAG, "Found school: " + school.getName());
                                            mSchoolReceived = true;
                                            makeMediaList();
                                        }
                                    });
                                    break;
                                }
                            }
                            Log.d(TAG, "Similar Schools: " + mSimilarSchools);
                            Log.d(TAG, "School: " + mSchool);
                            mSimilarSchoolsReceived = true;
                            makeMediaList();
                        }else{
                            getSchoolsFromRemoteUsingCategoryName();
                        }
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }else{
            getSchoolsFromRemoteUsingCategoryName();
        }
    }

    private void getSchoolsFromRemoteUsingCategoryName(){
        final long nameQueryStartTime = System.currentTimeMillis();
        Log.d(TAG, "Querying remote DB for schools");
//        final String schoolObjId = getArguments().getString(SCHOOL_OBJ_ID_KEY);
//        String categoryObjectId = getArguments().getString(CATEGORY_OBJ_ID_KEY);
//        String categoryName = getArguments().getString(CATEGORY_NAME_KEY);
        String categoryObjectId = ParseLocalPrefs.getSelectedCategoryId(getContext());
        mSelectedSchoolId = ParseLocalPrefs.getSelectedSchoolId(getContext());

        final ParseQuery<Category> categoryQuery = Category.getQuery();
        if (categoryObjectId == null) {
            String categoryName = "Top LA College";
            categoryQuery.whereEqualTo(Category.NAME_KEY, categoryName);
            Log.d(TAG, "Searching for default Category: " + categoryName);
        }else{
            Log.d(TAG, "Searching for Category with Id: " + categoryObjectId);
            categoryQuery.whereEqualTo(Category.OBJECT_ID_KEY, categoryObjectId);
        }
        categoryQuery.getFirstInBackground(new GetCallback<Category>() {
            @Override
            public void done(Category category, ParseException e) {
                final long categoryByNameQueryEndTime = System.currentTimeMillis();
                Log.d(TAG, "Remote name/id query returned in " + (categoryByNameQueryEndTime - nameQueryStartTime) + " ms");
                if (e==null) {
                    ParseQuery<School> schoolQuery = category.getSchools().getQuery();
                    schoolQuery.selectKeys(School.BASIC_DISPLAY_KEYS);
                    schoolQuery.findInBackground(new FindCallback<School>() {
                        @Override
                        public void done(List<School> schools, ParseException e) {
                            long schoolQueryEndTime = System.currentTimeMillis();
                            Log.d(TAG, "Remote school query returned in " + (schoolQueryEndTime - categoryByNameQueryEndTime) + " ms");
                            for(School school: schools){
                                if (school.getObjectId().equals(mSelectedSchoolId)) {
                                    mSchool = school;
                                    mSchool.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                                        @Override
                                        public void done(ParseObject object, ParseException e) {
                                            mSchoolReceived = true;
                                            makeMediaList();
                                        }
                                    });
                                    break;
                                }
                            }
                            mSimilarSchools = schools;
                            mSimilarSchoolsReceived = true;
                            makeMediaList();
                        }
                    });
                }
            }
        });

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
        Log.d(TAG, "Getting similar schools. Category ObjId: " + getArguments().getString(CATEGORY_OBJ_ID_KEY));
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
//                                            ParseObject.pinAllInBackground(ParseLocalPrefs.CurrentSessionKey, schools);

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
                                    // ParseObject.pinAllInBackground(ParseLocalPrefs.CurrentSessionKey, schools);
                                }
                            }
                        });

                    }
                });
    }

    private void makeMediaList(){
        Log.d(TAG, "Inside makeMediaList()");
        Log.d(TAG, "School-Received/Similar-Schools_Recieved: " + mSchoolReceived + "/" + mSimilarSchoolsReceived);
        if(mSchoolReceived && mSimilarSchoolsReceived){
            mMediaList.clear();
            Log.d(TAG, "About to create media list");
            Log.d(TAG, "School Name: " + mSchool.getName());
            Log.d(TAG, "Number of similar schools: " + mSimilarSchools.size());
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

    private void showMediaDetail(int position){
        Log.d(TAG, "Inside showMediaDetail()");
        Media media = mMediaList.get(position);
        boolean displayMedia = false;
        switch (media.getMediaType()){
            case TWITTER:
                displayMedia = true;
                TwitterMedia twitterMedia = (TwitterMedia) media;
                sdvLogo.setImageURI(mSchool.getLogoImage().getUrl());
                tvSchoolName.setText(mSchool.getName());
                sdvMediaImage.setImageURI(twitterMedia.getPictureUrl());
                ivPlatformIcon.setImageResource(R.drawable.ic_tw_color);
                tvPlatformName.setText('@' + mSchool.getTwScreenName());
                tvMediaText.setText(twitterMedia.getFullText());
                break;
            case FACEBOOK:
                displayMedia = true;
                FacebookMedia facebookMedia = (FacebookMedia) media;
                sdvLogo.setImageURI(mSchool.getLogoImage().getUrl());
                tvSchoolName.setText(mSchool.getName());
                sdvMediaImage.setImageURI(facebookMedia.getFullPictureUrl());
                ivPlatformIcon.setImageResource(R.drawable.ic_fb_color);
                tvPlatformName.setText(mSchool.getFacebookUrl());
                tvMediaText.setText(facebookMedia.getMessage());
                break;
        }

        if(displayMedia){
            ((MainActivity) getActivity()).showSchoolDetailMediaView();
            rlMediaDetail.setVisibility(View.VISIBLE);
            rvMedia.setVisibility(View.GONE);
        }
    }

    private void hideMediaDetail(){
        Log.d(TAG, "Inside hideMediaDetail()");
        ((MainActivity) getActivity()).hideSchoolDetailMediaView();
        rvMedia.setVisibility(View.VISIBLE);
        rlMediaDetail.setVisibility(View.GONE);
    }

}
