package com.viasofts.mygcs.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPrefHelper {

    public static final String PREF_FILE = "mrpref";

    /****************************************************/
    /******************* HOT SPOT INFO ******************/

    // MAC ADDRESS
    public static final String DEFAULT_MAC_ADDR = "5c:cf:7f:d9:66:7a";
//    public static final String DEFAULT_MAC_ADDR = "5C:CF:7F:D9:66:BF";

    // Station mode
    public static final String DEFAULT_SSID_ST = "vandi1234";
    public static final String DEFAULT_PWD_ST = "vandi1234";

    // AP mode
    public static final String DEFAULT_SSID_AP = "vandi1234";
    public static final String DEFAULT_PWD_AP = "vandi1234";
    /******************* HOT SPOT INFO ******************/
    /****************************************************/

    // 설정 - 기체 종류 선택에서 사용하는 기체 분류값
    public static final int VEHICLE_A1 = 1;
    public static final int VEHICLE_C1 = 2;

    public static final int LIQUID = 1;  // 액제
    public static final int GRANULAR = 2;  // 입제

    // Keys
    public static final String KEY_SSID_ST = "keyssidst";
    public static final String KEY_PWD_ST = "keypwdst";
    public static final String KEY_IP_ST = "keyipst";
    public static final String KEY_MACADDR = "keymacaddr";
    public static final String KEY_SSID_AP = "keyssidstap";
    public static final String KEY_PWD_AP = "keypwdstap";

    // 사용자 기체, 분사량, 고도모드 설정
    public static final String KEY_FRAME_TYPE = "keysltvehicle";  //  A1, C1 기체 선택값
    public static final String KEY_FRAME_PRODUCT_NUM = "keyframeproductnum";

    public static final String KEY_SLT_SPRAYAMOUNT = "keysltsprayamount"; // 자동방제시 액제 분사량 선택값 : 1단, 2단
    public static final String KEY_SLT_GRANULAMOUNT = "keysltgranulamount";  // 자동방제시 입제 개폐량 선택값 : 1단, 2단

    public static final String KEY_ALTMODE = "keyaltmode";  // 고도 설정 모드

    public static final String KEY_TERRAIN_ALT = "keyterrainalt";  // Terring 고도 설정값

    // 입제, 액제 선택
    public static final String KEY_GRADIENT = "sltgradient";

    // User
    public static final String KEY_USER_ID = "keyuserid";
    public static final String KEY_USER_NAME = "keyusername";

    // 이륙고도 저장값
    public static final String KEY_TAKEOFF_ALT = "takeoffalt";

    // 이어방제 마지막 missionListId 값
    public static final String KEY_ABORTEDMISSION_LAST_ID = "abortedmissonlast";

    // 자동분사(AutoSprayRate, AutoSpraySpeedMin) 관련
    public static final String KEY_AUTOSPRAY_SPEED_1 = "autosprayspeed1";
    public static final String KEY_AUTOSPRAY_SPEED_2 = "autosprayspeed2";

    public static SharedPreferences.Editor getPrefEdit(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_FILE, MODE_PRIVATE);
        return pref.edit();
    }

    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = getPrefEdit(context);
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences pref = context.getSharedPreferences(PREF_FILE, MODE_PRIVATE) ;
        return pref.getBoolean(key, defaultValue);
    }

    public static void putInt(Context context, String key, int value) {
        SharedPreferences.Editor editor = getPrefEdit(context);
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences pref = context.getSharedPreferences(PREF_FILE, MODE_PRIVATE);
        return pref.getInt(key, defaultValue);
    }

    public static void putFloat(Context context, String key, float value) {
        SharedPreferences.Editor editor = getPrefEdit(context);
        editor.putFloat(key, value);
        editor.commit();
    }

    public static float getFloat(Context context, String key, float defaultValue) {
        SharedPreferences pref = context.getSharedPreferences(PREF_FILE, MODE_PRIVATE);
        float v = pref.getFloat(key, defaultValue);
        return v;
    }

    public static void putString(Context context, String key, String value) {
        SharedPreferences.Editor editor = getPrefEdit(context);
        editor.putString(key, value);
        editor.commit();
    }

    public static String getString(Context context, String key, String defaultString) {
        SharedPreferences pref = context.getSharedPreferences(PREF_FILE, MODE_PRIVATE);
        return pref.getString(key, defaultString);
    }

    public static void removeString(Context context, String key) {
        SharedPreferences.Editor editor = getPrefEdit(context);
        editor.remove(key);
        editor.commit();
    }

    // HOTSPOT NETWORK SSID, PWD
    // Station mode
    public static String getAPSSID_ST(Context context) {
        return getString(context, KEY_SSID_ST, DEFAULT_SSID_ST);
    }

    public static void setAPSSID_ST(Context context, String ssid) {
        SharedPreferences.Editor editor = getPrefEdit(context);
        editor.putString(KEY_SSID_ST, ssid);
        editor.commit();
    }

    public static String getAPPWD_ST(Context context) {
        return getString(context, KEY_PWD_ST, DEFAULT_PWD_ST);
    }

    public static void setAPPWD_ST(Context context, String pwd) {
        SharedPreferences.Editor editor = getPrefEdit(context);
        editor.putString(KEY_PWD_ST, pwd);
        editor.commit();
    }

    public static void setAPIP_ST(Context context, String ip) {
        SharedPreferences.Editor editor = getPrefEdit(context);
        editor.putString(KEY_IP_ST, ip);
        editor.commit();
    }

    public static String getAPIP_ST(Context context, String defaultString) {
        return getString(context, KEY_IP_ST, defaultString);
    }

    // AP mode
    public static String getSSID_AP(Context context) {
        return getSSID_AP(context, DEFAULT_SSID_AP);
    }

    public static String getSSID_AP(Context context, String defaultSSID) {
        return getString(context, KEY_SSID_AP, defaultSSID);
    }

    public static void setSSID_AP(Context context, String ssid) {
        SharedPreferences.Editor editor = getPrefEdit(context);
        editor.putString(KEY_SSID_AP, ssid);
        editor.commit();
    }

    public static String getPWD_AP(Context context) {
        return getPWD_AP(context, DEFAULT_PWD_AP);
    }

    public static String getPWD_AP(Context context, String defaultPWD) {
        return getString(context, KEY_PWD_AP, defaultPWD);
    }

    public static void setPWD_AP(Context context, String pwd) {
        SharedPreferences.Editor editor = getPrefEdit(context);
        editor.putString(KEY_PWD_AP, pwd);
        editor.commit();
    }

    public static String getMacAddr(Context context) {
        return getString(context, KEY_MACADDR, DEFAULT_MAC_ADDR);
    }

    public static void setMacAddr(Context context, String macaddr) {
        SharedPreferences.Editor editor = getPrefEdit(context);
        editor.putString(KEY_MACADDR, macaddr);
        editor.commit();
    }

    public static String debugNetworkConnecInfo_ST(Context context) {
        String info = "DeviceNetworkCon [SSID_ST : " + getAPSSID_ST(context) + "], [PWD_ST : " + getAPPWD_ST(context) + "]";
        return info;
    }

    public static String debugNetworkConnecInfo_AP(Context context) {
        String info = "DeviceNetworkCon [SSID_AP : " + getSSID_AP(context) + "], [PWD_AP : " + getPWD_AP(context) + "]";
        return info;
    }
}
