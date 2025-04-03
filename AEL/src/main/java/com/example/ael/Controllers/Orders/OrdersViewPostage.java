package com.example.ael.Controllers.Orders;

import com.example.ael.Main;
import com.example.ael.Utility.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrdersViewPostage {
    @FXML
    private AnchorPane mainPane;

    @FXML
    private Label refLabel;

    @FXML
    private ComboBox combo_PostalService;
    @FXML
    private TextField text_PkgWeight;
    @FXML
    private TextField text_TrackNo;
    @FXML
    private ComboBox combo_AddrID;
    @FXML
    private ComboBox combo_Country;
    @FXML
    private TextField text_Postcode;
    @FXML
    private TextField text_AddrLine2;
    @FXML
    private TextField text_AddrLine1;

    @FXML
    protected void initialize(){
        Object reference = Main.getPersistentData();
        if(reference != null) {
            String text = ("Order Reference: " + (String) reference);
            refLabel.setText(text);
        }

        String postageQuery = ("SELECT Postage.* FROM Postage WHERE ReferenceNum LIKE '"+reference+"' LIMIT 1;");
        Connection connection = DatabaseManager.connect();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(postageQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next() == true){
                text_PkgWeight.setText(""+resultSet.getInt("Weight"));
                text_PkgWeight.setDisable(false);
                text_TrackNo.setText(resultSet.getString("TrackingNumber"));
                text_TrackNo.setDisable(false);

                ObservableList<String> postalService = FXCollections.observableArrayList(resultSet.getString("Service"));
                combo_PostalService.setItems(postalService);
                combo_PostalService.getSelectionModel().select(0);
                combo_PostalService.setDisable(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closeConnection(connection);
        }

        String addrQuery = ("SELECT Addresses.* FROM Addresses INNER JOIN Orders ON Orders.AddressID=Addresses.AddressID WHERE ReferenceNum LIKE '"+reference+"' LIMIT 1;");
        connection = DatabaseManager.connect();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(addrQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            ObservableList<Integer> addrID = FXCollections.observableArrayList(resultSet.getInt("AddressID"));
            combo_AddrID.setItems(addrID);
            combo_AddrID.getSelectionModel().select(0);

            ObservableList<String> country = FXCollections.observableArrayList(resultSet.getString("CountryName"));
            combo_Country.setItems(country);
            combo_Country.getSelectionModel().select(0);

            text_Postcode.setText(resultSet.getString("PostCode"));
            text_AddrLine1.setText(resultSet.getString("Line1"));
            text_AddrLine2.setText(resultSet.getString("Line2"));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closeConnection(connection);
        }
    }

    @FXML
    protected void onCancelButtonClick() {
        closeWindow();
    }

    private void closeWindow() {
        Stage currentStage = (Stage) mainPane.getScene().getWindow();
        currentStage.close();
    }
}
