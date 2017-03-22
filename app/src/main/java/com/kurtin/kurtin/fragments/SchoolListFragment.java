package com.kurtin.kurtin.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kurtin.kurtin.R;
import com.kurtin.kurtin.adapters.SchoolListAdapter;
import com.kurtin.kurtin.helpers.ItemClickSupport;
import com.kurtin.kurtin.listeners.KurtinNavListener;
import com.kurtin.kurtin.models.Category;
import com.kurtin.kurtin.models.CategoryJoin;
import com.kurtin.kurtin.models.School;
import com.kurtin.kurtin.persistence.ParseLocalPrefs;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.R.attr.category;

/**
 * A simple {@link Fragment} subclass.
 */
public class SchoolListFragment extends Fragment {

    public static final String TAG = "SchoolListFragment";
    public static final String TITLE = "Schools";

    private static final String CATEGORY_TYPE_OBJ_ID = "categoryTypeObjId";

    List<School> mSchools;
    RecyclerView rvSchools;
    SchoolListAdapter mSchoolListAdapter;
    LinearLayout llProgress;
    Category mCategory;

    KurtinNavListener mKurtinNavListener;

    public SchoolListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "Creating View");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_school_list, container, false);
        bindUiElements(view);
        loadData();
        return view;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            mKurtinNavListener = (KurtinNavListener) context;
        }catch (Exception e){
            mKurtinNavListener = null;
            e.printStackTrace();
            Log.e(TAG, "Must implement KurtinNavListener");
        }
        Log.d(TAG, "Inside onAttach");
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mKurtinNavListener = null;
    }


    public static SchoolListFragment newInstance(String categoryTypeObjId) {

        Bundle args = new Bundle();
        args.putString(CATEGORY_TYPE_OBJ_ID, categoryTypeObjId);
        SchoolListFragment fragment = new SchoolListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void bindUiElements(View view){
        rvSchools = (RecyclerView) view.findViewById(R.id.rvSchools);
        mSchools = new ArrayList<>();
        mSchoolListAdapter = new SchoolListAdapter(mSchools, getContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvSchools.setLayoutManager(layoutManager);
        rvSchools.setAdapter(mSchoolListAdapter);
        ItemClickSupport.addTo(rvSchools).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                //Launch a school detail view.
                School selectedSchool = mSchools.get(position);
                Log.v(TAG, "Selected School: " + selectedSchool.getName() + " -> " + selectedSchool.getObjectId());
                ParseLocalPrefs.setSelectedSchoolName(getContext(), selectedSchool.getName());
                ParseLocalPrefs.setSelectedSchoolId(getContext(), selectedSchool.getObjectId());
                mKurtinNavListener.onSchoolDetailFragmentRequested(null, null, null);
//                mKurtinNavListener.onSchoolDetailFragmentRequested(mSchools.get(position).getObjectId(), mCategory.getObjectId(), mCategory.getName());

            }
        });
        llProgress = (LinearLayout) view.findViewById(R.id.llProgress);
    }

    private void loadData(){
        llProgress.setVisibility(View.VISIBLE);
        if (ParseLocalPrefs.categorySchoolsAreSaved(getContext())) {
            loadSchools();
        } else{
            loadCategory();
        }
    }

    private void loadCategory(){
//        Bundle args = getArguments();
//        String categoryObjId = args.getString(CATEGORY_TYPE_OBJ_ID);
        String categoryObjId = ParseLocalPrefs.getSelectedCategoryId(getContext());
        ParseQuery<Category> categoryQuery = Category.getQuery();
        if (ParseLocalPrefs.categoriesAreSaved(getContext())) {
            Log.v(TAG, "Getting selected category from local data");
            categoryQuery.fromLocalDatastore();
        }else{
            Log.v(TAG, "Getting selected category from remote data");
        }
        categoryQuery.getInBackground(categoryObjId, new GetCallback<Category>() {
            @Override
            public void done(Category category, ParseException e) {
                if (e==null) {
                    mCategory = category;
                    loadSchools();
                }else{
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadSchools(){

        final long firstQueryStartTime = System.currentTimeMillis();

        if (ParseLocalPrefs.categorySchoolsAreSaved(getContext())) {
            Log.d(TAG, "Getting schools from local datastore");
            ParseQuery <School> schoolQuery = School.getQuery();
            schoolQuery.fromPin(ParseLocalPrefs.CATEGORY_SCHOOLS_PIN);
            schoolQuery.findInBackground(new FindCallback<School>() {
                @Override
                public void done(List<School> schools, ParseException e) {
                    if (e==null) {
                        Log.d(TAG, "Number of schools returned: " + schools.size());
                        if(schools.size() != 0) {
                            mSchools.clear();
                            mSchools.addAll(schools);
                            mSchoolListAdapter.notifyDataSetChanged();
                            llProgress.setVisibility(View.GONE);
                        }else{
                            Log.e(TAG, "No schools returned but app thinks schools are stored locally");
                            ParseLocalPrefs.setCategorySchoolsAreSaved(getContext(), false);
                            loadCategory();
                        }
                    }else{
                        e.printStackTrace();
                    }
                }
            });
        }else {

            ParseRelation<School> schoolRelation = mCategory.getSchools();
            ParseQuery<School> schoolQuery = schoolRelation.getQuery();
            schoolQuery.selectKeys(Arrays.asList("name", "state", "city", "studentImage", "logoImage"));
            schoolQuery.findInBackground(new FindCallback<School>() {
                @Override
                public void done(List<School> schools, ParseException e) {
                    ParseLocalPrefs.setCategorySchoolsAreSaved(getContext(), false);
                    final long secondQueryStartTime = System.currentTimeMillis();
                    Log.v(TAG, "First loadSchools Query took: " + (secondQueryStartTime - firstQueryStartTime) + " ms");
                    // Remove this if statement once all categories have schools attached to them
                    if (schools.isEmpty()) {
                        Log.v(TAG, "No schools returned. Performing basic school search instead.");
                        int numSchools = 25;
                        ParseQuery<School> schoolQuery = School.getQuery();
                        schoolQuery.setLimit(numSchools);
                        schoolQuery.findInBackground(new FindCallback<School>() {
                            @Override
                            public void done(final List<School> schools, ParseException e) {
                                long secondQueryEndTime = System.currentTimeMillis();
                                Log.v(TAG, "Second loadSchools Query took: " + (secondQueryEndTime - secondQueryStartTime) + " ms");

                                mSchools.clear();
                                mSchools.addAll(schools);

                                final long pinOneStartTime = System.currentTimeMillis();
                                ParseLocalPrefs.setCategorySchoolsAreSaved(getContext(), false);
                                Log.d(TAG, "About to unpin category schools");
                                ParseObject.unpinAllInBackground(ParseLocalPrefs.CATEGORY_SCHOOLS_PIN, new DeleteCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Log.d(TAG, "About to pin schools. Size of mSchools" + mSchools.size());
                                            ParseObject.pinAllInBackground(ParseLocalPrefs.CATEGORY_SCHOOLS_PIN, mSchools, new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    ParseLocalPrefs.setCategorySchoolsAreSaved(getContext().getApplicationContext(), true);
                                                    long pinEndTime = System.currentTimeMillis();
                                                    Log.v(TAG, "School list pinning took: " + (pinEndTime - pinOneStartTime) / 1000.0 + " secs");
                                                }
                                            });
                                        } else {
                                            e.printStackTrace();
                                        }
                                    }
                                });

//                                if (ParseLocalPrefs.categorySchoolsAreSaved(getContext())) {
//                                    ParseLocalPrefs.setCategorySchoolsAreSaved(getContext(), false);
//                                    Log.d(TAG, "About to unpin category schools");
//                                    ParseObject.unpinAllInBackground(ParseLocalPrefs.CATEGORY_SCHOOLS_PIN, new DeleteCallback() {
//                                        @Override
//                                        public void done(ParseException e) {
//                                            if (e == null) {
//                                                Log.d(TAG, "About to pin schools. Size of mSchools" + mSchools.size());
//                                                ParseObject.pinAllInBackground(ParseLocalPrefs.CATEGORY_SCHOOLS_PIN, mSchools, new SaveCallback() {
//                                                    @Override
//                                                    public void done(ParseException e) {
//                                                        ParseLocalPrefs.setCategorySchoolsAreSaved(getContext().getApplicationContext(), true);
//                                                        long pinEndTime = System.currentTimeMillis();
//                                                        Log.v(TAG, "School list pinning took: " + (pinEndTime - pinOneStartTime) / 1000.0 + " secs");
//                                                    }
//                                                });
//                                            } else {
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                    });
//                                } else {
//                                    Log.d(TAG, "About to pin schools. Size of mSchools" + mSchools.size());
//                                    ParseObject.pinAllInBackground(ParseLocalPrefs.CATEGORY_SCHOOLS_PIN, mSchools, new SaveCallback() {
//                                        @Override
//                                        public void done(ParseException e) {
//                                            ParseLocalPrefs.setCategorySchoolsAreSaved(getContext().getApplicationContext(), true);
//                                            long pinEndTime = System.currentTimeMillis();
//                                            Log.v(TAG, "School list pinning took: " + (pinEndTime - pinOneStartTime) / 1000.0 + " secs");
//                                        }
//                                    });
//                                }

                                mSchools.clear();
                                mSchools.addAll(schools);
                                mSchoolListAdapter.notifyDataSetChanged();
                                llProgress.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Showing generic School list.\nCategory coming soon.", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        mSchools.clear();
                        mSchools.addAll(schools);

                        // Save results to local data store
                        final long pinOneStartTime = System.currentTimeMillis();
                        ParseLocalPrefs.setCategorySchoolsAreSaved(getContext(), false);
                        Log.d(TAG, "About to unpin category schools");
                        ParseObject.unpinAllInBackground(ParseLocalPrefs.CATEGORY_SCHOOLS_PIN, new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.d(TAG, "About to pin schools. Size of mSchools" + mSchools.size());
                                    ParseObject.pinAllInBackground(ParseLocalPrefs.CATEGORY_SCHOOLS_PIN, mSchools, new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e==null) {
                                                ParseLocalPrefs.setCategorySchoolsAreSaved(getContext().getApplicationContext(), true);
                                                long pinEndTime = System.currentTimeMillis();
                                                Log.v(TAG, "School list pinning took: " + (pinEndTime - pinOneStartTime) / 1000.0 + " secs");
                                            }else{
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    e.printStackTrace();
                                }
                            }
                        });

//                        if (ParseLocalPrefs.categorySchoolsAreSaved(getContext())) {
//                            ParseLocalPrefs.setCategorySchoolsAreSaved(getContext(), false);
//                            Log.d(TAG, "About to unpin category schools");
//                            ParseObject.unpinAllInBackground(ParseLocalPrefs.CATEGORY_SCHOOLS_PIN, new DeleteCallback() {
//                                @Override
//                                public void done(ParseException e) {
//                                    if (e == null) {
//                                        Log.d(TAG, "About to pin schools. Size of mSchools" + mSchools.size());
//                                        ParseObject.pinAllInBackground(ParseLocalPrefs.CATEGORY_SCHOOLS_PIN, mSchools, new SaveCallback() {
//                                            @Override
//                                            public void done(ParseException e) {
//                                                if (e==null) {
//                                                    ParseLocalPrefs.setCategorySchoolsAreSaved(getContext().getApplicationContext(), true);
//                                                    long pinEndTime = System.currentTimeMillis();
//                                                    Log.v(TAG, "School list pinning took: " + (pinEndTime - pinOneStartTime) / 1000.0 + " secs");
//                                                }else{
//                                                    e.printStackTrace();
//                                                }
//                                            }
//                                        });
//                                    } else {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            });
//                        } else {
//                            Log.d(TAG, "About to pin schools. Size of mSchools" + mSchools.size());
//                            ParseObject.pinAllInBackground(ParseLocalPrefs.CATEGORY_SCHOOLS_PIN, mSchools, new SaveCallback() {
//                                @Override
//                                public void done(ParseException e) {
//                                    if (e==null) {
//                                        ParseLocalPrefs.setCategorySchoolsAreSaved(getContext().getApplicationContext(), true);
//                                        long pinEndTime = System.currentTimeMillis();
//                                        Log.v(TAG, "School list pinning took: " + (pinEndTime - pinOneStartTime) / 1000.0 + " secs");
//                                    }else{
//                                        e.printStackTrace();
//                                    }
//                                }
//                            });
//                        }

                        mSchoolListAdapter.notifyDataSetChanged();
                        llProgress.setVisibility(View.GONE);

                    }
                }
            });
        }





