package com.kurtin.kurtin.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kurtin.kurtin.R;
import com.kurtin.kurtin.models.Media;
import com.kurtin.kurtin.models.School;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MediaDetailFragment extends Fragment {

    // Header
    private SimpleDraweeView sdvLogo;
    private TextView tvSchoolName;
    // Main content
    private SimpleDraweeView sdvMediaImage;
    // Content info
    private SimpleDraweeView sdvPlatformIcon;
    private TextView tvPlatformName;
    private TextView tvMediaText;

    public MediaDetailFragment() {
        // Required empty public constructor
    }

    public static MediaDetailFragment newInstance(List<Media> mediaList, School school) {

        Bundle args = new Bundle();
        MediaDetailFragment fragment = new MediaDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_media_detail, container, false);

        bindUiElements(view);

        return  view;
    }

    private void bindUiElements(View view){
        sdvLogo = (SimpleDraweeView) view.findViewById(R.id.sdvLogo);
        tvSchoolName = (TextView) view.findViewById(R.id.tvSchoolName);
        sdvMediaImage = (SimpleDraweeView) view.findViewById(R.id.sdvMediaImage);
        sdvPlatformIcon = (SimpleDraweeView) view.findViewById(R.id.sdvPlatformIcon);
        tvPlatformName = (TextView) view.findViewById(R.id.tvPlatformName);
        tvMediaText = (TextView) view.findViewById(R.id.tvMediaText);
    }

}
