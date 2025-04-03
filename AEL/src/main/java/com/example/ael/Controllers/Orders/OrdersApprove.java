package com.example.ael.Controllers.Orders;

import com.example.ael.Main;
import com.example.ael.Utility.DatabaseManager;
import com.example.ael.Utility.PostageIntegrationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrdersApprove {
    @FXML
    private TextField text_height;
    @FXML
    private TextField text_length;
    @FXML
    private TextField text_width;
    @FXML
    private TextField text_weight;
    @FXML
    private ComboBox combo_size;
    @FXML
    private ComboBox combo_class;
    @FXML
    private ComboBox combo_service;
    @FXML
    private TextField text_trackingNo;
    @FXML
    private AnchorPane mainPane;

    private String reference;
    private PostageIntegrationService postageService;

    @FXML
    protected void initialize() {
        postageService = new PostageIntegrationService();
        reference = Main.getPersistentData().toString();
        populateCombo("Size", "PostageSizes", combo_size);
        populateCombo("Class", "PostageClasses", combo_class);
        populateCombo("Service", "PostalServices", combo_service);
    }

    @FXML
    protected void onCancelButtonClick() {
        closeWindow();
    }


    @FXML
    protected void onManualEntryButtonClick() {
        text_trackingNo.setDisable(false);
    }

    @FXML
    protected void onGenerateButtonClick() {
        text_trackingNo.setDisable(true);

        // Validate all other fields
        try {
            Integer length = Integer.parseInt(text_length.getText());
            Integer width = Integer.parseInt(text_width.getText());
            Integer height = Integer.parseInt(text_height.getText());
            Integer weight = Integer.parseInt(text_weight.getText());

            // Generate a new tracking number via the API using the given fields
            String trackingNumber = postageService.createParcel(length, width, height, weight);

            if(trackingNumber == null) {
                new Alert(Alert.AlertType.ERROR, "Error creating tracking number.");
            }
            else {
                // Set the trackingNo text to the new tracking number
                text_trackingNo.setText(trackingNumber);
            }
        } catch (NumberFormatException e) {
            // Handle invalid input (non-numeric values)
            new Alert(Alert.AlertType.ERROR, "Invalid input. Please enter numeric values for dimensions and weight.").showAndWait();
        }
    }

    private boolean saveRecord() {
        // Validate all fields
        if (!validateFields()) {
            return false;
        }

        String trackingNumber = text_trackingNo.getText();

//        if (!saveShippingLabel()) {
//            return false;
//        }
        new Alert(Alert.AlertType.INFORMATION, "Shipping label save temp");

        // Insert a record into postage with Reference as primary key
        if (!insertPostageRecord(reference, Integer.parseInt(text_weight.getText()), (String) combo_size.getValue(), (String) combo_class.getValue(), (String) combo_service.getValue(), trackingNumber)) {
            return false;
        }
        // Update order record and set Stage to approved
        if (!updateOrderRecord(reference)) {
            return false;
        }

        return true;
    }

    private boolean validateFields() {
        // Check if any of the fields NULL
        if (text_weight.getText().isEmpty() ||
                text_trackingNo.getText().isEmpty() ||
                combo_size.getSelectionModel().isEmpty() ||
                combo_class.getSelectionModel().isEmpty() ||
                combo_service.getSelectionModel().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please enter a value in all required fields.").showAndWait();
            return false;
        }

        // Check if numeric fields contain numeric values
        try {
            Integer.parseInt(text_weight.getText());
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid input. Please enter numeric values for dimensions and weight.").showAndWait();
            return false;
        }

        // Validation success
        return true;
    }

    public boolean insertPostageRecord(String referenceNum, int weight, String size, String postageClass, String service, String trackingNumber) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseManager.connect();

            // Insert postage record
            String sql = "INSERT INTO Postage (ReferenceNum, Weight, Size, Class, Service, TrackingNumber) VALUES (?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, referenceNum);
            stmt.setInt(2, weight);
            stmt.setString(3, size);
            stmt.setString(4, postageClass);
            stmt.setString(5, service);
            stmt.setString(6, trackingNumber);
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            // Close the PreparedStatement and Connection
            DatabaseManager.closeConnection(conn);
        }
    }

    public boolean updateOrderRecord(String referenceNum) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseManager.connect();

            // Update order record
            String sql = "UPDATE Orders SET Stage = 'Approved' WHERE ReferenceNum = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, referenceNum);
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            // Close the PreparedStatement and Connection
            DatabaseManager.closeConnection(conn);
        }
    }

    @FXML
    protected void onSaveButtonClick() {
        Boolean success = saveRecord();
            if (success) {
                new Alert(Alert.AlertType.INFORMATION, "Record Saved").showAndWait();
                closeWindow();
            // Shipping label saved successfully
            saveRecord(); // Call saveRecord method after saving the shipping label
        } else {
            // Error message if error while saving shipping label
            new Alert(Alert.AlertType.ERROR, "Failed to finalize approval. Error saving shipping label.").showAndWait();
        }
    }

    private boolean saveShippingLabel() {
        String senderName = null;
        String senderCity = null;
        String senderState = null;
        String senderZip = null;
        String senderAddress = null;

        // Fetch sender details from the database
        String query = "SELECT Customers.FirstName, Customers.Surname, Addresses.PostCode, Addresses.Line1, Addresses.Line2, Addresses.County, Addresses.City " +
                "FROM Orders INNER JOIN Customers ON Customers.CustomerID=Orders.CustomerID INNER JOIN Addresses ON Addresses.AddressID=Orders.AddressID " +
                "WHERE Orders.ReferenceNum LIKE '" + reference + "' LIMIT 1;";

        Connection connection = DatabaseManager.connect();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    senderName = (resultSet.getString("FirstName") + " " + resultSet.getString("Surname"));
                    senderCity = resultSet.getString("City");
                    senderState = resultSet.getString("County");
                    senderZip = resultSet.getString("PostCode");

                    senderAddress = resultSet.getString("Line1");
                    String line2 = resultSet.getString("Line2");
                    if(line2 != null) { senderAddress += (" " + line2); }
                }
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error retrieving address details from database.").showAndWait();
            return false;
        } finally {
            DatabaseManager.closeConnection(connection);
        }

        // Create the shipping label using the PostageIntegrationService
        String labelUrl = postageService.createShippingLabel(senderName, senderAddress, senderCity, senderState, senderZip);

        // Save the shipping label to a local folder
        if (labelUrl != null) {
            String pdfFilePath = postageService.downloadLabelPDF(labelUrl);
            if (pdfFilePath != null) {
                // Label successfully saved, show success message
                new Alert(Alert.AlertType.INFORMATION, "Shipping label saved successfully.").showAndWait();
                return true;
            } else {
                // Error saving label, show error message
                new Alert(Alert.AlertType.ERROR, "Error saving shipping label.").showAndWait();
                return false;
            }
        } else {
            // Error creating label, show error message
            new Alert(Alert.AlertType.ERROR, "Error creating shipping label.").showAndWait();
            return false;
        }
    }


    private void populateCombo(String fieldName, String tableName, ComboBox combo) {
        Connection connection = DatabaseManager.connect();
        String query = ("SELECT " + fieldName + " FROM " + tableName + ";");
        ObservableList<Object> items = FXCollections.observableArrayList();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(resultSet.getObject(fieldName));
                }
                combo.setItems(items);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closeConnection(connection);
        }
    }

    private void closeWindow() {
        Stage currentStage = (Stage) mainPane.getScene().getWindow();
        currentStage.close();
    }
}
