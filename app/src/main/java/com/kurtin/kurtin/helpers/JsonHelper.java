package com.kurtin.kurtin.helpers;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import static android.R.attr.key;
import static android.R.attr.value;

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

    public JSONArray getJsonArray(String key){
        JSONArray array;
        try{
            array = mJsonObject.getJSONArray(key);
            return array;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getString(JSONObject jsonObject, String key){
        String value;
        try{
            value = jsonObject.getString(key);
            return value;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getObjectFromArray(JSONArray jsonArray, int index){
        try{
            JSONObject jsonObject = jsonArray.getJSONObject(index);
            return jsonObject;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getJsonObject(JSONObject parentObject, String key){
        JSONObject value;
        try{
            return parentObject.getJSONObject(key);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
