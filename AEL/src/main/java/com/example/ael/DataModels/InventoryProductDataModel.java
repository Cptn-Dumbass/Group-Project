package com.example.ael.DataModels;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class InventoryProductDataModel {
    private final SimpleIntegerProperty productID;
    private final SimpleStringProperty categoryName;
    private final SimpleStringProperty title;
    private final SimpleIntegerProperty weight;
    private final SimpleDoubleProperty unitCost;
    private final SimpleIntegerProperty minLevel;
    private final SimpleIntegerProperty currentLevel;

    public InventoryProductDataModel(int productID, String categoryName, String title, int weight, double unitCost, int minLevel, int currentLevel) {
        this.productID = new SimpleIntegerProperty(productID);
        this.categoryName = new SimpleStringProperty(categoryName);
        this.title = new SimpleStringProperty(title);
        this.weight = new SimpleIntegerProperty(weight);
        this.unitCost = new SimpleDoubleProperty(unitCost);
        this.minLevel = new SimpleIntegerProperty(minLevel);
        this.currentLevel = new SimpleIntegerProperty(currentLevel);
    }

    public int getProductID() {
        return productID.get();
    }

    public SimpleIntegerProperty productIDProperty() {
        return productID;
    }

    public String getCategoryName() {
        return categoryName.get();
    }

    public SimpleStringProperty categoryNameProperty() {
        return categoryName;
    }

    public String getTitle() {
        return title.get();
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public int getWeight() {
        return weight.get();
    }

    public SimpleIntegerProperty weightProperty() {
        return weight;
    }

    public double getUnitCost() {
        return unitCost.get();
    }

    public SimpleDoubleProperty unitCostProperty() {
        return unitCost;
    }

    public int getMinLevel() {
        return minLevel.get();
    }

    public SimpleIntegerProperty minLevelProperty() {
        return minLevel;
    }

    public int getCurrentLevel() {
        return currentLevel.get();
    }

    public SimpleIntegerProperty currentLevelProperty() {
        return currentLevel;
    }
}
