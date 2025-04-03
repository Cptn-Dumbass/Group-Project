package com.example.ael.Controllers;

import com.example.ael.DataModels.AlertDataModel;
import com.example.ael.Main;
import com.example.ael.Utility.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class AlertSceneController {

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

    @FXML
    private TextField filterField;

    @FXML
    private TableView<AlertDataModel> alertTable;

    @FXML
    private TableColumn<AlertDataModel, Double> productIDColumn;

    @FXML
    private TableColumn<AlertDataModel, String> quantityColumn;

    @FXML
    private TableColumn<AlertDataModel, Double> costColumn;

    @FXML
    private TableColumn<AlertDataModel, String> purchaseDateColumn;

    @FXML
    public void initialize() {
        productIDColumn.setCellValueFactory(new PropertyValueFactory<>("productID"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
        costColumn.setCellValueFactory(new PropertyValueFactory<>("Cost"));
        purchaseDateColumn.setCellValueFactory(new PropertyValueFactory<>("purchaseDate"));


        // Fetch data from the database and populate the TableView
//        fetchAlertsFromDatabase();
    }

    private void fetchAlertsFromDatabase() {
        ObservableList<AlertDataModel> items = FXCollections.observableArrayList();

        // Establish database connection using DatabaseManager
        Connection connection = DatabaseManager.connect();

        // Replace "alerts" with your actual table name
        String query = "SELECT * FROM restocks";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Double productID = resultSet.getDouble("productID");
                    String quantity = resultSet.getString("quantity");
                    Double totalCost = resultSet.getDouble("totalCost");
                    String purchaseDate = resultSet.getString("purchaseDate");

                    AlertDataModel alertDataModel = new AlertDataModel(productID, quantity, totalCost, purchaseDate);

                    items.add(alertDataModel);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the connection
            DatabaseManager.closeConnection(connection);
        }

        alertTable.setItems(items);
    }
}
