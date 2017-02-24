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
import android.widget.Toast;

import com.kurtin.kurtin.R;
import com.kurtin.kurtin.adapters.CategoriesAdapter;
import com.kurtin.kurtin.helpers.ItemClickSupport;
import com.kurtin.kurtin.listeners.KurtinNavListener;
import com.kurtin.kurtin.models.BaseAd;
import com.kurtin.kurtin.models.Category;
import com.kurtin.kurtin.persistence.ParseLocal;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import static com.kurtin.kurtin.fragments.CategoriesFragment.TileType.AD;
import static com.kurtin.kurtin.fragments.CategoriesFragment.TileType.CATEGORY;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoriesFragment extends Fragment {

    public static String TAG = "CategoriesFragment";

    RecyclerView rvCategories;
    GridLayoutManager.SpanSizeLookup mSpanSizeLookup;
    CategoriesAdapter mCategoriesAdapter;
    List<ParseObject> mCategoryTiles;
    KurtinNavListener mKurtinNavListener;

    public enum TileType{
        AD, CATEGORY
    }


    public CategoriesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        bindUiElements(view);
        getCategories(view);
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
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mKurtinNavListener = null;
    }

    private void bindUiElements(View view){
        rvCategories = (RecyclerView) view.findViewById(R.id.rvCategories);
        mCategoryTiles = new ArrayList<>();
        mCategoriesAdapter = new CategoriesAdapter(mCategoryTiles);
        initSpanLookup();
        int numberOfColumns = 6;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), numberOfColumns);
        gridLayoutManager.setSpanSizeLookup(mSpanSizeLookup);
        rvCategories.setLayoutManager(gridLayoutManager);
        rvCategories.setAdapter(mCategoriesAdapter);
        ItemClickSupport.addTo(rvCategories).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                ParseObject parseObject = mCategoryTiles.get(position);
                if (parseObject instanceof Category){
                    //Get schools in category
                    String categoryTypeObjId = parseObject.getObjectId();
                    mKurtinNavListener.onSchoolListFragmentRequested(categoryTypeObjId);
                }else{
                    Toast.makeText(getContext(), "Content coming soon", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getCategories(final View view){
        ParseQuery<Category> categoryQuery = Category.getQuery();
        categoryQuery.findInBackground(new FindCallback<Category>() {
            public void done(List<Category> categoryTypes, ParseException e) {
                if (e == null) {
                    mCategoryTiles.clear();
                    mCategoryTiles.addAll(createDataSet(categoryTypes));
                    mCategoriesAdapter.notifyDataSetChanged();
                    ParseObject.pinAllInBackground(ParseLocal.CurrentSessionKey, categoryTypes);
                    Log.d(TAG, "CategoryTypes returned successfully");
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void initSpanLookup(){
        mSpanSizeLookup = new GridLayoutManager.SpanSizeLookup(){
            @Override
            public int getSpanSize(int position){
                return mCategoriesAdapter.getSpanSize(position);
            }
        };
    }

    private List<ParseObject> createDataSet(List<Category> categoryTypes){
        List<ParseObject> list = new ArrayList<>();
        BaseAd ad = new BaseAd();
        ad.setTitle("Drop Knowledge");
        ad.setCaption("Students are taking control of the college admissions process");
        list.add(ad);
        list.addAll(categoryTypes);
        return list;
    }

    private TileType getTileType(int position){
        ParseObject parseObject = mCategoryTiles.get(position);
        if(parseObject instanceof BaseAd){
            return AD;
        }else {
            return CATEGORY;
        }
    }

    private boolean isCategory(int position){
        return getTileType(position) == CATEGORY;
    }

}
