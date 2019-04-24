package com.example.newproject.web.core;

import com.example.newproject.web.domain.TrademarkDomain;

import java.util.ArrayList;
import java.util.List;

public class TrademarkSet {

    private static List<TrademarkDomain> trademarkDomains;

    public static List<TrademarkDomain> getTrademarkDomains() {
        if (trademarkDomains == null) {
            trademarkDomains = new ArrayList<TrademarkDomain>();
        }
        return trademarkDomains;
    }

    public static void setTrademarkDomains(List<TrademarkDomain> trademarkDomains) {

        TrademarkSet.trademarkDomains = trademarkDomains;
    }

    public static void addTrademarkDomain(TrademarkDomain trademarkDomain){

        if (trademarkDomains == null) {
            trademarkDomains = new ArrayList<TrademarkDomain>();
        }

        trademarkDomains.add(trademarkDomain);
    }

    public static TrademarkDomain getTrademarkDomainById(int trademarkId) {

        if (trademarkDomains == null) {
            trademarkDomains = new ArrayList<TrademarkDomain>();
        }

        for (TrademarkDomain trademarkDomain : trademarkDomains) {
            if (trademarkDomain.getTrademarkId() == trademarkId) {
                return trademarkDomain;
            }
        }
        return null;
    }

}
