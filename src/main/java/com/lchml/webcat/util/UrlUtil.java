package com.lchml.webcat.util;

/**
 * Created by lc on 11/15/17.
 */
public class UrlUtil {
    public static String resolveUrlPath(String path) {
        if (!path.isEmpty()) {
            return (path.startsWith("/") ? path : "/" + path).replace("//", "/");
        } else {
            return path;
        }
    }
}
