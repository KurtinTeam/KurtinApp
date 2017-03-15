package com.kurtin.kurtin.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.drawee.controller.ControllerViewportVisibilityListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.kurtin.kurtin.R;
import com.kurtin.kurtin.helpers.ImageHelper;
import com.kurtin.kurtin.helpers.ItemClickSupport;
import com.kurtin.kurtin.helpers.ScreenUtils;
import com.kurtin.kurtin.models.BaseAd;
import com.kurtin.kurtin.models.FacebookMedia;
import com.kurtin.kurtin.models.Media;
import com.kurtin.kurtin.models.PhotoMedia;
import com.kurtin.kurtin.models.School;
import com.kurtin.kurtin.models.SimilarSchools;
import com.kurtin.kurtin.models.TwitterMedia;
import com.parse.ParseObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.configure;

/**
 * Created by cvar on 3/3/17.
 */

public class SchoolDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String TAG = "SchoolDetailAdapter";

    public static final int SMALL = 0, MEDIUM = 1, LARGE = 2, AD = 3;

    private static final Integer SIMILAR_SCHOOLS_HEIGHT_DP = 144;
    private static final Integer BANNER_HEIGHT_DP = 192;


    private List<Media> mMediaList;
    private int mScreenWidth;
    private int mNumGridColumns;


    private static class SimilarSchoolsVh extends RecyclerView.ViewHolder{

        RecyclerView rvSchoolIcons;

        private SimilarSchoolsVh(View view){
            super(view);
            rvSchoolIcons = (RecyclerView) view;
        }
    }

    private static class PhotoVh extends RecyclerView.ViewHolder{

        SimpleDraweeView sdvPhoto;

        private PhotoVh(View view){
            super(view);
            sdvPhoto = (SimpleDraweeView) view;
        }
    }

    private static class TileVh extends RecyclerView.ViewHolder{

        SimpleDraweeView sdvPhoto;

        private TileVh(View view){
            super(view);
            sdvPhoto = (SimpleDraweeView) view;
        }
    }

    public int getSpanSize(int position){
        switch (Media.MediaType.toMediaType(getItemViewType(position))){
            case PHOTO:
                return mNumGridColumns;
            case SIMILAR_SCHOOLS:
                return mNumGridColumns;
            default:
                return mNumGridColumns/3;
        }
    }

    public SchoolDetailAdapter(List<Media> mediaList, int numGridColumns, Context context){
        Log.v(TAG, "Inside SchoolDetailAdapter constructor");
        mMediaList = mediaList;
        mScreenWidth = ScreenUtils.getWidth(context, ScreenUtils.ScreenType.USABLE_SCREEN);
        mNumGridColumns = numGridColumns;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        Log.v(TAG, "Inside getItemCount. Count = " + mMediaList.size());
        return mMediaList.size();
    }

    //TODO: Use old getItemView and remove need for SMALL, MEDIUM, and LARGE viewTypes

    @Override
    public int getItemViewType(int position) {
        Log.v(TAG, "Inside getItemViewType");
        return mMediaList.get(position).getMediaType().getValue();
    }

