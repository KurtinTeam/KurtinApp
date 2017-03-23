package com.kurtin.kurtin.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kurtin.kurtin.R;
import com.kurtin.kurtin.activities.MainActivity;
import com.kurtin.kurtin.adapters.SchoolDetailPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class SchoolDetailTabFragment extends Fragment {

    public static final String TAG = "SchoolDetailTabFragment";
    public static final String TITLE = "School";

    private ViewPager vpViewPager;
    private TabLayout tlTabs;


    public SchoolDetailTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "Creating view");
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_school_detail_tab, container, false);
        bindUiElements(viewGroup);
        return viewGroup;
    }

    private void bindUiElements(ViewGroup viewGroup){
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        vpViewPager = (ViewPager) viewGroup.findViewById(R.id.vpViewPager);
        vpViewPager.setAdapter(new SchoolDetailPagerAdapter(getChildFragmentManager(),
                getContext()));

        // Give the TabLayout the ViewPager
        tlTabs = (TabLayout) viewGroup.findViewById(R.id.tlTabs);
        tlTabs.setupWithViewPager(vpViewPager);
    }

}
