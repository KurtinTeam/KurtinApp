package com.kurtin.kurtin.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kurtin.kurtin.R;
import com.kurtin.kurtin.listeners.AuthenticationListener;
import com.kurtin.kurtin.models.AuthPlatform;
import com.kurtin.kurtin.models.KurtinUser;

import java.util.List;

import static android.view.View.GONE;


/**
 * Created by cvar on 2/8/17.
 */

public class AuthManagerAdapter extends
        RecyclerView.Adapter<AuthManagerAdapter.ViewHolder> {

    public static final String TAG = "AuthManagerAdapter";

    AuthenticationListener mAuthenticationListener;

    private static final Integer INDEX_NOT_SET = -1;
    private static final Integer ID_NO_DRAWABLE = 0;


    private List<AuthPlatform> mPlatforms;
    // Store the context for easy access
    private Context mContext;
    private Integer mExpandedIndex;
    private final Integer ICON_MARGIN;
    private final Integer ICON_DIM;



    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvPlatform;
        public TextView tvConnection;
        public TextView tvMessage;
        public TextView tvMessageSubject;
        public ImageView ivIcon;
        public ImageView ivExpand;
        public Button btnAuthAction;
        public RelativeLayout rlDetail;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            super(itemView);

            tvPlatform = (TextView) itemView.findViewById(R.id.tvPlatform);
            tvConnection = (TextView) itemView.findViewById(R.id.tvConnection);
            tvMessage = (TextView) itemView.findViewById(R.id.tvMessage);
            tvMessageSubject = (TextView) itemView.findViewById(R.id.tvMessageSubject);
            ivIcon = (ImageView) itemView.findViewById(R.id.ivIcon);
            ivExpand = (ImageView) itemView.findViewById(R.id.ivExpand);
            btnAuthAction = (Button) itemView.findViewById(R.id.btnAuthAction);
            rlDetail = (RelativeLayout) itemView.findViewById(R.id.rlDetail);
        }

        public void expandDetailView(){
            rlDetail.setVisibility(View.VISIBLE);
        }

        public void collapseDetailView(){
            rlDetail.setVisibility(GONE);
        }
    }

    public AuthManagerAdapter(Context context, List<AuthPlatform> platforms) {
        mPlatforms = platforms;
        mContext = context;
        mExpandedIndex = INDEX_NOT_SET;
        ICON_MARGIN = (int) getContext().getResources().getDimension(R.dimen.icon_margin);
        ICON_DIM = (int) getContext().getResources().getDimension(R.dimen.tv_drawable_icon);
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public AuthManagerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
//        View contactView = inflater.inflate(R.layout.item_authentication_manager, parent, false);
        View contactView = inflater.inflate(R.layout.item_auth_mgr, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        //Show button by default
        viewHolder.btnAuthAction.setVisibility(View.VISIBLE);
        // Get the data model based on position
        final AuthPlatform authPlatform = mPlatforms.get(position);
        // Platform data
        final AuthPlatform.PlatformType platformType = authPlatform.getPlatformType();
        viewHolder.tvPlatform.setText(platformType.toString());
        // Set Authentication Texts
        String connectionStatusText;
        String message;
        String authActionText;
        if(authPlatform.isAuthenticated()){
            //Platform Authenticated
            connectionStatusText = mContext.getString(R.string.authenticated);
            if(KurtinUser.getCurrentUser(getContext()).primaryPlatformEqualTo(platformType)){
                //Primary Authentication platform
                message = getContext().getString(R.string.status_logged_in);
                authActionText = getContext().getString(R.string.log_out);
                viewHolder.btnAuthAction.setVisibility(View.GONE);
            }else{
                //Secondary Authentication Platform
                message = mContext.getString(R.string.status_authenticated);
                authActionText = mContext.getString(R.string.disconnect);
            }
        }else{
            //Platform not authenticated
            connectionStatusText = mContext.getString(R.string.not_authenticated);
            message = mContext.getString(R.string.status_not_authenticated);
            authActionText = mContext.getString(R.string.connect);
        }
        //Set texts
        viewHolder.tvConnection.setText(connectionStatusText);
        viewHolder.tvMessage.setText(message);
        viewHolder.tvMessageSubject.setText(mPlatforms.get(position).getPlatformType().toString());
        viewHolder.btnAuthAction.setText(authActionText);
        //Set icon
        viewHolder.ivIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), getSocialIconId(platformType)));
        //Set expanded state
        if (mExpandedIndex.equals(position)){
            viewHolder.rlDetail.setVisibility(View.VISIBLE);
            viewHolder.ivExpand.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_expand_less_black_24dp));
        }else{
            viewHolder.rlDetail.setVisibility(GONE);
            viewHolder.ivExpand.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_expand_more_black_24dp));
        }
        //Set click listener
        viewHolder.btnAuthAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(KurtinUser.getCurrentUser(getContext()).primaryPlatformEqualTo(platformType)){
                    //TODO: handle primary login case.  Maybe a confirmation message
                }
                if(authPlatform.isAuthenticated()){
                    try{
                        mAuthenticationListener.logOut(platformType);
                    }catch (Exception e){
                        Log.e(TAG, "Error in btnAuthAction.setOnClickListener. Must implement mAuthenticationListener");
                    }
                }else{
                    try{
                        mAuthenticationListener.onAuthenticationRequested(platformType);
                    }catch (Exception e){
                        Log.e(TAG, "Error in btnAuthAction.setOnClickListener. Must implement mAuthenticationListener");
                    }
                }
                notifyItemChanged(viewHolder.getAdapterPosition());
            }
        });


