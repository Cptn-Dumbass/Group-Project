package com.example.ael;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BuyerInfo {

    @JsonProperty("BuyerEmail")
    private String BuyerEmail;

    @JsonProperty("BuyerName")
    private String BuyerName;

    BuyerInfo(String buyerEmail, String buyerName){
        super();
        BuyerEmail = buyerEmail;
        BuyerName = buyerName;
    }

    public String getBuyerEmail() {
        return BuyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        BuyerEmail = buyerEmail;
    }

    public String getBuyerName() {
        return BuyerName;
    }

    public void setBuyerName(String buyerName) {
        BuyerName = buyerName;
    }
}
