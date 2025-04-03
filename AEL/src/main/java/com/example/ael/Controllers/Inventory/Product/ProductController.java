package com.example.ael.Controllers.Inventory.Product;

import com.example.ael.DataModels.InventoryProductDataModel;
import com.example.ael.DataModels.OrderDataModel;
import com.example.ael.Main;
import com.example.ael.Utility.DatabaseManager;
import com.example.ael.Utility.ProductsHyperlinkViewCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ProductController {
    @FXML
    public void onDashboardButtonClick() { Main.changeScene("Dashboard.fxml"); }

    @FXML
    protected void onOrdersButtonClick() {
        Main.changeScene("orders-view-admin.fxml");
    }

    @FXML
    protected void onInventoryButtonClick( ) {
        Main.changeScene("inventory-products.fxml");
    }

    @FXML
    protected void onAlertsButtonClick() {
        Main.changeScene("AlertScene.fxml");
    }

    @FXML
    protected void onReportsButtonClick() {
        Main.changeScene("reports-view.fxml");
    }

    @FXML
    protected void onSettingsButtonClick() { Main.changeScene("Settings.fxml"); }

    @FXML
    protected void onExitButtonClick() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to exit?");
        Optional<ButtonType> decision = alert.showAndWait();
        if(decision.get() == ButtonType.OK) {
            System.exit(0);
        }
    }

    @FXML
    private TableView<InventoryProductDataModel> inventoryTable;

    @FXML
    private TableColumn<InventoryProductDataModel, Integer> productIDColumn;

    @FXML
    private TableColumn<InventoryProductDataModel, String> categoryNameColumn;

    @FXML
    private TableColumn<InventoryProductDataModel, String> titleColumn;

    @FXML
    private TableColumn<InventoryProductDataModel, Integer> weightColumn;

    @FXML
    private TableColumn<InventoryProductDataModel, Double> unitCostColumn;

    @FXML
    private TableColumn<InventoryProductDataModel, Integer> minLevelColumn;

    @FXML
    private TableColumn<InventoryProductDataModel, Integer> currentLevelColumn;

    @FXML
    private TableColumn<InventoryProductDataModel, Void> assocSKUColumn;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    @FXML
    private Button remapSKUButton;

    @FXML
    protected void onAddButtonClick() {
        Main.popUpModalScreen("inventory-products-add.fxml");
        initialize();
    }

    @FXML
    protected void onEditButtonClick() {
        Integer index = inventoryTable.getSelectionModel().getFocusedIndex();
        String productID = String.valueOf(((InventoryProductDataModel) inventoryTable.getItems().get(index)).getProductID());
        Main.setPersistentData(productID);
        Main.popUpModalScreen("inventory-products-edit.fxml");
        initialize();
    }

    @FXML
    protected void onDeleteButtonClick() {
        Integer prodID = inventoryTable.getSelectionModel().getSelectedItem().getProductID();
        Connection connection = DatabaseManager.connect();
        try {
            String alertText = "";
            boolean orderItemsPresent = false, restocksPresent= false, SKUPresent = false;
            ResultSet rs;

            rs = connection.prepareStatement("SELECT SKU FROM ProductSKU WHERE ProductID="+prodID+";").executeQuery();
            if (rs.next()) {
                SKUPresent = true;
                alertText += ("Product #"+prodID+" has associated SKUs, and deleting it will remove those SKUs.\n");
                rs = connection.prepareStatement("SELECT * FROM OrderItems WHERE SKU IN (SELECT SKU FROM ProductSKU WHERE ProductID="+prodID+");").executeQuery();
                if (rs.next()) {
                    orderItemsPresent = true;
                    alertText += ("Product #"+prodID+" is present in one of more orders, and deleting it will remove it from those orders.\n");
                }
            }
            rs = connection.prepareStatement("SELECT * FROM Restocks WHERE ProductID="+prodID+";").executeQuery();
            if (rs.next()) {
                alertText += ("Product #"+prodID+" has associated restocks, and deleting it will remove those Restocks.\n");
                restocksPresent = true;
            }
            alertText += "Are you sure you want to delete Product #"+prodID+"?";

            String productDelete = ("DELETE FROM Products WHERE ProductID="+prodID+";");

            Optional<ButtonType> decision = new Alert(Alert.AlertType.CONFIRMATION, alertText).showAndWait();
            if (decision.get().equals(ButtonType.OK)) {
                if(orderItemsPresent || restocksPresent || SKUPresent) {
                    connection.setAutoCommit(false);

                    String restocksDelete = ("DELETE FROM Restocks WHERE ProductID="+prodID+";");
                    String orderItemsDelete = ("DELETE FROM OrderItems WHERE SKU IN (SELECT SKU FROM ProductSKU WHERE ProductID="+prodID+");");
                    String productSKUDelete = ("DELETE FROM ProductSKU WHERE ProductID="+prodID+";");

                    if (orderItemsPresent) { connection.prepareStatement(orderItemsDelete).execute(); }
                    if (SKUPresent) { connection.prepareStatement(productSKUDelete).execute(); }
                    if (restocksPresent) { connection.prepareStatement(restocksDelete).execute(); }

                    connection.prepareStatement(productDelete).execute();
                    connection.commit();
                }
                else {
                    connection.prepareStatement(productDelete).execute();
                }
                new Alert(Alert.AlertType.INFORMATION, "Product deleted successfully");
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error deleting record.").showAndWait();
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException error2) {
                new Alert(Alert.AlertType.ERROR, "Error rolling back changes.").showAndWait();
                error2.printStackTrace();
            }
        } finally {
            DatabaseManager.closeConnection(connection);
            initialize();
        }
    }

    @FXML
    protected void onRemapSKUButtonClick() {
        Integer index = inventoryTable.getSelectionModel().getFocusedIndex();
        String productID = String.valueOf(((InventoryProductDataModel) inventoryTable.getItems().get(index)).getProductID());
        Main.setPersistentData(productID);
        Main.popUpModalScreen("inventory-products-remapAssocSKU.fxml");
        initialize();
    }

    @FXML
    protected void onProductButtonClick() {
        Main.changeScene("inventory-products.fxml");
    }

    @FXML
    protected void onRestockButtonClick() { Main.changeScene("inventory-restocks.fxml"); }

    @FXML
    public void initialize() {
        refreshTable();
    }

    private void refreshTable() {
        productIDColumn.setCellValueFactory(new PropertyValueFactory<>("productID"));
        categoryNameColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        unitCostColumn.setCellValueFactory(new PropertyValueFactory<>("unitCost"));
        minLevelColumn.setCellValueFactory(new PropertyValueFactory<>("minLevel"));
        currentLevelColumn.setCellValueFactory(new PropertyValueFactory<>("currentLevel"));

        assocSKUColumn.setCellFactory(tc -> new ProductsHyperlinkViewCell<>("inventory-products-assocSKU.fxml"));

        fetchDataFromDatabase();

        inventoryTable.getSelectionModel().clearSelection();
        inventoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                editButton.setDisable(false);
                deleteButton.setDisable(false);
                remapSKUButton.setDisable(false);
            }
            else {
                editButton.setDisable(true);
                deleteButton.setDisable(true);
                remapSKUButton.setDisable(true);
            }
        });
    }

    private void fetchDataFromDatabase() {
        ObservableList<InventoryProductDataModel> inventoryData =FXCollections.observableArrayList();

        final String currentLevelQuery = "SELECT Products.*, Categories.CategoryName, ProductTotal.ProductID, IFNULL(RestocksTotal.RestocksAdded-ProductTotal.TotalBought, 0) as CurrentTotal "
                                       + "FROM ( SELECT Products.ProductID, IFNULL(SUM(ProductSKUTotal.Total), 0) AS TotalBought "
                                       + "FROM ( SELECT ProductSKU.SKU, ProductSKU.ProductID, QuantityPerSKU.TotalQuantity * ProductSKU.Amount AS Total "
                                       + "FROM ( SELECT SUM(Quantity) AS TotalQuantity, SKU FROM OrderItems GROUP BY SKU ) as QuantityPerSKU "
                                       + "INNER JOIN ProductSKU ON QuantityPerSKU.SKU = ProductSKU.SKU ) as ProductSKUTotal "
                                       + "RIGHT JOIN Products ON ProductSKUTotal.ProductID=Products.ProductID GROUP BY Products.ProductID ) as ProductTotal "
                                       + "LEFT JOIN ( SELECT ProductID, SUM(Quantity) as RestocksAdded FROM Restocks GROUP BY ProductID) as RestocksTotal "
                                       + "ON RestocksTotal.ProductID=ProductTotal.ProductID "
                                       + "INNER JOIN Products ON ProductTotal.ProductID=Products.ProductID "
                                       + "INNER JOIN Categories ON Products.CategoryID=Categories.CategoryID "
                                       + "ORDER BY Products.ProductID;";

        try (Connection connection = DatabaseManager.connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(currentLevelQuery)) {
            while (resultSet.next()) {
                int productID = resultSet.getInt("Products.ProductID");
                String category = resultSet.getString("CategoryName");
                String title = resultSet.getString("Title");
                Double unitCost = resultSet.getDouble("UnitCost");
                int weight = resultSet.getInt("Weight");
                int currentLevel = resultSet.getInt("CurrentTotal");
                int minLevel = resultSet.getInt("MinLevel");

                InventoryProductDataModel inventory = new InventoryProductDataModel(productID, category, title, weight, unitCost, minLevel, currentLevel);
                inventoryData.add(inventory);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        inventoryTable.setItems(inventoryData);
    }
}