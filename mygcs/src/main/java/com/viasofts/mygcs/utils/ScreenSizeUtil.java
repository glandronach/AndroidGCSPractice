package com.viasofts.mygcs.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class ScreenSizeUtil {

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public static int getWindowSizeWidthDP(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return (int)convertPixelsToDp(dm.widthPixels, context);
    }

    public static int getWindowSizeHeightDP(Context context) {
        return (int)convertPixelsToDp(getWindowSizeHeight(context), context);
    }

    public static int getWindowSizeHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }
}
