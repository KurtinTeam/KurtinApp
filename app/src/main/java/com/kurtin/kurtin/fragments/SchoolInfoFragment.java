package com.kurtin.kurtin.fragments;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.facebook.drawee.view.SimpleDraweeView;
import com.kurtin.kurtin.R;
import com.kurtin.kurtin.adapters.SchoolInfoAdapter;
import com.kurtin.kurtin.helpers.ImageHelper;
import com.kurtin.kurtin.helpers.JsonHelper;
import com.kurtin.kurtin.models.School;
import com.kurtin.kurtin.persistence.ParseLocalPrefs;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.R.attr.key;

/**
 * A simple {@link Fragment} subclass.
 */
public class SchoolInfoFragment extends Fragment {

    public static final String TAG = "SchoolInfoFragment";

    private School mSchool;
    private List<String> mInfoList;
    private SimpleDraweeView sdvLogo;
    private TextView tvHeader;
    private RecyclerView rvSchoolInfo;
    private SchoolInfoAdapter mSchoolInfoAdapter;
    private LinearLayout llProgress;


    public SchoolInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "Creating view");
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_school_info, container, false);

        bindUiElements(viewGroup);
        getSchool();

        return viewGroup;
    }

    private void bindUiElements(ViewGroup viewGroup){
        sdvLogo = (SimpleDraweeView) viewGroup.findViewById(R.id.sdvLogo);
        sdvLogo.setHierarchy(ImageHelper.getCircleHierarchy(getResources()));
        tvHeader = (TextView) viewGroup.findViewById(R.id.tvHeader);
        rvSchoolInfo = (RecyclerView) viewGroup.findViewById(R.id.rvSchoolInfo);
        mInfoList = new ArrayList<>();
        mSchoolInfoAdapter = new SchoolInfoAdapter(mInfoList);
        rvSchoolInfo.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvSchoolInfo.setAdapter(mSchoolInfoAdapter);
        llProgress = (LinearLayout) viewGroup.findViewById(R.id.llProgress);
    }

    private void getSchool(){
        llProgress.setVisibility(View.VISIBLE);
        String schoolId = ParseLocalPrefs.getSelectedSchoolId(getContext());
        ParseQuery<School> schoolQuery = School.getQuery();
        if (ParseLocalPrefs.categorySchoolsAreSaved(getContext())){
            schoolQuery.fromPin(ParseLocalPrefs.CATEGORY_SCHOOLS_PIN);
        }
        schoolQuery.getInBackground(schoolId, new GetCallback<School>() {
            @Override
            public void done(School school, ParseException e) {
                if (e==null) {
                    mSchool = school;
                    sdvLogo.setImageURI(mSchool.getLogoImage().getUrl());
                    getInfo();
                }else{
                    e.printStackTrace();
                }
            }
        });
    }

    private void getInfo(){
        List<String> infoList = new ArrayList<>();
        JSONObject infoObject = mSchool.getStats();
        String statName;
        String statValue;
        String decoration = " - ";
        JSONObject statObject;
        Iterator<?> keys = infoObject.keys();
        while(keys.hasNext()){
            String key = (String)keys.next();
            statObject = JsonHelper.getJsonObject(infoObject, key);
            statName = JsonHelper.getString(statObject, "title");
            statValue = JsonHelper.getString(statObject, "value");
            infoList.add(statName + decoration + statValue);
        }

        mInfoList.clear();
        mInfoList.addAll(infoList);
        mSchoolInfoAdapter.notifyDataSetChanged();
        llProgress.setVisibility(View.GONE);
        Log.d(TAG, "Info List size:" + infoList.size());
        Log.d(TAG, "Dict keys:" + keys.toString());
    }

}
