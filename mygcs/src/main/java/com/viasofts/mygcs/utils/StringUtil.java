package com.viasofts.mygcs.utils;


public class StringUtil {

    public static boolean isNullOrEmpty(String str){

        if(str == null || str.isEmpty() || str.length() < 1)
            return true;

        str = removeSpace(str);
        if(str.isEmpty() || str.length() < 1)
            return true;

        return false;
    }

    public static String removeSpace(String s) {
        
        String withoutspaces = "";
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != ' ')
                withoutspaces += s.charAt(i);

        }
        return withoutspaces;
    }
}
