package com.example.ael.Controllers;

import com.example.ael.AmazonRequest;
import com.example.ael.EbayJsonHandler;
import com.example.ael.EbayRequestHandler;
import com.example.ael.Main;
import com.sun.javafx.application.HostServicesDelegate;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

public class SettingsController {

    public javafx.scene.control.TextField authorisationBox;

    public javafx.scene.control.TextField accountNameField;

    public javafx.scene.control.TextField amazonBox;

    public javafx.scene.control.TextField clientBox;

    public javafx.scene.control.TextField secretBox;

    private EbayRequestHandler handler
            = new EbayRequestHandler();


    private AmazonRequest amazonRequest = new AmazonRequest();

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
    protected void onEbayButtonClick() throws IOException, URISyntaxException {
        Main.getHost().showDocument(handler.getAuthUri().toString());
    }

    @FXML
    protected void onAuthoriseButtonClick() throws IOException {
        try{
            handler.createOauthProperties(
                    handler.getAccessAndRefreshTokens(authorisationBox.getText()),
                    accountNameField.getText());
        }catch (Exception e){
            System.out.println(e.getMessage());
            new Alert(Alert.AlertType.ERROR, "Problem authorising Ebay").showAndWait();

        }
        accountNameField.clear();
        authorisationBox.clear();
    }

    @FXML
    protected void onAmazonButtonClick(){

        try{
            amazonRequest.getAmazonAccessToken(amazonBox.getText(), clientBox.getText(), secretBox.getText());
        } catch (URISyntaxException | IOException | InterruptedException e){
            new Alert(Alert.AlertType.ERROR, "Problem authorising Amazon").showAndWait();
        }
    }
}
