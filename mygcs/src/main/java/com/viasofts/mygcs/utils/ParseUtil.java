package com.viasofts.mygcs.utils;

import java.util.regex.Pattern;

public class ParseUtil {

    public static int parseStringToInt(String s, int defaultValue) {
        try {
            return s.matches("-?\\d+") ? Integer.parseInt(s) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static float parseStringToFloat(String s, float defaultValue) {
        try {
            return isFloat(s) ? Float.parseFloat(s) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static double parseStringToDouble(String s, double defaultValue) {
        try {
            return isFloat(s) ? Double.parseDouble(s) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static final Pattern DOUBLE_PATTERN = Pattern.compile(
            "[\\x00-\\x20]*[+-]?(NaN|Infinity|((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)" +
                    "([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|" +
                    "(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))" +
                    "[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*");

    public static boolean isFloat(String s)
    {
        return DOUBLE_PATTERN.matcher(s).matches();
    }
}
