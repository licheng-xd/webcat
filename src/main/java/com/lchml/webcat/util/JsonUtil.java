package com.lchml.webcat.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.util.Map;

/**
 * Created by lc on 11/14/17.
 */
public class JsonUtil {
    private final static Gson gson = new Gson();

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T1, T2> Map<T1, T2> json2Map(String jsonStr) {
        return gson.fromJson(jsonStr, (new TypeToken<Map<T1, T2>>() {}).getType());
    }

    public static <T> T json2Object(String jsonStr, TypeToken<T> typeToken) {
        return gson.fromJson(jsonStr, typeToken.getType());
    }

    public static <T> T json2Object(String jsonStr, Class<T> clazz) {
        return gson.fromJson(jsonStr, clazz);
    }
}
