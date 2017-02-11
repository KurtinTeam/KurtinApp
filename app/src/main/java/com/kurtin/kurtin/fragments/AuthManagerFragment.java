package com.kurtin.kurtin.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kurtin.kurtin.R;
import com.kurtin.kurtin.adapters.AuthManagerAdapter;
import com.kurtin.kurtin.helpers.ItemClickSupport;
import com.kurtin.kurtin.listeners.AuthenticationListener;
import com.kurtin.kurtin.models.AuthPlatform;
import com.kurtin.kurtin.models.KurtinUser;

import java.util.List;

import static com.kurtin.kurtin.R.id.rvPlatforms;

/**
 * A simple {@link Fragment} subclass.
 */
public class AuthManagerFragment extends Fragment {

    public static final String TAG = "AuthManagerFragment";

    private AuthenticationListener mAuthenticationListener;

    private TextView tvLogOut;
    private RecyclerView rvPlatforms;
    private AuthManagerAdapter mAuthManagerAdapter;
    private List<AuthPlatform> mPlatforms;

    public AuthManagerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_authentication_manager, container, false);
        bindUiElements(view);
        setClickListeners();
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginFragment.LoginFragmentListener) {
            mAuthenticationListener = (AuthenticationListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement LoginFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAuthenticationListener = null;
    }

    @Override
    public void onStart(){
        super.onStart();
        mAuthManagerAdapter.setAuthenticationListener(mAuthenticationListener);
    }

    @Override
    public void onStop(){
        super.onStop();
        mAuthManagerAdapter.setAuthenticationListener(null);
    }

    private void bindUiElements(View view){
        tvLogOut = (TextView) view.findViewById(R.id.tvLogOut);
        rvPlatforms = (RecyclerView) view.findViewById(R.id.rvPlatforms);
        mPlatforms = KurtinUser.getUserPlatforms(getContext());
        mAuthManagerAdapter = new AuthManagerAdapter(getContext(), mPlatforms);
        rvPlatforms.setAdapter(mAuthManagerAdapter);
        rvPlatforms.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setClickListeners(){
        ItemClickSupport.addTo(rvPlatforms).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                AuthManagerAdapter adapter = (AuthManagerAdapter) recyclerView.getAdapter();
                adapter.expandView(position);
            }
        });

        tvLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    mAuthenticationListener.onKurtinLogOutRequested();
                }catch (Exception e){
                    Log.e(TAG, "tvLogOut.OnClick failed. Must implement AuthenticationListener");
                    e.printStackTrace();
                }
            }
        });
    }

}
