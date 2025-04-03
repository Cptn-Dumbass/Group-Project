package com.example.ael.Controllers.Orders;

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

public class OrdersViewItems {
    @FXML
    private AnchorPane mainPane;

    @FXML
    private Label refLabel;

    @FXML
    private TableView table_OrderItems;

    @FXML
    private TableColumn<OrderItemDataModel, String> SKUColumn;

    @FXML
    private TableColumn<OrderItemDataModel, Integer> QuantityColumn;

    @FXML
    protected void initialize(){
        Object reference = Main.getPersistentData();
        if(reference != null) {
            String text = ("Order Reference: " + (String) reference);
            refLabel.setText(text);
        }

        SKUColumn.setCellValueFactory(new PropertyValueFactory<>("SKU"));
        QuantityColumn.setCellValueFactory(new PropertyValueFactory<>("Quantity"));

        ObservableList<OrderItemDataModel> items = FXCollections.observableArrayList();
        Connection connection = DatabaseManager.connect();

        String query = ("SELECT * FROM OrderItems WHERE ReferenceNum LIKE '"+reference+"';");

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String SKU = resultSet.getString("SKU");
                    Integer Quantity = resultSet.getInt("Quantity");

                    OrderItemDataModel item = new OrderItemDataModel(SKU, Quantity);

                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception (show an error message, log the error, etc.)
        } finally {
            // Close the connection
            DatabaseManager.closeConnection(connection);
        }

        table_OrderItems.setItems(items);
    }

    @FXML
    protected void onCancelButtonClick() {
        closeWindow();
    }

    private void closeWindow() {
        Stage currentStage = (Stage) mainPane.getScene().getWindow();
        currentStage.close();
    }

    protected class OrderItemDataModel {
        public String getSKU() {
            return SKU;
        }

        public void setSKU(String SKU) {
            this.SKU = SKU;
        }

        public Integer getQuantity() {
            return Quantity;
        }

        public void setQuantity(Integer quantity) {
            Quantity = quantity;
        }

        String SKU;
        Integer Quantity;

        public OrderItemDataModel(String SKU, Integer Quantity) {
            this.SKU=SKU;
            this.Quantity=Quantity;
        }
    }
}
