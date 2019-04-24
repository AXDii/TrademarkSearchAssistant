package com.example.newproject.web.util;

import android.content.Context;
import android.widget.Toast;

public class MyToast {

    private Context context;

    public MyToast(Context context){
//        Toast toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        this.context = context;
    }

    public void show(String message, int duration) {
        Toast.makeText(context, message, duration).show();
    }

    public void showLongTime(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public void showShortTime(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
