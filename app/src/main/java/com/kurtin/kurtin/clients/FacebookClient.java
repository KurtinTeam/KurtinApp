package com.kurtin.kurtin.clients;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.kurtin.kurtin.helpers.JsonHelper;
import com.kurtin.kurtin.models.AuthPlatform;
import com.kurtin.kurtin.models.FacebookUser;
import com.kurtin.kurtin.models.KurtinUser;
import com.kurtin.kurtin.persistence.CurrentUserPreferences;

import org.json.JSONArray;
import org.json.JSONObject;


import static android.R.attr.description;
import static android.R.attr.id;
import static com.kurtin.kurtin.helpers.JsonHelper.TAG;

/**
 * Created by cvar on 1/23/17.
 */

public class FacebookClient {

    public static final String TAG = "FacebookClient";

    private static AuthPlatform.PlatformType FACEBOOK_PLATFORM = AuthPlatform.PlatformType.FACEBOOK;
    private static FacebookClient mSharedClient = null;

    private FacebookClient(){

    }

    public static FacebookClient getSharedInstance(){
        if(mSharedClient == null){
            mSharedClient = new FacebookClient();
        }
        return mSharedClient;
    }

    public void logOut(Context context) {
        LoginManager.getInstance().logOut();
        KurtinUser.setAuthenticationStatus(context, FACEBOOK_PLATFORM, false);
    }

    public void getCurrentUser(final JsonCallback callback){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    if (response.getError() == null){
                        callback.onSuccess(object);
                    }else{
                        callback.onFailure(response.getError());
                    }
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString(ParamKeys.FIELDS, ParamValues.ME_FIELDS);
            graphRequest.setParameters(parameters);
            graphRequest.executeAsync();
        }

    }

    public void checkAuthenticationStatus(final BooleanCallback callback){
        if(this.hasAccessToken()) {
            getCurrentUser(new JsonCallback() {
                @Override
                public void onSuccess(JSONObject fbJsonObject) {
                    callback.result(true);
                }

                @Override
                public void onFailure(FacebookRequestError error) {
                    callback.result(false);
                }
            });
        }else{
            callback.result(false);
        }
    }

    public boolean hasAccessToken(){
        return AccessToken.getCurrentAccessToken() != null;
    }

    //Implemented the below function in FacebookUser
    //TODO: delete function once FacebookUser funciton is tested
