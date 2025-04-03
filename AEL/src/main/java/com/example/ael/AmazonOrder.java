package com.example.ael;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AmazonOrder {

    //TODO -decide weather to rename this class to a non amazon specific name
    //TODO -Replace annotations with JsonAlias, but other stuff takes precedent
    @JsonProperty("AmazonOrderId")
    private String AmazonOrderId;

    @JsonProperty("orderFulfillmentStatus")
    private String OrderStatus;

    @JsonProperty("Amount")
    private String Amount;

    @JsonProperty("PaymentMethod")
    private String PaymentMethod;

    @JsonProperty("PurchaseDate")
    private String PurchaseDate;

    @JsonProperty("SalesChannel")
    private String SalesChannel;

    @JsonProperty("BuyerInfo")
    private BuyerInfo buyerInfo;

    private AmazonOrderItem[] item;

    public AmazonOrder(){
        super();
    }

    public String getAmazonOrderId() {
        return AmazonOrderId;
    }

    public String getOrderStatus() {
        return OrderStatus;
    }

    public String getPurchaseDate() {
        return PurchaseDate;
    }

    public String getAmount() {
        return Amount;
    }

    public String getPaymentMethod() {
        return PaymentMethod;
    }

    public String getSalesChannel() {
        return SalesChannel;
    }

    public BuyerInfo getBuyerInfo() {
        return buyerInfo;
    }

    public void setBuyerInfo(BuyerInfo buyerInfo) {
        this.buyerInfo = buyerInfo;
    }

    public AmazonOrderItem[] getItem() {
        return item;
    }

    public void setItem(AmazonOrderItem[] item) {
        this.item = item;
    }

    public void setAmazonOrderId(String amazonOrderId) {
        AmazonOrderId = amazonOrderId;
    }

    public void setOrderStatus(String orderStatus) {
        OrderStatus = orderStatus;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public void setPaymentMethod(String paymentMethod) {
        PaymentMethod = paymentMethod;
    }

    public void setPurchaseDate(String purchaseDate) {
        PurchaseDate = purchaseDate;
    }

    public void setSalesChannel(String salesChannel) {
        SalesChannel = salesChannel;
    }
}
