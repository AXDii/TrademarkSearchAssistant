package com.example.newproject.client.core;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.example.newproject.web.cons.CommonConstant;
import com.example.newproject.web.core.OkHttpsFactory;
import com.example.newproject.web.core.TrademarkList;
import com.example.newproject.web.domain.InitDataDomain;
import com.example.newproject.web.util.JSONStringUtil;
import com.example.newproject.web.util.MessageFactory;
import com.example.newproject.web.util.MyToast;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 初始化程序所使用的类
 */
public class InitApp {

    private static final String TAG = "InitApp";
    private static boolean initComplete = false;
    private static Context context = null;

    static class MyHandler extends Handler {

        WeakReference<Activity> weakReference;

        public MyHandler(Activity activity) {
            weakReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            String jsonString = data.getString(CommonConstant.JSON_STRING);
            InitDataDomain initDataDomain = (InitDataDomain)JSONStringUtil.getObjectFromJSONString(jsonString, InitDataDomain.class);
            Context context = weakReference.get();

            MyToast myToast = new MyToast(context);
            switch (initDataDomain.getStatusCode()) {
                case CommonConstant.GAIN_INIT_DATA_FAULT:
                    myToast.showLongTime("服务器出错");
                    break;
                case CommonConstant.OPERATE_SUCCESS:
                    Log.d(TAG, "handleMessage: 获取数据成功");
                    TrademarkList.setTrademarkListDomains(initDataDomain.getTrademarkListDomainList());
                    break;
            }
        }
    }


    public static Context getContext() {
        return context;
    }

    private static void setContext(Context context) {
        InitApp.context = context;
    }

    public static void init(Activity activity){
        initComplete = true;

        final MyToast myToast = new MyToast(activity);

        final MyHandler myHandler = new MyHandler(activity);

        setContext(activity);

        Request request = OkHttpsFactory.createRequest(CommonConstant.API_GET_TRADEMARK_LIST_DATA);
        OkHttpClient client = OkHttpsFactory.getOkHttpClient();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                myToast.showShortTime("网络出错");
                Looper.loop();
                Log.d(TAG, "onFailure: 网络出错");
                Log.d(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonString = response.body().string();
                InitDataDomain initDataDomain = (InitDataDomain)JSONStringUtil.getObjectFromJSONString(jsonString, InitDataDomain.class);
//                TrademarkList.setTrademarkListDomains(initDataDomain.getTrademarkListDomainList());
                Message message = MessageFactory.createMessageFromObject(initDataDomain);
                myHandler.sendMessage(message);
            }
        });


        DirManager.InitDirs(activity);



    }

    public static boolean isInitComplete() {
        return initComplete;
    }
}
