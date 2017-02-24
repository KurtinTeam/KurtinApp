package com.kurtin.kurtin.helpers;

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

import com.kurtin.kurtin.activities.MainActivity;

/**
 * Created by cvar on 2/23/17.
 */

public class ScreenUtils {

    public static final ScreenType USABLE_SCREEN = ScreenType.USABLE_SCREEN;
    public static final ScreenType ENTIRE_SCREEN = ScreenType.ENTIRE_SCREEN;

    private enum ScreenType{
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

}
