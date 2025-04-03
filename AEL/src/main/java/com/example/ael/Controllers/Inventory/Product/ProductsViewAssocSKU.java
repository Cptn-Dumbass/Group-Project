package com.example.ael.Controllers.Inventory.Product;

import com.example.ael.Controllers.Orders.OrdersViewItems;
import com.example.ael.Main;
import com.example.ael.Utility.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductsViewAssocSKU {
    @FXML
    private Label prodIDLabel;

    @FXML
    private TableView table_SKUs;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private TableColumn<AssocSKUDataModel, String> SKUColumn;

    @FXML
    private TableColumn<AssocSKUDataModel, String> LocationColumn;

    @FXML
    private TableColumn<AssocSKUDataModel, Integer> AmountColumn;

    @FXML
    public void initialize() {
        String prodID = Main.getPersistentData().toString();
        if(prodID != null) {
            String text = ("Current Product ID - " + (String) prodID);
            prodIDLabel.setText(text);
        }
        populateTable(prodID);
    }

    private void populateTable(String prodID) {
        SKUColumn.setCellValueFactory(new PropertyValueFactory<>("SKU"));
        LocationColumn.setCellValueFactory(new PropertyValueFactory<>("WarehouseLocation"));
        AmountColumn.setCellValueFactory(new PropertyValueFactory<>("Amount"));

        ObservableList<AssocSKUDataModel> items = FXCollections.observableArrayList();
        Connection connection = DatabaseManager.connect();

        String query = ("SELECT ProductSKU.SKU, WarehouseLocation, Amount FROM ProductSKU WHERE ProductID LIKE '"+prodID+"';");

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String SKU = resultSet.getString("SKU");
                    String Location = resultSet.getString("WarehouseLocation");
                    Integer Amount = resultSet.getInt("Amount");

                    AssocSKUDataModel item = new AssocSKUDataModel(SKU, Amount, Location);

                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closeConnection(connection);
        }

        table_SKUs.setItems(items);
    }

    @FXML
    public void onExitButtonClick() { closeWindow(); }

    private void closeWindow() {
        Stage currentStage = (Stage) mainPane.getScene().getWindow();
        currentStage.close();
    }

    protected class AssocSKUDataModel {
        public AssocSKUDataModel(String SKU, Integer amount, String warehouseLocation) {
            this.SKU = SKU;
            this.Amount = amount;
            this.WarehouseLocation = warehouseLocation;
        }

        String SKU;

        public Integer getAmount() {
            return Amount;
        }

        public void setAmount(Integer amount) {
            Amount = amount;
        }

        Integer Amount;
        String WarehouseLocation;

        public String getSKU() {
            return SKU;
        }

        public void setSKU(String SKU) {
            this.SKU = SKU;
        }

        public String getWarehouseLocation() {
            return WarehouseLocation;
        }

        public void setWarehouseLocation(String warehouseLocation) {
            WarehouseLocation = warehouseLocation;
        }
    }
}
