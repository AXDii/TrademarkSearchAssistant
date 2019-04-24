package com.example.newproject.client.ui.trademarkSetActivitys;

public class TrademarkInfo {

    private String trademarkName;
//    private String trademarkNameEU;
    private String trademarkDescription;

    public TrademarkInfo(String trademarkName, String trademarkDescription) {
        this.trademarkName = trademarkName;
        this.trademarkDescription = trademarkDescription;
    }

    public String getTrademarkName() {
        return trademarkName;
    }

    public void setTrademarkName(String trademarkName) {
        this.trademarkName = trademarkName;
    }

    public String getTrademarkDescription() {
        return trademarkDescription;
    }

    public void setTrademarkDescription(String trademarkDescription) {
        this.trademarkDescription = trademarkDescription;
    }


}
