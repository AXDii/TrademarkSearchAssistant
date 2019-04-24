package com.example.newproject.web.util;

import com.google.gson.Gson;

public class JSONStringUtil {

    private static Gson gson;

    static {
        gson = new Gson();
    }

    public static String getJSONStringFromObject(Object object) {

        return gson.toJson(object);

    }


    public static Object getObjectFromJSONString(String jsonString, Class _class) {

        return gson.fromJson(jsonString, _class);

    }


}