//    @Override
//    public int getItemViewType(int position) {
//        Media media = mMediaList.get(position);
//
//        switch (media.getMediaType()){
//            case PHOTO:
//                return LARGE;
//            case SIMILAR_SCHOOLS:
//                return LARGE;
//            default:
//                return SMALL;
//        }
//    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Log.v(TAG, "Inside onCreateViewHolder");
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        // establish vertical padding size
        int verticalPadding = (int) ScreenUtils.dpToPixels(viewGroup.getResources(), 1);
        int padding = (int) ScreenUtils.dpToPixels(viewGroup.getResources(), 1);


        switch (Media.MediaType.toMediaType(viewType)){
            case PHOTO:
                LinearLayout.LayoutParams sdvLayoutParams =
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                (int) ScreenUtils.dpToPixels(viewGroup.getResources(), BANNER_HEIGHT_DP));
                SimpleDraweeView simpleDraweeView = new SimpleDraweeView(viewGroup.getContext(), ImageHelper.getBasicHierarchy(viewGroup.getResources()));
                simpleDraweeView.setLayoutParams(sdvLayoutParams);
                simpleDraweeView.setPadding(padding, verticalPadding, padding, verticalPadding);
                viewHolder = new PhotoVh(simpleDraweeView);
                return viewHolder;
            case SIMILAR_SCHOOLS:
                LinearLayout.LayoutParams rvLayoutParams =
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                (int) ScreenUtils.dpToPixels(viewGroup.getResources(), SIMILAR_SCHOOLS_HEIGHT_DP));
                RecyclerView recyclerView = new RecyclerView(viewGroup.getContext());
                recyclerView.setLayoutParams(rvLayoutParams);
                recyclerView.setBackgroundColor(ContextCompat.getColor(viewGroup.getContext(), R.color.divider_light));
                recyclerView.setPadding(padding, verticalPadding, padding, verticalPadding);
                viewHolder = new SimilarSchoolsVh(recyclerView);
                return viewHolder;
            case FACEBOOK:
                LinearLayout.LayoutParams sdvFbLayoutParams =
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mScreenWidth/3);
                SimpleDraweeView sdvFbPostTile =
                        new SimpleDraweeView(viewGroup.getContext(), ImageHelper.getBasicHierarchy(viewGroup.getResources()));
                sdvFbPostTile.setLayoutParams(sdvFbLayoutParams);
                sdvFbPostTile.setPadding(padding, verticalPadding, padding, verticalPadding);
                viewHolder = new TileVh(sdvFbPostTile);
                return viewHolder;
            case TWITTER:
                LinearLayout.LayoutParams sdvTwLayoutParams =
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mScreenWidth/3);
                SimpleDraweeView sdvTweetTile =
                        new SimpleDraweeView(viewGroup.getContext(), ImageHelper.getBasicHierarchy(viewGroup.getResources()));
                sdvTweetTile.setLayoutParams(sdvTwLayoutParams);
                sdvTweetTile.setPadding(padding, verticalPadding, padding, verticalPadding);
                viewHolder = new TileVh(sdvTweetTile);
                return viewHolder;
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Log.v(TAG, "Inside onBindViewHolder");
        switch (Media.MediaType.toMediaType(viewHolder.getItemViewType())){
            case PHOTO:
                configurePhoto(viewHolder, position);
                break;
            case SIMILAR_SCHOOLS:
                configureSimilarSchools(viewHolder, position);
                break;
            case FACEBOOK:
                configureFbTile(viewHolder, position);
                break;
            case TWITTER:
                configureTwTile(viewHolder, position);
                break;
            default:
        }
    }

    private void configurePhoto(RecyclerView.ViewHolder viewHolder, int position){
        Log.v(TAG, "Inside configurePhoto");
        PhotoVh photoVh = (PhotoVh) viewHolder;
        PhotoMedia photoMedia = (PhotoMedia) mMediaList.get(position);
        photoVh.sdvPhoto.setImageURI(photoMedia.getSourceUrl());
    }

    private void configureFbTile(RecyclerView.ViewHolder viewHolder, int position){
        Log.v(TAG, "Inside configurePhoto");
        TileVh tileVh = (TileVh) viewHolder;
        FacebookMedia facebookMedia = (FacebookMedia) mMediaList.get(position);
        tileVh.sdvPhoto.setImageURI(facebookMedia.getFullPictureUrl());
    }

    private void configureTwTile(RecyclerView.ViewHolder viewHolder, int position){
        Log.v(TAG, "Inside configurePhoto");
        TileVh tileVh = (TileVh) viewHolder;
        TwitterMedia twitterMedia = (TwitterMedia) mMediaList.get(position);
        tileVh.sdvPhoto.setImageURI(twitterMedia.getPictureUrl());
    }

    private void configureSimilarSchools(RecyclerView.ViewHolder viewHolder, int position){
        Log.v(TAG, "Inside configureSimilarSchools. ");
        SimilarSchoolsVh similarSchoolsVh = (SimilarSchoolsVh) viewHolder;
        final SimilarSchools similarSchools = (SimilarSchools) mMediaList.get(position);
        Log.v(TAG, "Inside configureSimilarSchools. NumSchools: " + similarSchools.getSchools().size());

        SchoolIconsAdapter schoolIconsAdapter = new SchoolIconsAdapter(similarSchools.getSchools());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(similarSchoolsVh.rvSchoolIcons.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        similarSchoolsVh.rvSchoolIcons.setLayoutManager(linearLayoutManager);
        similarSchoolsVh.rvSchoolIcons.setAdapter(schoolIconsAdapter);
        ItemClickSupport.addTo(similarSchoolsVh.rvSchoolIcons).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                School selectedSchool = similarSchools.getSchools().get(position);
                mMediaList = Media.changeSchool(mMediaList, selectedSchool);
                notifyDataSetChanged();
            }
        });
        schoolIconsAdapter.notifyDataSetChanged();
    }

}
