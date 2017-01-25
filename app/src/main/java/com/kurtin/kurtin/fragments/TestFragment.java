package com.kurtin.kurtin.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kurtin.kurtin.R;
import com.parse.ParseObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class TestFragment extends Fragment {

    public static final String TAG = "TestFragment";

    Button btnParseTest;

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
    }

}
