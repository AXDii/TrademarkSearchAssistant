package com.example.newproject.web.util;

import android.os.Bundle;
import android.os.Message;

import com.example.newproject.web.cons.CommonConstant;

public class MessageFactory {

    public static Message createMessageFromObject(Object object){
        Message message = new Message();
        Bundle data = new Bundle();
        data.putString(CommonConstant.JSON_STRING, JSONStringUtil.getJSONStringFromObject(object));
        message.setData(data);
        return message;
    }
}