//        //Show button by default
//        viewHolder.btnAuthAction.setVisibility(View.VISIBLE);
//        // Get the data model based on position
//        final AuthPlatform authPlatform = mPlatforms.get(position);
//        // Platform data
//        final AuthPlatform.PlatformType platformType = authPlatform.getPlatformType();
//        viewHolder.tvPlatform.setText(platformType.toString());
//        // Set Authentication Texts
//        String connectionStatusText;
//        String message;
//        String authActionText;
//        if(authPlatform.isAuthenticated()){
//            //Platform Authenticated
//            connectionStatusText = mContext.getString(R.string.authenticated);
//            if(KurtinUser.getCurrentUser(getContext()).primaryPlatformEqualTo(platformType)){
//                //Primary Authentication platform
//                message = getContext().getString(R.string.status_logged_in);
//                authActionText = getContext().getString(R.string.log_out);
//                viewHolder.btnAuthAction.setVisibility(View.GONE);
//            }else{
//                //Secondary Authentication Platform
//                message = mContext.getString(R.string.status_authenticated);
//                authActionText = mContext.getString(R.string.disconnect);
//            }
//        }else{
//            //Platform not authenticated
//            connectionStatusText = mContext.getString(R.string.not_authenticated);
//            message = mContext.getString(R.string.status_not_authenticated);
//            authActionText = mContext.getString(R.string.connect);
//        }
//        //Set texts
//        viewHolder.tvConnection.setText(connectionStatusText);
//        viewHolder.tvMessage.setText(message);
//        viewHolder.tvMessageSubject.setText(mPlatforms.get(position).getPlatformType().toString());
//        viewHolder.btnAuthAction.setText(authActionText);
//        //Set expanded state
//        Drawable socialIcon = getSocialDrawable(platformType);
//        if (mExpandedIndex.equals(position)){
//            viewHolder.rlDetail.setVisibility(View.VISIBLE);
//            Drawable expandLess = ContextCompat.getDrawable(getContext(), R.drawable.ic_expand_less_black_24dp);
//            expandLess.setBounds(0,0,ICON_DIM,ICON_DIM);
//            viewHolder.tvPlatform.setCompoundDrawables(
//                    socialIcon, null, expandLess, null);
//        }else{
//            viewHolder.rlDetail.setVisibility(GONE);
//            Drawable expandMore = ContextCompat.getDrawable(getContext(), R.drawable.ic_expand_more_black_24dp);
//            expandMore.setBounds(0,0,ICON_DIM,ICON_DIM);
//            viewHolder.tvPlatform.setCompoundDrawables(
//                    socialIcon, null, expandMore, null);
//        }
//        viewHolder.tvPlatform.setCompoundDrawablePadding(ICON_MARGIN);
//
//        viewHolder.btnAuthAction.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(KurtinUser.getCurrentUser(getContext()).primaryPlatformEqualTo(platformType)){
//                    //TODO: handle primary login case.  Maybe a confirmation message
//                }
//                if(authPlatform.isAuthenticated()){
//                    try{
//                        mAuthenticationListener.logOut(platformType);
//                    }catch (Exception e){
//                        Log.e(TAG, "Error in btnAuthAction.setOnClickListener. Must implement mAuthenticationListener");
//                    }
//                }else{
//                    try{
//                        mAuthenticationListener.onAuthenticationRequested(platformType);
//                    }catch (Exception e){
//                        Log.e(TAG, "Error in btnAuthAction.setOnClickListener. Must implement mAuthenticationListener");
//                    }
//                }
//                notifyItemChanged(position);
//            }
//        });



    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mPlatforms.size();
    }


    public void expandView(int position){
        if(mExpandedIndex.equals(position)){
            mExpandedIndex = INDEX_NOT_SET;
        }else {
            int oldPosition = mExpandedIndex;
            mExpandedIndex = position;
            if (oldPosition != INDEX_NOT_SET) {
                notifyItemChanged(oldPosition);
            }
        }
        notifyItemChanged(position);
    }

    public void setAuthenticationListener(AuthenticationListener listener){
        mAuthenticationListener = listener;
    }

    private int getSocialIconId(AuthPlatform.PlatformType platformType){
        switch (platformType){
            case FACEBOOK:
                return R.drawable.ic_fb_color;
            case TWITTER:
                return R.drawable.ic_tw_color;
            case INSTAGRAM:
                return R.drawable.ic_ig_color;
            default:
                Log.e(TAG, "Unhandled platformType in getSocialIconId(int position)");
                return ID_NO_DRAWABLE;
        }
    }

    private Drawable getSocialDrawable(AuthPlatform.PlatformType platformType){

        Drawable drawable;
        switch (platformType) {
            case FACEBOOK:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_fb_color);
                drawable.setBounds(0, 0, ICON_DIM, ICON_DIM);
                return drawable;
            case TWITTER:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_tw_color);
                drawable.setBounds(0, 0, ICON_DIM, ICON_DIM);
                return drawable;
            case INSTAGRAM:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_ig_color);
                drawable.setBounds(0, 0, ICON_DIM, ICON_DIM);
                return drawable;
            default:
                Log.e(TAG, "Unhandled platformType in getSocialDrawable(AuthPlatform.PlatformType platformType)");
                return null;
        }
    }

    public interface AuthManagerAdapterListener {
        public void logOut(AuthPlatform.PlatformType platformType);
        public void logIn(AuthPlatform.PlatformType platformType);
    }

}
