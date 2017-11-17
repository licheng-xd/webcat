package com.lchml.webcat.util;

/**
 * Created by lc on 11/14/17.
 */
public class NumUtil {
    public static double toDouble(String str) {
        return toDouble(str, 0.0D);
    }

    public static double toDouble(String str, double defaultValue) {
        if(str == null) {
            return defaultValue;
        } else {
            try {
                return Double.parseDouble(str);
            } catch (NumberFormatException var4) {
                return defaultValue;
            }
        }
    }
}
