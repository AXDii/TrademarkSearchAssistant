package com.example.newproject.web.util;

import java.util.regex.Pattern;

public class CheckFormat {

    private static final String REGEX_PHONE = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9])|(17[0-9]))\\d{8}$";
    private static final String REGEX_PASSWORD = "^[0-9a-zA-Z]{6,16}$";
    private static final String REGEX_NAME = "^(?!_)(?!.*?_$)[a-zA-Z0-9_\\u4e00-\\u9fa5]+$";
    private static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";


    public static boolean isPhoneNumberFormat(String texts) {
        return Pattern.matches(REGEX_PHONE, texts);
    }

    public static boolean isCorrectPasswordFormat(String texts){
        return Pattern.matches(REGEX_PASSWORD, texts);
    }

    public static boolean isCorrectNameFormat(String texts) {
        return Pattern.matches(REGEX_NAME, texts);
    }

    public static boolean isCorrectEmailFormat(String texts) {

        return Pattern.matches(REGEX_EMAIL, texts);

    }



}
