package com.kurtin.kurtin.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.FacebookRequestError;
import com.kurtin.kurtin.R;
import com.kurtin.kurtin.clients.FacebookClient;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class TestFragment extends Fragment {

    public static final String TAG = "TestFragment";

    Button btnParseTest;
    Button btnFbApiTest;

    public TestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_test, container, false);
        bindUi(parentView);
        setOnClickListeners();
        return parentView;
    }

    private void bindUi(View parentView){
        btnParseTest = (Button) parentView.findViewById(R.id.btnParseTest);
        btnFbApiTest = (Button) parentView.findViewById(R.id.btnFbApiTest);
    }

    private void setOnClickListeners(){
        btnParseTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseObject testObject = new ParseObject("TestObject");
                testObject.put("foo", "bar");
                testObject.saveInBackground();
            }
        });

        btnFbApiTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = "21105780752";
                FacebookClient.getSharedInstance().getPageWithID(id, new FacebookClient.JsonCallback() {
                    @Override
                    public void onSuccess(JSONObject fbJsonObject) {
                        Log.v(TAG, "Success & Array: " + fbJsonObject.toString());
                    }

                    @Override
                    public void onFailure(FacebookRequestError error) {
                        Log.v(TAG, "Error: " + error.toString());
                    }
                });
            }
        });
    }

}

//url search
//String url = "https://www.facebook.com/UniversityOfMichigan";
//FacebookClient.getSharedInstance().getObjectWithUrl(url, new FacebookClient.JsonCallback() {
//@Override
//public void onSuccess(JSONObject fbJsonObject) {
//        Log.v(TAG, "Success & Object: " + fbJsonObject.toString());
//        }
//
//@Override
//public void onFailure(FacebookRequestError error) {
//        Log.v(TAG, "Error: " + error.toString());
//        }
//        });

//Page query
//        FacebookClient.getSharedInstance().searchForPageWithQuery(url, new FacebookClient.JsonCallback() {
//@Override
//public void onSuccess(JSONObject fbJsonObject) {
//        Log.v(TAG, "Page query response: " + response.getJSONObject().toString());
//        JsonHelper jsonHelper = new JsonHelper(response.getJSONObject());
//        JSONArray array = jsonHelper.getJsonArray(ResponseKeys.DATA);
//TODO: Figure out if you need to handle a null case where no errors but no data
//        }
//
//@Override
//public void onFailure(FacebookRequestError error) {
//        Log.v(TAG, "Error: " + error.toString());
//        }
//        });