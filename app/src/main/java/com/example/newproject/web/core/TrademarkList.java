package com.example.newproject.web.core;

import com.example.newproject.web.domain.TrademarkListDomain;

import java.util.List;

public class   TrademarkList {

    private static List<TrademarkListDomain> trademarkListDomains;

    private static boolean dataAcquisitionCompleted = false;

    public static List<TrademarkListDomain> getTrademarkListDomains() {
        return trademarkListDomains;
    }

    public static void setTrademarkListDomains(List<TrademarkListDomain> trademarkListDomains) {
        TrademarkList.trademarkListDomains = trademarkListDomains;
        changeToTrademarkListDataAcquisitionSuccess();
    }

    private static void changeToTrademarkListDataAcquisitionSuccess(){
        dataAcquisitionCompleted = true;
    }

    public static void cleanTrademarkListData(){
        dataAcquisitionCompleted = false;
        trademarkListDomains = null;
    }

    public static boolean isDataAcquisitionCompleted() {
        return dataAcquisitionCompleted;
    }


    public static TrademarkListDomain getTrademarkListDomainByTrademarkName(String trademarkName) {
        for (TrademarkListDomain trademarkListDomain : trademarkListDomains) {
            if (trademarkListDomain.getTrademarkName().equals(trademarkName)) {
                return trademarkListDomain;
            }
        }
        return null;

    }
}
