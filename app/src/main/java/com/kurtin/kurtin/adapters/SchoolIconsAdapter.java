package com.kurtin.kurtin.adapters;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kurtin.kurtin.R;
import com.kurtin.kurtin.helpers.ImageHelper;
import com.kurtin.kurtin.helpers.ScreenUtils;
import com.kurtin.kurtin.models.School;

import java.util.List;

/**
 * Created by cvar on 3/3/17.
 */

public class SchoolIconsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<School> mSchools;

    private static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvSchoolName;
        SimpleDraweeView sdvIcon;

        private ViewHolder(View view){
            super(view);
            sdvIcon = (SimpleDraweeView) view.findViewById(R.id.sdvIcon);
            sdvIcon.setHierarchy(ImageHelper.getCircleHierarchy(
                    view.getResources(),
                    ContextCompat.getColor(view.getContext(), R.color.kurtin_primary),
                    ScreenUtils.dpToPixels(view.getResources(), 1f)));
            tvSchoolName = (TextView) view.findViewById(R.id.tvSchoolName);
        }
    }

    SchoolIconsAdapter(List<School> schools){
        mSchools = schools;
    }

    @Override
    public int getItemCount() {
        return mSchools.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_similar_school, viewGroup, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        School school = mSchools.get(position);
        SchoolIconsAdapter.ViewHolder vh = (ViewHolder) viewHolder;
        vh.sdvIcon.setImageURI(school.getLogoImage().getUrl());
        vh.tvSchoolName.setText(school.getName());
    }

}