//    public void updateIdInSharedPreferences(final Context context){
//        if(CurrentUserPreferences.userIsLoggedIn(context)){
//            getCurrentUser(new JsonCallback() {
//                @Override
//                public void onSuccess(JSONObject fbJsonObject) {
//                    JsonHelper jsonHelper = new JsonHelper(fbJsonObject);
//                    String facebookId = jsonHelper.getString(FacebookUser.FbKeys.ID, null);
//                    if(facebookId != null) {
//                        CurrentUserPreferences.setFacebookId(context, facebookId);
//                    }else{
//                        Log.e(TAG, "Failure retrieving the facebookId in updateIdInSharedPreferences(final Context context)");
//                    }
//                }
//
//                @Override
//                public void onFailure(FacebookRequestError error) {
//                    Log.e(TAG, "Failure getting current user in updateIdInSharedPreferences(final Context context)");
//                }
//            });
//        }
//    }

    public void getMe(final JsonCallback callback) {
        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.v(TAG, "Me object: " + object);
                Log.v(TAG, "Me response: " + response.toString());
                Log.v(TAG, "Me response error: " + response.getError().toString());
                if (response.getError() == null) {
                    callback.onSuccess(object);
                }else{
                    callback.onFailure(response.getError());
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString(ParamKeys.FIELDS, ParamValues.ME_FIELDS);
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }

    public void getObjectWithUrl(String url, final JsonCallback callback){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        String endpoint = "/";
        HttpMethod method = HttpMethod.GET;
        Bundle params = new Bundle();
        params.putString(ParamKeys.ID, url);
        params.putString(ParamKeys.FIELDS, ParamValues.OPEN_GRAPH_OBJECT_FIELDS);

        /* make the API call */
        new GraphRequest(
                accessToken, endpoint,
                params, method,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                    /* handle the result */
                        if (noErrorsIn(response)){
                            callback.onSuccess(response.getJSONObject());
                        }else{
                            callback.onFailure(response.getError());
                        }
                    }
                }
        ).executeAsync();
    }

    public void getUserWithID(String user_id, final JsonCallback callback){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        String endpoint = "/" + user_id;
        HttpMethod method = HttpMethod.GET;
        Bundle params = new Bundle();
        params.putString(ParamKeys.FIELDS, ParamValues.USER_FIELDS);

        executeAsyncGraphRequest(
                accessToken, endpoint, params, method,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        if(noErrorsIn(response)){
                            callback.onSuccess(response.getJSONObject());
                        }else{
                            callback.onFailure(response.getError());
                        }
                    }
                });
    }

    public void getPageWithID(String page_id, final JsonCallback callback){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        String endpoint = "/" + page_id;
        HttpMethod method = HttpMethod.GET;
        Bundle params = new Bundle();
        params.putString(ParamKeys.FIELDS, ParamValues.UNIV_PAGE_FIELDS);

        executeAsyncGraphRequest(
                accessToken, endpoint, params, method,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        if(noErrorsIn(response)){
                            callback.onSuccess(response.getJSONObject());
                        }else{
                            callback.onFailure(response.getError());
                        }
                    }
                });
    }

    public void profilePic(String id, final JsonCallback callback){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        String endpoint = "/" + id + "/picture";
        HttpMethod method = HttpMethod.GET;
        Bundle params = new Bundle();
        params.putString(ParamKeys.REDIRECT, ParamValues.FALSE);

        executeAsyncGraphRequest(
                accessToken, endpoint, params, method,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        if(noErrorsIn(response)){
                            callback.onSuccess(response.getJSONObject());
                        }else{
                            callback.onFailure(response.getError());
                        }
                    }
                });
    }

    public void searchForPageWithQuery(String query, final JsonCallback callback){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        String endpoint = "search";
        HttpMethod method = HttpMethod.GET;
        Bundle params = new Bundle();
        params.putString(ParamKeys.QUERY, query);
        params.putString(ParamKeys.TYPE, ParamValues.PAGE_TYPE);
        params.putString(ParamKeys.FIELDS, ParamValues.OPEN_GRAPH_OBJECT_FIELDS);

        executeAsyncGraphRequest(
                accessToken, endpoint, params, method,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        if(noErrorsIn(response)){
                            callback.onSuccess(response.getJSONObject());
                        }else{
                            callback.onFailure(response.getError());
                        }
                    }
                });
    }

    public void searchForUserWithQuery(String query, final JsonCallback callback){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        String endpoint = "search";
        HttpMethod method = HttpMethod.GET;
        Bundle params = new Bundle();
        params.putString(ParamKeys.QUERY, query);
        params.putString(ParamKeys.TYPE, ParamValues.USER_TYPE);

        executeAsyncGraphRequest(
                accessToken, endpoint, params, method,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        if(noErrorsIn(response)){
                            callback.onSuccess(response.getJSONObject());
                        }else{
                            callback.onFailure(response.getError());
                        }
                    }
                });
    }

    private void executeAsyncGraphRequest(
            AccessToken accessToken, String endpoint,
            Bundle params, HttpMethod method, GraphRequest.Callback callback){
        new GraphRequest(
                accessToken, endpoint, params, method, callback).executeAsync();

    }

    private boolean noErrorsIn(GraphResponse response){
        return response.getError() == null;
    }

    //Returns null if there is no token
    public String getAccessToken(Context context){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        String string;
        if(accessToken != null){
            string = accessToken.getToken();
        }else{
            string = null;
        }
        Log.v(TAG, "Token: " + string);
        return string;
    }

    public interface JsonCallback{
        void onSuccess(JSONObject fbJsonObject);
        void onFailure(FacebookRequestError error);
    }

    public interface JsonArrayCallback{
        void onSuccess(JSONArray fbJsonArrayObject);
        void onFailure(FacebookRequestError error);
    }

    public interface BooleanCallback{
        void result(boolean booleanResult);
    }

    private final class ParamKeys{
        private static final String FIELDS = "fields";
        private static final String ID = "id";
        private static final String REDIRECT = "redirect";
        private static final String QUERY = "q";
        private static final String TYPE = "type";
    }

    private final class ParamValues{
        private static final String ME_FIELDS = "id, name, first_name, last_name, link, email, location, third_party_id";
        private static final String USER_FIELDS = "id, name, first_name, last_name, link, email, location, third_party_id";
        private static final String UNIV_PAGE_FIELDS = "id, name, about, bio, affiliation, " +
                "app_links, best_page, birthday, cover, description, featured_video, founded, " +
                "general_info, location, mission, overall_star_rating, place_type, website";

        private static final String OPEN_GRAPH_OBJECT_FIELDS = "id, name, about, cover, bio";
        private static final String FALSE = "false";
        private static final String TRUE = "true";
        private static final String PAGE_TYPE = "page";
        private static final String USER_TYPE = "user";

        //Working Page fields
        //Not functional page fields: ad_campaign,
    }

    private final class ResponseKeys{
        private static final String DATA = "data";
    }
}
