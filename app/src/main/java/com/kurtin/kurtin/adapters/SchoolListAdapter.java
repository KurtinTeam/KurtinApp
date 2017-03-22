package com.kurtin.kurtin.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.kurtin.kurtin.R;
import com.kurtin.kurtin.helpers.ImageHelper;
import com.kurtin.kurtin.helpers.ScreenUtils;
import com.kurtin.kurtin.models.School;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.List;

import static android.R.attr.vmSafeMode;
import static android.R.attr.width;

/**
 * Created by cvar on 2/23/17.
 */

public class SchoolListAdapter extends RecyclerView.Adapter<SchoolListAdapter.ViewHolder> {

    private static final String TAG = "SchoolListAdapter";

    private List<School> mSchools;
    private int mScreenWidth;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private SimpleDraweeView sdvImage;
        private TextView tvName;
        private TextView tvLocation;
        private int mHeight;

        private ViewHolder(View itemView){
            super(itemView);

            sdvImage = (SimpleDraweeView) itemView.findViewById(R.id.sdvImage);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvLocation = (TextView) itemView.findViewById(R.id.tvLocation);
            mHeight = itemView.getLayoutParams().height;

//            GenericDraweeHierarchyBuilder builder =
//                    new GenericDraweeHierarchyBuilder(itemView.getResources());
//            GenericDraweeHierarchy hierarchy = builder
//                    .setFadeDuration(300)
//                    .setPlaceholderImage(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_profile_drawer_96dp))
//                    .build();
//            sdvImage.setHierarchy(hierarchy);
            sdvImage.setHierarchy(ImageHelper.getBasicHierarchy(
                    itemView.getResources(),
                    ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_profile_drawer_96dp)));
        }
    }

    public SchoolListAdapter(List<School> schools, Context context){
        mSchools = schools;
        mScreenWidth = ScreenUtils.getWidth(context, ScreenUtils.ScreenType.USABLE_SCREEN);
    }

    @Override
    public SchoolListAdapter.ViewHolder onCreateViewHolder(ViewGroup parentViewGroup, int viewType) {
        Context context = parentViewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View schoolView = inflater.inflate(R.layout.item_school_list, parentViewGroup, false);

        return new ViewHolder(schoolView);
    }

    @Override
    public void onBindViewHolder(final SchoolListAdapter.ViewHolder viewHolder, int position) {
        School school = mSchools.get(position);
        String location = school.getCity() + ", " + school.getState();
        int width = mScreenWidth;
        int height = viewHolder.mHeight;
        viewHolder.sdvImage.setController(
                ImageHelper.getResizeController(
                        viewHolder.sdvImage, school.getStudentImage().getUrl(), width, height));
//        viewHolder.sdvImage.setImageURI(school.getStudentImage().getUrl());
        viewHolder.tvName.setText(school.getName());
        viewHolder.tvLocation.setText(location);

//        final Long fetchStartTime = System.currentTimeMillis();
//        school.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
//            @Override
//            public void done(ParseObject object, ParseException e) {
//                Long fetchEndTime = System.currentTimeMillis();
//                Log.d(TAG, "School fetch took: " + (fetchEndTime - fetchStartTime) + " ms");
//                School fetchedSchool = (School) object;
//                String location = fetchedSchool.getCity() + ", " + fetchedSchool.getState();
//                int width = mScreenWidth;
//                int height = viewHolder.mHeight;
//                viewHolder.sdvImage.setController(
//                        ImageHelper.getResizeController(
//                                viewHolder.sdvImage, fetchedSchool.getStudentImage().getUrl(), width, height));
////        viewHolder.sdvImage.setImageURI(school.getStudentImage().getUrl());
//                viewHolder.tvName.setText(fetchedSchool.getName());
//                viewHolder.tvLocation.setText(location);
//            }
//        });


    }

    @Override
    public int getItemCount() {
        return mSchools.size();
    }

}
