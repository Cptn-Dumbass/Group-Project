package com.example.ael.Utility;

import com.example.ael.DataModels.OrderDataModel;

public class OrdersHyperlinkViewCell<T> extends HyperlinkViewCell<T> {
    public OrdersHyperlinkViewCell(String screenName) {
        super(screenName);
    }

    protected String getDataToSend() {
        OrderDataModel orderData = (OrderDataModel) getTableRow().getItem();
        return (orderData != null) ? orderData.getReference() : null;
    }
}