//        ParseQuery<CategoryJoin> categoryJoinQueryTest = CategoryJoin.getQuery();
//        categoryJoinQueryTest.whereEqualTo(CategoryJoin.CATEGORY_NAME_KEY, category.getName());
////        categoryJoinQueryTest.include(CategoryJoin.SCHOOL_KEY);
//        categoryJoinQueryTest.findInBackground(new FindCallback<CategoryJoin>() {
//            @Override
//            public void done(List<CategoryJoin> categoryJoinList, ParseException e) {
//                ParseLocalPrefs.setSavedCategorySchools(getContext(), false);
//                final long secondQueryStartTime = System.currentTimeMillis();
//                Log.v(TAG, "First loadSchools Query took: " + (secondQueryStartTime - firstQueryStartTime) + " ms");
//                // Remove this if statement once all categories have schools attached to them
//                if(categoryJoinList.isEmpty()){
//                    Log.v(TAG, "categoryJoinList is empty " + categoryJoinList.toString());
//                    int numSchools = 25;
//                    ParseQuery<School> schoolQuery = School.getQuery();
//                    schoolQuery.setLimit(numSchools);
//                    schoolQuery.findInBackground(new FindCallback<School>() {
//                        @Override
//                        public void done(List<School> schools, ParseException e) {
//                            long secondQueryEndTime = System.currentTimeMillis();
//                            mSchools.clear();
//                            mSchools.addAll(schools);
//                            mSchoolListAdapter.notifyDataSetChanged();
//                            llProgress.setVisibility(View.GONE);
//                            ParseObject.unpinAllInBackground(ParseLocalPrefs.CurrentCategorySchoolsKey, new DeleteCallback() {
//                                @Override
//                                public void done(ParseException e) {
//                                    if (e==null){
//                                        ParseObject.pinAllInBackground(ParseLocalPrefs.CurrentCategorySchoolsKey, mSchools, new SaveCallback() {
//                                            @Override
//                                            public void done(ParseException e) {
//                                                ParseLocalPrefs.setSavedCategorySchools(getContext().getApplicationContext(), true);
//                                                long pinEndTime = System.currentTimeMillis();
////                                                Log.v(TAG, "Pinning ops: " + (pinEndTime - pinOneStartTime) + " ms");
//                                            }
//                                        });
//                                    }else{
//                                        e.printStackTrace();
//                                    }
//                                }
//                            });
//
//                            Log.v(TAG, "Second loadSchools Query took: " + (secondQueryEndTime - secondQueryStartTime) + " ms");
//                            Toast.makeText(getContext(), "Showing generic School list.\nCategory coming soon.", Toast.LENGTH_LONG).show();
//                        }
//                    });
//                }else {
//                    ParseLocalPrefs.setSavedCategorySchools(getContext(), false);
//                    long getSchoolsLoopStartTime = System.currentTimeMillis();
//                    Log.v(TAG, "categoryJoinList is not empty " + categoryJoinList.toString());
//                    List<School> schools = new ArrayList<>();
//                    for (CategoryJoin categoryJoin : categoryJoinList) {
//                        schools.add(categoryJoin.getSchool());
////                        Log.d(TAG, "Pulled " + categoryJoin.getSchool().getName() + " and fb posts: " + categoryJoin.getSchool().getFbPosts());
//                    }
//                    long getSchoolsLoopEndTime = System.currentTimeMillis();
//                    Log.v(TAG, "Get schools loop took: " + (getSchoolsLoopEndTime - getSchoolsLoopStartTime) + " ms");
//                    mSchools.clear();
//                    mSchools.addAll(schools);
//                    mSchoolListAdapter.notifyDataSetChanged();
//                    llProgress.setVisibility(View.GONE);
//                    final long pinOneStartTime = System.currentTimeMillis();
////                    ParseObject.pinAllInBackground(ParseLocalPrefs.CurrentSessionKey, schools);
//                    final long unPinStartTime = System.currentTimeMillis();
//                    ParseObject.unpinAllInBackground(ParseLocalPrefs.CurrentCategorySchoolsKey, new DeleteCallback() {
//                        @Override
//                        public void done(ParseException e) {
//                            if (e==null){
//                                ParseObject.pinAllInBackground(ParseLocalPrefs.CurrentCategorySchoolsKey, mSchools, new SaveCallback() {
//                                    @Override
//                                    public void done(ParseException e) {
//                                        ParseLocalPrefs.setSavedCategorySchools(getContext().getApplicationContext(), true);
//                                        long pinEndTime = System.currentTimeMillis();
//                                        Log.v(TAG, "Pinning ops: " + (pinEndTime - pinOneStartTime) + " ms");
//                                    }
//                                });
//                            }else{
//                                e.printStackTrace();
//                            }
//                        }
//                    });
////                    ParseObject.pinAllInBackground(ParseLocalPrefs.CurrentCategorySchoolsKey, mSchools);
//                }
//            }
//        });

    }

}
