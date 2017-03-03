package com.kurtin.kurtin.fragments;


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
import com.kurtin.kurtin.models.Category;
import com.kurtin.kurtin.models.CategoryJoin;
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

    public SchoolListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_school_list, container, false);
        bindUiElements(view);
        getCategory();
        return view;
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
        mSchoolListAdapter = new SchoolListAdapter(mSchools);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvSchools.setLayoutManager(layoutManager);
        rvSchools.setAdapter(mSchoolListAdapter);
        llProgress = (LinearLayout) view.findViewById(R.id.llProgress);
    }

    private void getCategory(){
        llProgress.setVisibility(View.VISIBLE);
        Bundle args = getArguments();
        String categoryObjId = args.getString(CATEGORY_TYPE_OBJ_ID);
        ParseQuery<Category> categoryTypeQuery = Category.getQuery();
        categoryTypeQuery.fromLocalDatastore();
        categoryTypeQuery.getInBackground(categoryObjId, new GetCallback<Category>() {
            @Override
            public void done(Category category, ParseException e) {
                loadSchools(category);
            }
        });
    }

    private void loadSchools(Category category){
        ParseRelation<CategoryJoin> schoolsRelation = category.getCategoryItems();
        ParseQuery<CategoryJoin> categoryJoinQuery = schoolsRelation.getQuery();
        categoryJoinQuery.include(CategoryJoin.SCHOOL_KEY);
        categoryJoinQuery.findInBackground(new FindCallback<CategoryJoin>() {
            @Override
            public void done(List<CategoryJoin> categoryJoinList, ParseException e) {
                // Remove this if statement once all categories have schools attached to them
                if(categoryJoinList.isEmpty()){
                    Log.v(TAG, "categoryJoinList is empty " + categoryJoinList.toString());
                    ParseQuery<School> schoolQuery = School.getQuery();
                    schoolQuery.findInBackground(new FindCallback<School>() {
                        @Override
                        public void done(List<School> schools, ParseException e) {
                            mSchools.clear();
                            mSchools.addAll(schools);
                            mSchoolListAdapter.notifyDataSetChanged();
                            llProgress.setVisibility(View.GONE);
                            ParseObject.pinAllInBackground(ParseLocal.CurrentSessionKey, schools);

                            Toast.makeText(getContext(), "Showing generic School list.\nCategory coming soon.", Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    Log.v(TAG, "categoryJoinList is not empty " + categoryJoinList.toString());
                    List<School> schools = new ArrayList<>();
                    for (CategoryJoin categoryJoin : categoryJoinList) {
                        schools.add(categoryJoin.getSchool());
                    }
                    mSchools.clear();
                    mSchools.addAll(schools);
                    mSchoolListAdapter.notifyDataSetChanged();
                    llProgress.setVisibility(View.GONE);
                    ParseObject.pinAllInBackground(ParseLocal.CurrentSessionKey, schools);
                }
            }
        });

    }

}
