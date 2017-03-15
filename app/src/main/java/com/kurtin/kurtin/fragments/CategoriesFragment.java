package com.kurtin.kurtin.fragments;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.facebook.drawee.view.SimpleDraweeView;
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

    public static final String TAG = "CategoriesFragment";
    public static final String TITLE = "Categories";

    RecyclerView rvCategories;
    GridLayoutManager.SpanSizeLookup mSpanSizeLookup;
    CategoriesAdapter mCategoriesAdapter;
    List<ParseObject> mCategoryTiles;
    KurtinNavListener mKurtinNavListener;
    LinearLayout llProgress;

    boolean mCategoriesReceived;
    boolean mAdsReceived;

    //Base ad view
    private TextView tvTitle;
    private TextView tvCaption;
    private SimpleDraweeView sdvBanner;
    private List<View> mAdViews;
    private List<BaseAd> mAds;
    private ViewFlipper vfCarousel;
    private final int CAROUSEL_INTERVAL = 3000;


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
        getContent(view);
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
                    String categoryObjId = parseObject.getObjectId();
                    mKurtinNavListener.onSchoolListFragmentRequested(categoryObjId);
                }else{
                    Toast.makeText(getContext(), "Content coming soon", Toast.LENGTH_SHORT).show();
                }
            }
        });
        llProgress = (LinearLayout) view.findViewById(R.id.llProgress);

        //Base ad view
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvCaption = (TextView) view.findViewById(R.id.tvCaption);
        sdvBanner = (SimpleDraweeView) view.findViewById(R.id.sdvImage);
        vfCarousel = (ViewFlipper) view.findViewById(R.id.vfCarousel);
    }

    private void getContent(final View view){
        llProgress.setVisibility(View.VISIBLE);

        // Get categories
        mCategoriesReceived = false;
        ParseQuery<Category> categoryQuery = Category.getQuery();
        categoryQuery.orderByAscending(Category.DISPLAY_ORDER_KEY);
        categoryQuery.findInBackground(new FindCallback<Category>() {
            public void done(List<Category> categories, ParseException e) {
                if (e == null) {
                    mCategoryTiles.clear();
                    mCategoryTiles.addAll(categories);
                    mCategoriesAdapter.notifyDataSetChanged();
                    ParseObject.pinAllInBackground(ParseLocal.CurrentSessionKey, categories);
                    mCategoriesReceived = true;
                    dismissProgressBar();
                    Log.d(TAG, "CategoryTypes returned successfully");
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });

        // Get banner data
        mAdsReceived = false;
        ParseQuery<BaseAd> baseAdParseQuery = BaseAd.getQuery();
        baseAdParseQuery.findInBackground(new FindCallback<BaseAd>() {
            @Override
            public void done(List<BaseAd> ads, ParseException e) {
//                tvTitle.setText(ad.getTitle());
//                tvCaption.setText(ad.getCaption());
//                sdvBanner.setImageURI(ad.getMediaUrl());
                mAds = ads;
                initCarousel();
                mAdsReceived = true;
                dismissProgressBar();
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

    private void dismissProgressBar(){
        if(mCategoriesReceived && mAdsReceived){
            llProgress.setVisibility(View.GONE);
        }
    }

    private void initCarousel(){
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());

        vfCarousel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = vfCarousel.getDisplayedChild();
                BaseAd ad = mAds.get(index);

                //Prepare custom tabs
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.kurtin_primary));
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(getContext(), Uri.parse(ad.getTargetUrl()));
//                Toast.makeText(getContext(), ad.getSource(), Toast.LENGTH_SHORT).show();
            }
        });

        for(BaseAd ad: mAds){
            View adView = layoutInflater.inflate(R.layout.item_banner_ad, null, false);
            ((TextView) adView.findViewById(R.id.tvTitle)).setText(ad.getTitle());
            ((TextView) adView.findViewById(R.id.tvCaption)).setText(ad.getCaption());
            ((SimpleDraweeView) adView.findViewById(R.id.sdvBanner)).setImageURI(ad.getMediaUrl());
            vfCarousel.addView(adView);
        }

        vfCarousel.setFlipInterval(CAROUSEL_INTERVAL);
        vfCarousel.startFlipping();
    }

}
