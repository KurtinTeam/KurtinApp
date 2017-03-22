package com.kurtin.kurtin.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.kurtin.kurtin.activities.MainActivity;

import static android.R.attr.screenSize;

/**
 * Created by cvar on 2/23/17.
 */

public class ScreenUtils {

    public static final String TAG = "ScreenUtils";

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

    public static float getTopDecorationHeight(Context context) {
        float topDecorationHeight;
        Rect rectangle = new Rect();
        Point fullScreenSize = new Point();
        Point usableScreenSize = new Point();
        ((WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay()
                .getSize(fullScreenSize);
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay()
                .getSize(usableScreenSize);
        topDecorationHeight = fullScreenSize.y - usableScreenSize.y;

        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay()
                .getRectSize(rectangle);

        Log.d(TAG, "Top decoration (full/usable): " + fullScreenSize + " / " + usableScreenSize);
        Log.d(TAG, "Top decoration from rectangle: " + rectangle.top);

        return topDecorationHeight;
    }

    public static int getTopDecorationHeightRect(Context context){
        Rect rectangle = new Rect();

        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay()
                .getRectSize(rectangle);

        Log.d(TAG, "Top decoration from rectangle: " + rectangle.top);

        return rectangle.top;
    }
}
