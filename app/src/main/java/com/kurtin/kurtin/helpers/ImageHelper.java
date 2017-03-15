package com.kurtin.kurtin.helpers;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.kurtin.kurtin.R;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;

import static com.kurtin.kurtin.R.id.ivTest;

/**
 * Created by cvar on 2/23/17.
 */

public class ImageHelper {

    public static GenericDraweeHierarchy getBasicHierarchy(Resources resources){
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(resources);
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(300)
                .build();
        return hierarchy;
    }

    public static GenericDraweeHierarchy getBasicHierarchy(Resources resources, Drawable placeholder){
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(resources);
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(300)
                .setPlaceholderImage(placeholder)
                .build();
        return hierarchy;
    }

    public static GenericDraweeHierarchy getBasicHierarchy(Resources resources, Drawable placeholder, Drawable background){
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(resources);
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(300)
                .setPlaceholderImage(placeholder)
                .setBackground(background)
                .build();
        return hierarchy;
    }

    public static GenericDraweeHierarchy getCircleHierarchy(Resources resources){
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(resources);
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(300)
                .setRoundingParams(RoundingParams.asCircle())
                .build();
        return hierarchy;
    }

    public static GenericDraweeHierarchy getCircleHierarchy(Resources resources, int borderColor, float borderWidth){
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(resources);
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(300)
                .setRoundingParams(RoundingParams.asCircle()
                        .setBorder(borderColor, borderWidth)
                        .setPadding(borderWidth))
                .build();
        return hierarchy;
    }

}
