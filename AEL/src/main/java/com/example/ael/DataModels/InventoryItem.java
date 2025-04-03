package com.example.ael.DataModels;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class InventoryItem {

    private IntegerProperty inventoryId;
    private IntegerProperty productId;
    private StringProperty SKU;
    private StringProperty location;
    private IntegerProperty level;
    private IntegerProperty minLevel;

    public InventoryItem(int inventoryId, int productId, String SKU,
                  String location, int level, int minLevel){
        inventoryIdProperty().set(inventoryId);
        productIdProperty().set(productId);
        SKUProperty().set(SKU);
        locationProperty().set(location);
        levelProperty().set(level);
        minLevelProperty().set(minLevel);
    }

    public int getInventoryId() {
        return inventoryId.get();
    }
    public void setInventoryId(int inventoryId) {
        this.inventoryId.set(inventoryId);
    }

    public IntegerProperty inventoryIdProperty() {
        if (inventoryId == null) inventoryId = new SimpleIntegerProperty(this, "inventoryId");
        return inventoryId;
    }

    public int getProductId() {
        return productId.get();
    }

    public void setProductId(int productId) {
        this.productId.set(productId);
    }

    public IntegerProperty productIdProperty() {
        if (productId == null) productId = new SimpleIntegerProperty(this, "productId");
        return productId;
    }

    public String getSKU() {
        return SKU.get();
    }

    public void setSKU(String SKU) {
        this.SKU.set(SKU);
    }

    public StringProperty SKUProperty() {
        if (SKU == null) SKU = new SimpleStringProperty(this, "SKU");
        return SKU;
    }

    public String getLocation() {
        return location.get();
    }

    public void setLocation(String location) {
        this.location.set(location);
    }

    public StringProperty locationProperty() {
        if (location == null) location = new SimpleStringProperty(this, "location");
        return location;
    }

    public int getLevel() {
        return level.get();
    }

    public void setLevel(int level) {
        this.level.set(level);
    }

    public IntegerProperty levelProperty() {
        if (level == null) level = new SimpleIntegerProperty(this, "level");
        return level;
    }

    public int getMinLevel() {
        return minLevel.get();
    }

    public void setMinLevel(int minLevel) {
        this.minLevel.set(minLevel);
    }

    public IntegerProperty minLevelProperty() {
        if (minLevel == null) minLevel = new SimpleIntegerProperty(this, "minLevel");
        return minLevel;
    }
}