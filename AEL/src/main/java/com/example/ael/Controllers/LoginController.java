package com.example.ael.Controllers;

import com.example.ael.Main;
import com.example.ael.Utility.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginController {
    @FXML
    private TextField loginField;

    @FXML
    private AnchorPane mainPane;

    @FXML
    public void onContinueButtonPress() {
        String loginQuery = ("SELECT * FROM Users WHERE Users.Username LIKE '"+loginField.getText()+"';");
        try (Connection connection = DatabaseManager.connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(loginQuery)) {
            if (!resultSet.next()) {
                //mainPane.getScene().setCursor(Cursor.DEFAULT);
                Alert alert = new Alert(Alert.AlertType.INFORMATION,"Could not find this user in the database. Please try again.");
                alert.showAndWait();
            }
            else {
                String accessLevel = resultSet.getString("AccessLevel");
                String username = resultSet.getString("Username");
                String[] userData = {username, accessLevel};
                Main.setPersistentData(userData);
                Main.changeScene("Dashboard.fxml");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void onHelpButtonPress() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION,"temp help text");
        alert.showAndWait();
    }
}
