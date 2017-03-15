package com.kurtin.kurtin.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.WindowManager;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.kurtin.kurtin.activities.MainActivity;

/**
 * Created by cvar on 2/23/17.
 */

public class ScreenUtils {

    public static final ScreenType USABLE_SCREEN = ScreenType.USABLE_SCREEN;
    public static final ScreenType ENTIRE_SCREEN = ScreenType.ENTIRE_SCREEN;

    public enum ScreenType{
        USABLE_SCREEN, ENTIRE_SCREEN
    }

    public static Point getSize(Context context, ScreenType screenType){
        Point screenSize = new Point();
        if(screenType.equals(ENTIRE_SCREEN)){
            context = context.getApplicationContext();
        }
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(screenSize);
        return screenSize;
    }

    public static int getHeight(Context context, ScreenType screenType){
        Point screenSize = getSize(context, screenType);
        return screenSize.y;
    }

    public static int getWidth(Context context, ScreenType screenType){
        Point screenSize = getSize(context, screenType);
        return screenSize.x;
    }

    public static float dpToPixels(Resources resources, float dp){
        float density = resources.getDisplayMetrics().density;
        return dp * density;
    }
}
