package com.kurtin.kurtin.adapters;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.kurtin.kurtin.R;
import com.kurtin.kurtin.helpers.ImageHelper;
import com.kurtin.kurtin.helpers.ScreenUtils;
import com.kurtin.kurtin.models.BaseAd;
import com.kurtin.kurtin.models.Category;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by cvar on 2/22/17.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String TAG = "CategoriesAdapter";

    private static final int SMALL = 0, MEDIUM = 1, LARGE = 2, AD = 3;


    // The items to display in your RecyclerView
    private List<ParseObject> mCategoryTiles;
    private int mViewType;
    private int mScreenWidth;
    private Context mContext;

    private static class CategoryVh extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        private SimpleDraweeView sdvImage;
        private TextView tvTitle;
        private TextView tvCaption;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        private CategoryVh(View view, int viewType) {
            super(view);
            Point screenSize = calcViewSize(view.getContext(), viewType);
//            view.setLayoutParams(new LinearLayout.LayoutParams(screenSize.x, screenSize.y));
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, screenSize.y));

            sdvImage = (SimpleDraweeView) view.findViewById(R.id.sdvImage);
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            tvCaption = (TextView) view.findViewById(R.id.tvCaption);

            GenericDraweeHierarchyBuilder builder =
                    new GenericDraweeHierarchyBuilder(view.getResources());
            GenericDraweeHierarchy hierarchy = builder
                    .setFadeDuration(300)
                    .setPlaceholderImage(R.drawable.ic_profile_drawer_96dp)
                    .build();
            sdvImage.setHierarchy(hierarchy);
        }

        private Point calcViewSize(Context context, int viewType){
            int screenWidth = ScreenUtils.getWidth(context, ScreenUtils.USABLE_SCREEN);
            Point viewSize = new Point();
            switch (viewType){
                case SMALL:
                    viewSize.x = screenWidth/3;
                    viewSize.y = screenWidth/3;
                    break;
                case MEDIUM:
                    viewSize.x = screenWidth/2;
                    viewSize.y = screenWidth/3;
                    break;
                default:
                    viewSize.x = screenWidth;
                    viewSize.y = screenWidth/3;
            }

            return viewSize;
        }
    }

    private static class AdVh extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        private SimpleDraweeView sdvImage;
        private TextView tvTitle;
        private TextView tvCaption;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        private AdVh(View view) {
            super(view);
            Point screenSize = calcViewSize(view.getContext());
//            view.setLayoutParams(new LinearLayout.LayoutParams(screenSize.x, screenSize.y));
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, screenSize.y));

            sdvImage = (SimpleDraweeView) view.findViewById(R.id.sdvImage);
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            tvCaption = (TextView) view.findViewById(R.id.tvCaption);

            GenericDraweeHierarchyBuilder builder =
                    new GenericDraweeHierarchyBuilder(view.getResources());
            GenericDraweeHierarchy hierarchy = builder
                    .setFadeDuration(300)
                    .setPlaceholderImage(R.drawable.ic_profile_drawer_96dp)
                    .build();
            sdvImage.setHierarchy(hierarchy);
        }

        private Point calcViewSize(Context context){
            int screenWidth = ScreenUtils.getWidth(context, ScreenUtils.USABLE_SCREEN);
            return new Point(screenWidth, screenWidth/3);
        }
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public CategoriesAdapter(List<ParseObject> categoryTiles, Context context) {
        mCategoryTiles = categoryTiles;
        mViewType = SMALL;
        mScreenWidth = ScreenUtils.getWidth(context, ScreenUtils.USABLE_SCREEN);
    }

    public void setViewType(int viewType) {
        mViewType = viewType;
    }

    public int getSpanSize(int position){
        switch (getItemViewType(position)){
            case SMALL:
                return 2;
            case MEDIUM:
                return 3;
            default:
                return 6;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mCategoryTiles.size();
    }

    @Override
    public int getItemViewType(int position) {
        ParseObject tile = mCategoryTiles.get(position);

        if (tile instanceof BaseAd){
            return AD;
        }else{
            return mViewType;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_cat, viewGroup, false);

        switch (viewType){
            case AD:
                viewHolder = new AdVh(view);
                break;
            default:
                viewHolder = new CategoryVh(view, viewType);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()){
            case AD:
                configureAdVh(viewHolder, position);
                break;
            default:
                configureCategoryVh(viewHolder, position);
        }
    }

    private void configureAdVh(RecyclerView.ViewHolder viewHolder, int position){
        CategoriesAdapter.AdVh adVh = (CategoriesAdapter.AdVh) viewHolder;
        BaseAd ad = (BaseAd) mCategoryTiles.get(position);
        adVh.tvTitle.setText(ad.getTitle());
        adVh.tvCaption.setText(ad.getCaption());
        Context context = viewHolder.itemView.getContext();
        adVh.sdvImage.getHierarchy().setBackgroundImage(getColorAsDrawable(position, context));
    }

    private void configureCategoryVh(RecyclerView.ViewHolder viewHolder, int position){
        CategoriesAdapter.CategoryVh categoryVh = (CategoriesAdapter.CategoryVh) viewHolder;
        Category category= (Category) mCategoryTiles.get(position);
        categoryVh.tvTitle.setText(category.getTitle());
        categoryVh.tvCaption.setText(category.getCaption());
        categoryVh.sdvImage.setImageURI(category.getPictureUrl());

        int tileDim = mScreenWidth/3;
        categoryVh.sdvImage.setController(ImageHelper.getResizeController(
                categoryVh.sdvImage, category.getPictureUrl(), tileDim, tileDim));

//        Context context = viewHolder.itemView.getContext();
//        categoryVh.sdvImage.getHierarchy().setBackgroundImage(getColorAsDrawable(position, context));
    }

    private Drawable getColorAsDrawable(int position, Context context){
        if (position == 0){
            return ContextCompat.getDrawable(context, R.color.blue_gray);
        }else if((position % 2) == 0){
            return ContextCompat.getDrawable(context, R.color.grape);
        }else{
            return ContextCompat.getDrawable(context, R.color.lemon_glacier);
        }
    }

}
