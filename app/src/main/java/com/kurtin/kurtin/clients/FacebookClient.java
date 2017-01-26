package com.kurtin.kurtin.clients;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.kurtin.kurtin.models.FacebookUser;

import org.json.JSONObject;
import org.scribe.model.Token;

import static com.kurtin.kurtin.helpers.JsonHelper.TAG;

/**
 * Created by cvar on 1/23/17.
 */

public class FacebookClient {
    public static void getCurrentUser(final JsonCallback callback){
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
            parameters.putString("fields", "id, name, first_name, last_name, link, email, location, third_party_id");
            graphRequest.setParameters(parameters);
            graphRequest.executeAsync();
        }

    }

    //Returns null if there is no token
    public static String getAccessToken(Context context){
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
        void onSuccess(JSONObject fbUserJsonObject);
        void onFailure(FacebookRequestError error);
    }
}
