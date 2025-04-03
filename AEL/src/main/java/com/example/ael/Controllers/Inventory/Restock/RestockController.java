package com.example.ael.Controllers.Inventory.Restock;

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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class RestockController {
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
    private TableView<RestockDataModel> restockTable;

    @FXML
    private TableColumn<RestockDataModel, Integer> restockIDColumn;

    @FXML
    private TableColumn<RestockDataModel, Integer> productIDColumn;

    @FXML
    private TableColumn<RestockDataModel, Integer> quantityColumn;

    @FXML
    private TableColumn<RestockDataModel, Double> totalCostColumn;

    @FXML
    private TableColumn<RestockDataModel, String> purchaseDateColumn;

    @FXML
    private TableColumn<RestockDataModel, String> supplierColumn;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    protected void onProductButtonClick() {
        Main.changeScene("inventory-products.fxml");
    }

    @FXML
    protected void onRestockButtonClick() { Main.changeScene("inventory-restocks.fxml"); }


    @FXML
    protected void onAddButtonClick() {
        Main.popUpModalScreen("inventory-restocks-add.fxml");
        initialize();
    }
    @FXML
    protected void onEditButtonClick() {
        Integer index = restockTable.getSelectionModel().getFocusedIndex();
        Integer restockID = ((RestockDataModel) restockTable.getItems().get(index)).getRestockID();
        Main.setPersistentData(restockID);
        Main.popUpModalScreen("inventory-restocks-edit.fxml");
        initialize();
    }
    @FXML
    protected void onDeleteButtonClick() {
        Integer restockID = restockTable.getSelectionModel().getSelectedItem().getRestockID();

        Optional<ButtonType> decision = new Alert(Alert.AlertType.CONFIRMATION, ("Are you sure you want to delete Restock #"+restockID+"?")).showAndWait();
        if (decision.get().equals(ButtonType.OK)) {
            Connection connection = DatabaseManager.connect();
            try {
                connection.prepareStatement(("DELETE FROM Restocks WHERE RestockID LIKE " + restockID + ";")).execute();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DatabaseManager.closeConnection(connection);
                initialize();
            }
        }
    }

    @FXML
    public void initialize() {
        refreshTable();
    }

    private void refreshTable() {
        restockIDColumn.setCellValueFactory(new PropertyValueFactory<>("RestockID"));
        productIDColumn.setCellValueFactory(new PropertyValueFactory<>("ProductID"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
        totalCostColumn.setCellValueFactory(new PropertyValueFactory<>("TotalCost"));
        purchaseDateColumn.setCellValueFactory(new PropertyValueFactory<>("PurchaseDate"));
        supplierColumn.setCellValueFactory(new PropertyValueFactory<>("Supplier"));

        fetchDataFromDatabase();

        restockTable.getSelectionModel().clearSelection();
        restockTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                editButton.setDisable(false);
                deleteButton.setDisable(false);
            }
            else {
                editButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });
    }

    private void fetchDataFromDatabase() {
        ObservableList<RestockDataModel> restockData = FXCollections.observableArrayList();
        Connection connection = DatabaseManager.connect();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Restocks");
            while (resultSet.next()) {
                int restockID = resultSet.getInt("RestockID");
                int productID = resultSet.getInt("ProductID");
                int quantity = resultSet.getInt("Quantity");
                Double totalCost = resultSet.getDouble("TotalCost");
                String purchaseDate = resultSet.getString("PurchaseDate");
                String supplier = resultSet.getString("Supplier");

                RestockDataModel item = new RestockDataModel(restockID, productID, quantity, totalCost, purchaseDate, supplier);
                restockData.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closeConnection(connection);
        }

        restockTable.setItems(restockData);
    }

    protected class RestockDataModel {
        Integer RestockID;
        Integer ProductID;
        Integer Quantity;
        Double TotalCost;
        String PurchaseDate;
        String Supplier;

        public RestockDataModel(Integer restockID, Integer productID, Integer quantity, Double totalCost, String purchaseDate, String supplier) {
            RestockID = restockID;
            ProductID = productID;
            Quantity = quantity;
            TotalCost = totalCost;
            PurchaseDate = purchaseDate;
            Supplier = supplier;
        }

        public Integer getRestockID() {
            return RestockID;
        }

        public void setRestockID(Integer restockID) {
            RestockID = restockID;
        }

        public Integer getProductID() {
            return ProductID;
        }

        public void setProductID(Integer productID) {
            ProductID = productID;
        }

        public Integer getQuantity() {
            return Quantity;
        }

        public void setQuantity(Integer quantity) {
            Quantity = quantity;
        }

        public Double getTotalCost() {
            return TotalCost;
        }

        public void setTotalCost(Double totalCost) {
            TotalCost = totalCost;
        }

        public String getPurchaseDate() {
            return PurchaseDate;
        }

        public void setPurchaseDate(String purchaseDate) {
            PurchaseDate = purchaseDate;
        }

        public String getSupplier() {
            return Supplier;
        }

        public void setSupplier(String supplier) {
            Supplier = supplier;
        }
    }
}
