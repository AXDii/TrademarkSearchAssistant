package com.example.newproject.web.core;

import android.util.Log;

import com.example.newproject.web.cons.CommonConstant;
import com.google.gson.Gson;

import java.nio.charset.Charset;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OkHttpsFactory {

    private static final String TAG = "OkHttpsFactory";

    private static OkHttpClient okHttpClient = null;

    //用于post方法
    private static Request createRequest(String url, RequestBody requestBody) {

        return  new Request.Builder().url(url).post(requestBody).build();

    }

    //用于get方法
    public static Request createRequest(String url) {
        Log.d(TAG, "createRequest: " + url);
        return new Request.Builder().url(url).get().build();
    }


    //默认转换为jsonString
    public static Request createRequest(String url, Object object) {

        Gson gson = new Gson();
        String jsonString = gson.toJson(object);
        //---------调试代码------
        Log.d(TAG, "createRequest: " + jsonString);
        //----------end----------
//        RequestBody requestBody1 = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), jsonString);
        Charset utf8Charset = Charset.forName("UTF-8");
        RequestBody requestBody = new FormBody.Builder(utf8Charset).add(CommonConstant.JSON_STRING, jsonString).build();
        return  createRequest(url, requestBody);

    }

    public static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        }
        return okHttpClient;
    }

}
