package com.kurtin.kurtin.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kurtin.kurtin.R;
import com.kurtin.kurtin.helpers.JsonHelper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.R.attr.name;
import static android.R.attr.value;

/**
 * Created by cvar on 3/22/17.
 */

public class SchoolInfoAdapter extends RecyclerView.Adapter<SchoolInfoAdapter.InfoVH> {

    private List<String> mInfoList;

    protected static class InfoVH extends RecyclerView.ViewHolder{

        private TextView tvInfo;

        private InfoVH(View view){
            super(view);
            tvInfo = (TextView) view.findViewById(R.id.tvInfo);
        }

    }

    public SchoolInfoAdapter(List<String> infoList){
        mInfoList = infoList;
    }

    @Override
    public InfoVH onCreateViewHolder(ViewGroup parent, int viewType){
        return new InfoVH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_school_info, parent, false));
    }

    @Override
    public void onBindViewHolder(InfoVH infoVH, int position){
        infoVH.tvInfo.setText(mInfoList.get(position));
    }

    @Override
    public int getItemCount() {
        return mInfoList.size();
    }
}
