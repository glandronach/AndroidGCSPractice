package com.viasofts.mygcs.utils;

import android.util.Log;

public class Logger {

    private static final String TAG = "MRLogger";

    public static final boolean ISDEBUG = true;

    public static final int STRING_LENGTH_LIMIT = 1000;

    public static void d(String msg) {
        if(isLog()) Log.d(TAG, "[DEBUG] : " + msg);
    }

    public static void d(String tag, String msg) {
        if(isLog()) Log.d(TAG, "[" +tag + "] : " + msg);
    }

    public static void longd(String msg) {
        d("longd length : " + msg.length());
        for(int i = 0; i <= msg.length() / STRING_LENGTH_LIMIT; i++) {
            int start = i * STRING_LENGTH_LIMIT;
            int end = (i+1) * STRING_LENGTH_LIMIT;
            end = end > msg.length() ? msg.length() : end;
            d("\n" + msg.substring(start, end));
        }
    }

    public static void e(String msg) {
        if(isLog()) Log.e(TAG, "[ERROR] : " + msg);
    }

    public static void e(String tag, String msg) {
        if(isLog()) Log.e(TAG, "[" +tag + "] : " + msg);
    }

    public static void v(String msg) {
        if(isLog()) Log.v(TAG, "[VERBOSE] : " + msg);
    }

    public static void v(String tag, String msg) {
        if(isLog()) Log.v(TAG, "[" +tag + "] : " + msg);
    }

    public static void w(String msg) {
        if(isLog()) Log.w(TAG, "[WARNING] : " + msg);
    }

    public static void w(String tag, String msg) {
        if(isLog()) Log.w(TAG, "[" +tag + "] : " + msg);
    }

    public static void i(String msg) {
        if(isLog()) Log.i(TAG, "[INFO] : " + msg);
    }

    public static void i(String tag, String msg) {
        if(isLog()) Log.i(TAG, "[" +tag + "] : " + msg);
    }

    public static boolean isLog() {
//        return BuildConfig.DEBUG;
        return ISDEBUG;
    }
}
