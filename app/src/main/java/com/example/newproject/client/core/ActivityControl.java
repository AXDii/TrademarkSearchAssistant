package com.example.newproject.client.core;

import android.app.Activity;
import android.content.Context;


/**
 * Created by AXD on 2018/3/20.
 * 页面控制类，用于保存父类
 */

public class ActivityControl {

    private static Context context = null;
    private static Activity activity = null;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        ActivityControl.context = context;
    }

    public static Activity getActivity() {
        return activity;
    }

    public static void setActivity(Activity activity) {
        ActivityControl.activity = activity;
    }

    public static void finishActivity(){
        if (activity != null) {

            activity.finish();

        }
    }
}
