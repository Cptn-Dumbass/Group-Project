package com.example.ael.Controllers.Inventory.Product;

import com.example.ael.Controllers.Orders.OrdersEdit;
import com.example.ael.Main;
import com.example.ael.Utility.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.sql.*;

public class ProductsRemapSKU {
    @FXML
    private Label prodIDLabel;

    @FXML
    private TableView table_SKUs;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private TableColumn<AssocSKUDataModel, String> SKUColumn;

    @FXML
    private TableColumn<AssocSKUDataModel, Integer> AmountColumn;

    @FXML
    private TableColumn<AssocSKUDataModel, String> LocationColumn;

    @FXML
    private TextField text_SKU;

    @FXML
    private TextField text_Location;

    @FXML
    private TextField text_Amount;

    @FXML
    private Button removeButton;

    private String prodID = null;

    @FXML
    public void initialize() {
        prodID = (String) Main.getPersistentData();
        if(prodID != null) {
            String text = ("Current Product ID - " + (String) prodID);
            prodIDLabel.setText(text);
        }
        populateTable();
        table_SKUs.getSelectionModel().clearSelection();
        table_SKUs.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) { removeButton.setDisable(false); }
            else { removeButton.setDisable(true); }
        });
    }

    @FXML
    public void onAddButtonClick() {
        String SKU = text_SKU.getText();
        String Location = text_Location.getText();
        String Amount = text_Amount.getText();

        if(SKU.trim().isEmpty() || Location.trim().isEmpty() || Amount.trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please enter a value in both fields.").showAndWait();
            return;
        }

        try { Integer.parseInt(Amount); } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Please enter a valid integer for Amount").showAndWait();
            return;
        }

        ObservableList<AssocSKUDataModel> items = table_SKUs.getItems();
        items.add(new AssocSKUDataModel(SKU, Integer.parseInt(Amount), Location));
        
        table_SKUs.refresh();
        text_SKU.clear();
        text_Location.clear();
    }

    @FXML
    public void onRemoveButtonClick() {
        Object item = table_SKUs.getSelectionModel().getSelectedItem();
        table_SKUs.getItems().remove(item);
    }

    @FXML
    public void onSaveButtonClick() {
        Boolean success = saveRecord();
        if(success) {
            new Alert(Alert.AlertType.INFORMATION,"Remap Saved").showAndWait();
            closeWindow();
        }
        //on fail, just leave window open - error reporting left to saverecord
    }

    private boolean saveRecord() {
        ObservableList<AssocSKUDataModel> items = table_SKUs.getItems(); //since we validate input in add, trusting all values in this to be valid
        String deleteQuery = ("DELETE FROM ProductSKU WHERE ProductID = '" + prodID + "' ");
        String insertQuery = null;
        if (items.size() > 0) {
            insertQuery = ("INSERT INTO ProductSKU(SKU, ProductID, Amount, WarehouseLocation) VALUES ");
            for (int i = 0; i < items.size(); i++) {
                AssocSKUDataModel item = items.get(i);
                insertQuery += ("('" + item.SKU + "', '" + prodID + "', " + item.Amount + ", '" + item.WarehouseLocation + "')");
                deleteQuery += (" AND SKU NOT LIKE '"+item.SKU+"'");
                if(i == (items.size()-1)) {
                    insertQuery += (" ON DUPLICATE KEY UPDATE Amount=VALUES(Amount), WarehouseLocation=VALUES(WarehouseLocation);");
                }
                else {
                    insertQuery += ", ";
                }
            }
        }
        deleteQuery += ";";

        Connection connection = DatabaseManager.connect();
        try {
            connection.setAutoCommit(false);

            if (deleteQuery != null) {
                connection.prepareStatement(deleteQuery).execute();
            }
            if (insertQuery != null) {
                connection.prepareStatement(insertQuery).execute();
            }

            connection.commit();
        } catch (SQLIntegrityConstraintViolationException e) {
          e.printStackTrace();
          new Alert(Alert.AlertType.ERROR, "Error deleting SKU - selected SKU is present in existing orders. Please remove selected SKU from orders before deleting.").showAndWait();
          closeWindow();
        } catch (SQLException e) {
            e.printStackTrace();
            try { connection.rollback(); } catch (SQLException e2) { new Alert(Alert.AlertType.ERROR, "Error rolling back changes."); }
        } finally {
            DatabaseManager.closeConnection(connection);
        }

        return true;
    }

    private void populateTable() {
        SKUColumn.setCellValueFactory(new PropertyValueFactory<>("SKU"));
        AmountColumn.setCellValueFactory(new PropertyValueFactory<>("Amount"));
        LocationColumn.setCellValueFactory(new PropertyValueFactory<>("WarehouseLocation"));

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
