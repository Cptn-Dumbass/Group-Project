package com.example.ael.Controllers;

import com.example.ael.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class ReportsController {
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
}
