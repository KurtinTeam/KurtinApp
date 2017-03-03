package com.kurtin.kurtin.fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.facebook.FacebookRequestError;
import com.kurtin.kurtin.R;
import com.kurtin.kurtin.clients.FacebookClient;
import com.kurtin.kurtin.clients.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TestFragment extends Fragment {

    public static final String TAG = "TestFragment";
    public static final String TITLE = "App Testing";


    Button btnParseTest;
    Button btnFbApiTest;
    Button btnTwApiTest;
    Button btnQuery;

    ImageView ivTest;

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
        btnTwApiTest = (Button) parentView.findViewById(R.id.btnTwApiTest);
        btnQuery = (Button) parentView.findViewById(R.id.btnQuery);

        ivTest = (ImageView) parentView.findViewById(R.id.ivTest);
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

        btnTwApiTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = "42562446";
                TwitterClient.getSharedInstance(getContext()).getTimelineWithUserId(id, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(
                            int statusCode, cz.msebera.android.httpclient.Header[] headers,
                            org.json.JSONObject response){
                        Log.v(TAG, "Success. Object response: " + response.toString());
                    }

                    @Override
                    public void onSuccess(
                            int statusCode, cz.msebera.android.httpclient.Header[] headers,
                            org.json.JSONArray response){
                        Log.v(TAG, "Success. Array response: " + response.toString());
                    }

                    @Override
                    public void onFailure(
                            int statusCode, cz.msebera.android.httpclient.Header[] headers,
                            java.lang.Throwable throwable, org.json.JSONObject errorResponse){
                        Log.v(TAG, "Failure. errorResponse: " + errorResponse.toString());
                    }
                });
            }
        });

        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("School");
                query.whereEqualTo("objectId", "4EXc9CyDDT");
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> schoolList, ParseException e) {
                        if (e == null) {
                            Log.d("score", "Retrieved " + schoolList.size() + " scores");
                            ParseObject school = schoolList.get(0);
                            ParseFile campusImage = (ParseFile) school.get("studentImage");
                            campusImage.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    Log.v(TAG, "Data downloaded");
                                    if(e != null) {
                                        e.printStackTrace();
                                    }
                                    if (data != null) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        ivTest.setImageBitmap(bitmap);
                                        Log.v(TAG, "Image set");
                                    }else{
                                        Log.v(TAG, "Data was null");
                                    }
                                }
                            });
                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                        }
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