package com.kurtin.kurtin.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.kurtin.kurtin.R;
import com.kurtin.kurtin.models.School;

import java.util.List;

/**
 * Created by cvar on 2/23/17.
 */

public class SchoolListAdapter extends RecyclerView.Adapter<SchoolListAdapter.ViewHolder> {

    private List<School> mSchools;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private SimpleDraweeView sdvImage;
        private TextView tvName;
        private TextView tvLocation;

        private ViewHolder(View itemView){
            super(itemView);

            sdvImage = (SimpleDraweeView) itemView.findViewById(R.id.sdvImage);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvLocation = (TextView) itemView.findViewById(R.id.tvLocation);

            GenericDraweeHierarchyBuilder builder =
                    new GenericDraweeHierarchyBuilder(itemView.getResources());
            GenericDraweeHierarchy hierarchy = builder
                    .setFadeDuration(300)
                    .setPlaceholderImage(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_profile_drawer_96dp))
                    .build();
            sdvImage.setHierarchy(hierarchy);
        }
    }

    public SchoolListAdapter(List<School> schools){
        mSchools = schools;
    }

    @Override
    public SchoolListAdapter.ViewHolder onCreateViewHolder(ViewGroup parentViewGroup, int viewType) {
        Context context = parentViewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View schoolView = inflater.inflate(R.layout.item_school_list, parentViewGroup, false);

        return new ViewHolder(schoolView);
    }

    @Override
    public void onBindViewHolder(SchoolListAdapter.ViewHolder viewHolder, int position) {
        School school = mSchools.get(position);

        String location = school.getCity() + ", " + school.getState();
        viewHolder.sdvImage.setImageURI(school.getStudentImage().getUrl());
        viewHolder.tvName.setText(school.getName());
        viewHolder.tvLocation.setText(location);
    }

    @Override
    public int getItemCount() {
        return mSchools.size();
    }

}
