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

public class OrdersViewCust {
    @FXML
    private AnchorPane mainPane;

    @FXML
    private Label refLabel;

    @FXML
    private ComboBox combo_CustID;
    @FXML
    private TextField text_FirstName;
    @FXML
    private TextField text_Surname;
    @FXML
    private TextField text_Email;
    @FXML
    private TextField text_PhoneNo;
    @FXML
    private TextField text_Company;

    @FXML
    protected void initialize(){
        Object reference = Main.getPersistentData();
        if(reference != null) {
            String text = ("Order Reference: " + (String) reference);
            refLabel.setText(text);
        }

        String query = ("SELECT Customers.* FROM Customers INNER JOIN Orders ON Orders.CustomerID=Customers.CustomerID WHERE ReferenceNum LIKE '"+reference+"' LIMIT 1;");
        Connection connection = DatabaseManager.connect();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            ObservableList<Integer> customerID = FXCollections.observableArrayList(resultSet.getInt("CustomerID"));
            combo_CustID.setItems(customerID);
            combo_CustID.getSelectionModel().select(0);

            text_FirstName.setText(resultSet.getString("FirstName"));
            text_Surname.setText(resultSet.getString("Surname"));
            text_Email.setText(resultSet.getString("EmailAddress"));
            text_PhoneNo.setText(resultSet.getString("PhoneNumber"));
            text_Company.setText(resultSet.getString("Company"));
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
