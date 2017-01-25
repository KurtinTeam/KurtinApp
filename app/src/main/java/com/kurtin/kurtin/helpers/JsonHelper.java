package com.kurtin.kurtin.helpers;

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
}
