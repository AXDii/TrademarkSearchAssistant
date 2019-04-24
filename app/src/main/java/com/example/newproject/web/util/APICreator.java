package com.example.newproject.web.util;

import com.example.newproject.web.cons.CommonConstant;

public class APICreator {

    public static String createGetTrademarkImgApi(int trademarkId) {

        return CommonConstant.API_GET_TRADEMARK_IMG_BY_ID + "?trademarkId=" + trademarkId;

    }

    public static String createGetTrademarkTbImgApi(int trademarkId) {

        return CommonConstant.API_GET_TRADEMARK_TB_IMG_BY_ID + "?trademarkId=" + trademarkId;

    }



}
