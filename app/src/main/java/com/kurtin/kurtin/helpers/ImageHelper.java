package com.kurtin.kurtin.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.facebook.drawee.view.SimpleDraweeView;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;

import static com.kurtin.kurtin.R.id.ivTest;

/**
 * Created by cvar on 2/23/17.
 */

public class ImageHelper {

//    public static void loadParseFileIntoFresco(ParseFile imageFile, SimpleDraweeView sdv){
//        imageFile.getDataInBackground(new GetDataCallback() {
//            @Override
//            public void done(byte[] data, ParseException e) {
//                Log.v(TAG, "Data downloaded");
//                if(e != null) {
//                    e.printStackTrace();
//                }
//                if (data != null) {
//                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                    sdv.
//                    ivTest.setImageBitmap(bitmap);
//                    Log.v(TAG, "Image set");
//                }else{
//                    Log.v(TAG, "Data was null");
//                }
//            }
//        });
//    }

}
