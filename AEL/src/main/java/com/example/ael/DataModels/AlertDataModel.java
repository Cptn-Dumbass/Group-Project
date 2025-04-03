package com.example.ael.DataModels;

public class AlertDataModel {
    private Double productID;
    private String Quantity;
    private Double Cost;
    private String purchaseDate;

    public AlertDataModel(Double productID, String quantity, Double cost, String purchaseDate) {
        this.productID = productID;
        this.Quantity = quantity;
        this.Cost = cost;
        this.purchaseDate = purchaseDate;
    }

    // Getter and setter methods

    public Double getProductID() {
        return productID;
    }

    public void setProductID(Double productID) {
        this.productID = productID;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        this.Quantity = quantity;
    }

    public Double getCost() {
        return Cost;
    }

    public void setPriority (Double cost) {
        this.Cost = cost;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}
