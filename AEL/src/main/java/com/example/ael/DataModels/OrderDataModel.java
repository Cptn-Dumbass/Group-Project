package com.example.ael.DataModels;

import com.example.ael.Controllers.Orders.OrdersTrainee;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class OrderDataModel {
    private BooleanProperty selected;
    private String reference;
    private String status;
    private String receivedDate;
    private String source;
    private String subSource;
    private String extRef;
    private String channelRef;
    private String paymentMethod;
    private String purchasePrice;
    private String processedDate;

    public OrderDataModel(String reference, String status, String receivedDate, String source, String subSource, String extRef, String channelRef, String paymentMethod, String purchasePrice, String processedDate) {
        this.selected = new SimpleBooleanProperty(false);
        this.reference = reference;
        this.status = status;
        this.receivedDate = receivedDate;
        this.source = source;
        this.subSource = subSource;
        this.extRef = extRef;
        this.channelRef = channelRef;
        this.paymentMethod = paymentMethod;
        this.purchasePrice = purchasePrice;
        this.processedDate = processedDate;
    }

    public BooleanProperty isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(String receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSubSource() {
        return subSource;
    }

    public void setSubSource(String subSource) {
        this.subSource = subSource;
    }

    public String getExtRef() {
        return extRef;
    }

    public void setExtRef(String extRef) {
        this.extRef = extRef;
    }

    public String getChannelRef() {
        return channelRef;
    }

    public void setChannelRef(String channelRef) {
        this.channelRef = channelRef;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(String purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(String processedDate) {
        this.processedDate = processedDate;
    }
}
