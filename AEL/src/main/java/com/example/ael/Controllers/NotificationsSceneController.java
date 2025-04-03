package com.example.ael.Controllers;

import com.example.ael.DataModels.NotificationDataModel;
import com.example.ael.Main;
import com.example.ael.Utility.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NotificationsSceneController {

    @FXML
    public void onDashboardButtonClick() {
        Main.changeScene("Dashboard.fxml");
    }

    @FXML
    protected void onOrdersButtonClick() {
        Main.changeScene("orders-view-admin.fxml");
    }

    @FXML
    protected void onAlertsButtonClick() {
        Main.changeScene("AlertScene.fxml");
    }

    @FXML
    protected void onNotificationsButtonCLick() {
        Main.changeScene("NotificationsScene.fxml");
    }

    @FXML
    protected void onInventoryButtonClick( ) {
        Main.changeScene("inventory-products.fxml");
    }

    @FXML
    protected void onRestockButtonClick( ) {
        Main.changeScene("Restock.fxml");
    }

    @FXML
    private TableView<NotificationDataModel> notificationTable;

    @FXML
    private TableColumn<NotificationDataModel, String> descriptionColumn;

    @FXML
    private TableColumn<NotificationDataModel, String> priorityColumn;

    @FXML
    private TableColumn<NotificationDataModel, String> dateTimeColumn;

    @FXML
    public void initialize() {
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        dateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("dateTimeReceived"));

        fetchAndPopulateData();
    }

    private void fetchAndPopulateData() {
        ObservableList<NotificationDataModel> notifDataList = FXCollections.observableArrayList();

        // Replace these placeholders with your actual table and column names
        String query = "SELECT * FROM Notifications;";

        try (Connection connection = DatabaseManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String description = resultSet.getString("Description");
                String priority = resultSet.getString("Priority");
                String dateTimeReceived = resultSet.getString("ReceivedDate");

                NotificationDataModel notificationDataModel = new NotificationDataModel(description, priority, dateTimeReceived);
                notifDataList.add(notificationDataModel);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        notificationTable.setItems(notifDataList);
    }
}
