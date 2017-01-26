package com.kurtin.kurtin.helpers;

import android.util.Log;

import org.json.JSONObject;

import static android.R.attr.key;

/**
 * Created by cvar on 1/24/17.
 */

public class JsonHelper {
    public static final String TAG = "JsonHelper";

    private JSONObject mJsonObject;

    public JsonHelper(JSONObject jsonObject){
        mJsonObject = jsonObject;
    }

    public void setRootJsonObject(JSONObject mJsonObject) {
        this.mJsonObject = mJsonObject;
    }

    public String getString(String key, String defaultValue){
        String value;
        try{
            value = mJsonObject.getString(key);
            return value;
        }catch (Exception e){
            e.printStackTrace();
            return defaultValue;
        }
    }

    public String getString(String key){
        String value;
        try{
            value = mJsonObject.getString(key);
            return value;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getJsonObject(String key){
        JSONObject value;
        try{
            value = mJsonObject.getJSONObject(key);
            if (value instanceof  JSONObject) {
                return value;
            }else{
                String error = "Tried to access a JSONObject but value was of a different type";
                Log.e(TAG, error);
                value.put("error", error);
                return value;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
