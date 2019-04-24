package com.example.newproject.web.core;

import com.example.newproject.web.cons.CommonConstant;
import com.example.newproject.web.domain.User;

public class UserInfo {

    private static User user;
    private static boolean isUserOnline = CommonConstant.USER_OFFLINE;

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        UserInfo.user = user;
    }

    public static boolean isIsUserOnline() {
        return isUserOnline;
    }

    private static void setIsUserOnline(boolean isUserOnline) {
        UserInfo.isUserOnline = isUserOnline;
    }

    public static void changeToOnlineStatus(){
        setIsUserOnline(CommonConstant.USER_ONLINE);
    }

    public static void changeToOfflineStatus() {
        setIsUserOnline(CommonConstant.USER_OFFLINE);
    }
}
