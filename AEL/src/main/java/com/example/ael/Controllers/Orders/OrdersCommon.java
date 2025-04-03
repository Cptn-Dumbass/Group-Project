package com.example.ael.Controllers.Orders;

import com.example.ael.DataModels.OrderDataModel;
import com.example.ael.Main;
import com.example.ael.Utility.DatabaseManager;
import com.example.ael.Utility.OrdersHyperlinkViewCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public abstract class OrdersCommon {
    @FXML
    protected TableView<OrderDataModel> orderTable;

    @FXML
    protected TableColumn<OrderDataModel, Integer> refColumn;

    @FXML
    protected TableColumn<OrderDataModel, Integer> statusColumn;

    @FXML
    protected TableColumn<OrderDataModel, Integer> receivedDateColumn;

    @FXML
    protected TableColumn<OrderDataModel, Integer> sourceColumn;

    @FXML
    protected TableColumn<OrderDataModel, String> subSourceColumn;

    @FXML
    protected TableColumn<OrderDataModel, String> externalRefColumn;

    @FXML
    protected TableColumn<OrderDataModel, String> channelRefColumn;

    @FXML
    protected TableColumn<OrderDataModel, String> paymentMethodColumn;

    @FXML
    protected TableColumn<OrderDataModel, String> purchasePriceColumn;

    @FXML
    protected TableColumn<OrderDataModel, String> processedDateColumn;

    @FXML
    protected TableColumn<OrderDataModel, Void> postageColumn;

    @FXML
    protected TableColumn<OrderDataModel, Void> itemsColumn;

    @FXML
    protected TableColumn<OrderDataModel, Void> custColumn;

    @FXML
    protected void onDashboardButtonClick() { Main.changeScene("Dashboard.fxml"); }

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
    protected void onSettingsButtonClick() {
        Main.changeScene("Settings.fxml");
    }

    @FXML
    protected void onExitButtonClick() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to exit?");
        Optional<ButtonType> decision = alert.showAndWait();
        if(decision.get() == ButtonType.OK) {
            System.exit(0);
        }
    }

    protected void refreshTable() {
        refreshTable(null);
    }

    protected void refreshTable(String hideStatusExcept){
        refColumn.setCellValueFactory(new PropertyValueFactory<>("reference"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        receivedDateColumn.setCellValueFactory(new PropertyValueFactory<>("receivedDate"));
        sourceColumn.setCellValueFactory(new PropertyValueFactory<>("source"));
        subSourceColumn.setCellValueFactory(new PropertyValueFactory<>("SubSource"));
        externalRefColumn.setCellValueFactory(new PropertyValueFactory<>("extRef"));
        channelRefColumn.setCellValueFactory(new PropertyValueFactory<>("channelRef"));
        paymentMethodColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        purchasePriceColumn.setCellValueFactory(new PropertyValueFactory<>("purchasePrice"));
        processedDateColumn.setCellValueFactory(new PropertyValueFactory<>("processedDate"));

        postageColumn.setCellFactory(tc -> new OrdersHyperlinkViewCell<>("orders-viewPostage.fxml"));
        itemsColumn.setCellFactory(tc -> new OrdersHyperlinkViewCell<>("orders-viewItems.fxml"));
        custColumn.setCellFactory(tc -> new OrdersHyperlinkViewCell<>("orders-viewCust.fxml"));

        fetchOrdersFromDatabase(hideStatusExcept);

        orderTable.refresh();
    }

    protected void fetchOrdersFromDatabase() {
        fetchOrdersFromDatabase(null);
    }

    protected void fetchOrdersFromDatabase(String hideStatusExcept) {
        ObservableList<OrderDataModel> items = FXCollections.observableArrayList();

        Connection connection = DatabaseManager.connect();

        String query = "SELECT * FROM Orders;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {

                    String reference = resultSet.getString("ReferenceNum");
                    String status = resultSet.getString("Stage");
                    String source = resultSet.getString("Source");
                    String subsource = resultSet.getString("Subsource");
                    String externalReference = resultSet.getString("ExternalReference");
                    String channelReference = resultSet.getString("ChannelReference");
                    String receivedDate = resultSet.getString("ReceivedDate");
                    String paymentMethod = resultSet.getString("PaymentMethod");
                    String purchasePrice = resultSet.getString("PurchasePrice");
                    String processedDate = resultSet.getString("ProcessedDate");

                    OrderDataModel orderDataModel = new OrderDataModel(reference, status, receivedDate, source, subsource, externalReference, channelReference, paymentMethod, purchasePrice, processedDate);

                    if (hideStatusExcept != null) {
                        if(status.equals(hideStatusExcept)) { items.add(orderDataModel); }
                    } else { items.add(orderDataModel); }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closeConnection(connection);
        }

        orderTable.setItems(items);
    }
}
