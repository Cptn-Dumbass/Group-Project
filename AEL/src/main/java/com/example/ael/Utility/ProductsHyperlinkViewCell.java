package com.example.ael.Utility;

import com.example.ael.DataModels.InventoryProductDataModel;

public class ProductsHyperlinkViewCell<T> extends HyperlinkViewCell<T> {
    public ProductsHyperlinkViewCell(String screenName) {
        super(screenName);
    }

    protected String getDataToSend() {
        InventoryProductDataModel productData = (InventoryProductDataModel) getTableRow().getItem();
        return (productData != null) ? String.valueOf(productData.getProductID()) : null;
    }
}