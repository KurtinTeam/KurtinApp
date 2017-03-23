package com.kurtin.kurtin.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.kurtin.kurtin.fragments.SchoolDetailFragment;
import com.kurtin.kurtin.fragments.SchoolInfoFragment;

/**
 * Created by cvar on 3/22/17.
 */

public class SchoolDetailPagerAdapter extends FragmentPagerAdapter {
    public static final String TAG = "SchoolDetailPagerAdptr";

    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Media", "Info"};
//    private Context context;

    public SchoolDetailPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        Log.d(TAG, "Creating new Adapter");
//        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new SchoolDetailFragment();
            case 1:
                return new SchoolInfoFragment();
            default:
                return new SchoolInfoFragment();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
