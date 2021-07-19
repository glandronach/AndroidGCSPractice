package com.viasofts.mygcs.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {

    public static String GetNowString(){
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }
}
