package com.example.ael;

import com.fasterxml.jackson.annotation.JsonAlias;


public class AmazonOrderItem {

    @JsonAlias({"AmazonOrderId", "orderId"})
    private String AmazonOrderId;

    @JsonAlias({"sku", "SellerSKU"})
    private String sku;

    @JsonAlias({"QuantityOrdered", "quantity"})
    private String quantity;

    public  AmazonOrderItem(){
        super();
    }

    public String getAmazonOrderId() {
        return AmazonOrderId;
    }

    public String getSku() {
        return sku;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setAmazonOrderId(String amazonOrderId) {
        this.AmazonOrderId = amazonOrderId;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
