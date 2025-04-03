package com.example.ael.Controllers.Orders;

import com.example.ael.Main;
import com.example.ael.Utility.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class OrdersEdit {
    @FXML
    private AnchorPane mainPane;

    @FXML
    private ComboBox combo_AddrID;
    @FXML
    private ComboBox combo_Country;
    @FXML
    private ComboBox combo_CustID;
    @FXML
    private ComboBox combo_PaymentMethod;
    @FXML
    private ComboBox combo_PostalService;
    @FXML
    private ComboBox combo_Source;
    @FXML
    private ComboBox combo_Status;

    @FXML
    private DatePicker date_Processed;
    @FXML
    private DatePicker date_Received;

    @FXML
    private TableView table_OrderItems;
    @FXML
    private TableColumn<OrdersViewItems.OrderItemDataModel, String> SKUColumn;
    @FXML
    private TableColumn<OrdersViewItems.OrderItemDataModel, Integer> QuantityColumn;

    @FXML
    private TextField text_AddrLine1;
    @FXML
    private TextField text_AddrLine2;
    @FXML
    private TextField text_ChnlRef;
    @FXML
    private TextField text_Company;
    @FXML
    private TextField text_Email;
    @FXML
    private TextField text_ExtRef;
    @FXML
    private TextField text_FirstName;
    @FXML
    private ComboBox combo_OrderItemSearch;
    @FXML
    private TextField text_orderItemQuantity;
    @FXML
    private TextField text_PhoneNo;
    @FXML
    private TextField text_PkgWeight;
    @FXML
    private TextField text_Postcode;
    @FXML
    private TextField text_RefNo;
    @FXML
    private TextField text_Surname;
    @FXML
    private TextField text_TrackNo;
    @FXML
    private TextField text_purchasePrice;
    @FXML
    private Button removeButton;

    private final String newText = "[NEW]";
    private String reference;

    @FXML
    protected void initialize() {
        table_OrderItems.getSelectionModel().clearSelection();
        table_OrderItems.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) { removeButton.setDisable(false); }
            else { removeButton.setDisable(true); }
        });
        reference = (String) Main.getPersistentData();
        populateFields();
    }

    @FXML
    protected void onCancelButtonClick() {
        closeWindow();
    }

    @FXML
    protected void onSaveButtonClick() {
        Boolean success = saveRecord();
        if(success) {
            new Alert(Alert.AlertType.INFORMATION,"Record Saved").showAndWait();
            closeWindow();
        }
        //on fail, just leave window open - error reporting left to saverecord
    }

    @FXML
    protected void onOrderAddButtonClick() {
        Integer Quantity = 0;
        try {
            Quantity = Integer.parseInt(text_orderItemQuantity.getText());
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.WARNING,"Please enter a valid value for quantity").showAndWait();
            return;
        }

        String SKU = combo_OrderItemSearch.getSelectionModel().getSelectedItem().toString();
        OrderItemDataModel item = new OrderItemDataModel(SKU, Quantity);
        ObservableList<OrderItemDataModel> items = table_OrderItems.getItems();
        items.add(item);
        table_OrderItems.setItems(items);
    }

    @FXML
    protected void onOrderRemoveButtonClick() {
        Object item = table_OrderItems.getSelectionModel().getSelectedItem();
        table_OrderItems.getItems().remove(item);
    }

    @FXML
    private void onAddressIDChanged() {
        if(combo_AddrID.getSelectionModel().getSelectedItem().equals(newText)){
            text_Postcode.setEditable(true);
            text_Postcode.clear();
            text_AddrLine1.setEditable(true);
            text_AddrLine1.clear();
            text_AddrLine2.setEditable(true);
            text_AddrLine2.clear();
            combo_Country.setEditable(true);
            combo_Country.getSelectionModel().clearSelection();
        }
        else {
            populateAddressFields((Integer) combo_AddrID.getSelectionModel().getSelectedItem());
            text_Postcode.setEditable(false);
            text_AddrLine1.setEditable(false);
            text_AddrLine2.setEditable(false);
            combo_Country.setEditable(false);
        }
    }

    @FXML
    private void onCustomerIDChanged() {
        if(combo_CustID.getSelectionModel().getSelectedItem().equals(newText)){
            text_FirstName.setEditable(true);
            text_FirstName.clear();
            text_Surname.setEditable(true);
            text_Surname.clear();
            text_Email.setEditable(true);
            text_Email.clear();
            text_PhoneNo.setEditable(true);
            text_PhoneNo.clear();
            text_Company.setEditable(true);
            text_Company.clear();
        }
        else {
            populateCustomerFields((Integer) combo_CustID.getSelectionModel().getSelectedItem());
            text_FirstName.setEditable(false);
            text_Surname.setEditable(false);
            text_Email.setEditable(false);
            text_PhoneNo.setEditable(false);
            text_Company.setEditable(false);
        }
    }

    @FXML
    private void onStatusChanged() {
        String status = combo_Status.getSelectionModel().getSelectedItem().toString();
        populatePostageFields(status);
    }

    private void closeWindow() {
        Stage currentStage = (Stage) mainPane.getScene().getWindow();
        currentStage.close();
    }

    private void populateFields() {
        SKUColumn.setCellValueFactory(new PropertyValueFactory<>("SKU"));
        QuantityColumn.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
        Connection connection = DatabaseManager.connect();

        //populate and lock reference number
        populateCombo("CustomerID", "Customers", combo_CustID, new String[] {newText});
        populateCombo("AddressID", "Addresses", combo_AddrID, new String[] {newText});
        populateCombo("Service", "PostalServices", combo_PostalService);
        populateCombo("Source", "OrderSources", combo_Source);
        populateCombo("Stage", "OrderStages", combo_Status);
        populateCombo("CountryName", "Countries", combo_Country);
        populateCombo("PaymentMethod", "OrderPaymentMethods", combo_PaymentMethod);
        populateCombo("SKU", "ProductSKU", combo_OrderItemSearch);

        String query = ("SELECT * FROM Orders WHERE Orders.ReferenceNum LIKE '"+reference+"';");

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    text_RefNo.setText(resultSet.getString("ReferenceNum"));

                    String status = resultSet.getString("Stage");
                    Integer custID = resultSet.getInt("CustomerID");
                    Integer addrID = resultSet.getInt("AddressID");

                    selectCombo(resultSet.getString("Source"), combo_Source);
                    selectCombo(resultSet.getString("PaymentMethod"), combo_PaymentMethod);

                    populateOrderItems();

                    selectCombo(custID, combo_CustID);
                    populateCustomerFields(custID);

                    selectCombo(addrID, combo_AddrID);
                    populateAddressFields(addrID);

                    selectCombo(status, combo_Status);
                    populatePostageFields(status);


                    text_ChnlRef.setText(resultSet.getString("ChannelReference"));
                    text_ExtRef.setText(resultSet.getString("ExternalReference"));
                    text_purchasePrice.setText("" + resultSet.getDouble("PurchasePrice"));
                    LocalDate receivedDate = resultSet.getDate("ReceivedDate").toLocalDate();
                    date_Received.setValue(receivedDate);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closeConnection(connection);
        }
    }

    private boolean saveRecord(){
        //todo validate length on all these
        String addrLine1 = text_AddrLine1.getText();
        String addrLine2= text_AddrLine2.getText();
        String chnlRef = text_ChnlRef.getText();
        String company = text_Company.getText();
        String email = text_Email.getText();
        String extRef = text_ExtRef.getText();
        String firstName = text_FirstName.getText();
        String surname = text_Surname.getText();
        //this could be validated as needing to be before processedDate? unimportant
        String receivedDate = (date_Received.getValue()).toString();

        //validating fields that can't be empty
        for (String item: new String[] {extRef, chnlRef, receivedDate, addrLine1, firstName, surname, email}) {
            if(item.equals("")) { new Alert(Alert.AlertType.ERROR, "Please ensure all required fields are populated").showAndWait(); return false; }
        }

        //validated text fields
        String phoneNo = text_PhoneNo.getText();
        if(Pattern.compile("^\\\\+[1-9]\\\\\\\\d{1,14}$").matcher(phoneNo).find()){
            new Alert(Alert.AlertType.ERROR, "Please enter a valid value for Phone No").showAndWait(); return false;
        }
        String purchasePrice = text_purchasePrice.getText();
        try { Double.parseDouble(purchasePrice); } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Please enter a valid decimal for Purchase Price").showAndWait(); return false;
        }
        String postcode = text_Postcode.getText();
        if(Pattern.compile("^[A-Z]{1,2}\\d[A-Z\\d]? ?\\d[A-Z]{2}$\n").matcher(postcode).find()){
            new Alert(Alert.AlertType.ERROR, "Please enter a valid value for PostCode").showAndWait(); return false;
        }

        //these dont need valid as we're trusting the combobox to only have valid values
        String addrID = combo_AddrID.getSelectionModel().getSelectedItem().toString();
        String country = combo_Country.getSelectionModel().getSelectedItem().toString();
        String custID = combo_CustID.getSelectionModel().getSelectedItem().toString();
        String paymentMethod = combo_PaymentMethod.getSelectionModel().getSelectedItem().toString();
        String source = combo_Source.getSelectionModel().getSelectedItem().toString();
        String status = combo_Status.getSelectionModel().getSelectedItem().toString();

        String processedDate = null;
        if(date_Processed.getValue() != null) { processedDate = (date_Processed.getValue()).toString(); }

        String custQuery = null;
        if(custID.equals(newText)) {
            custQuery =    "INSERT INTO Customers(FirstName, Surname, EmailAddress, PhoneNumber, Company) VALUES ("
                        + ("'"+firstName+"', ")
                        + ("'"+surname+"', ")
                        + ("'"+email+"', ")
                        + ("'"+phoneNo+"', ")
                        + ("'"+company+"'")
                        + (");");
        }
        else { //valid custID
            custQuery =    "UPDATE Customers SET "
                        + ("FirstName = '" + firstName + "', ")
                        + ("Surname = '" + surname + "', ")
                        + ("EmailAddress = '" + email + "', ")
                        + ("PhoneNumber = '" + phoneNo + "', ")
                        + ("Company = '" + company + "' ")
                        + ("WHERE CustomerID = '" + custID + "';");
        }

        String addrQuery = null;
        if(addrID.equals(newText)) {
            addrQuery =   "INSERT INTO Addresses(CountryName, PostCode, Line1, Line2) VALUES ("
                    + ("'"+country+"'")
                    + ("'"+postcode+"'")
                    + ("'"+addrLine1+"'")
                    + ("'"+addrLine2+"'")
                    + (");");
        }
        else { //valid addrID
            addrQuery =   "UPDATE Addresses SET "
                    + ("CountryName = '" + country + "', ")
                    + ("PostCode = '" + postcode + "', ")
                    + ("Line1 = '" + addrLine1 + "', ")
                    + ("Line2 = " + addrLine2 + " ")
                    + ("WHERE AddressID = '" + addrID + "';");
        }

        String postageQuery = null;
        if(status.equals("Approved") || status.equals("Sent")) {
            String pkgWeight = text_PkgWeight.getText();
            try { Integer.parseInt(pkgWeight); } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Please enter a valid integer for Package Weight").showAndWait(); return false;
            }
            String postalService = combo_PostalService.getSelectionModel().getSelectedItem().toString();
            //todo regex validaton for these fields
            String trackNo = text_TrackNo.getText();

            postageQuery = "INSERT INTO Postage(ReferenceNum, Weight, Service, TrackingNumber) VALUES ("
                        + ("'"+reference + "', ")
                        + (pkgWeight + ", ")
                      //+ ("'"+size+"',")
                      //+ ("'"+class+"',")
                        + ("'"+postalService+"', ")
                        + ("'"+trackNo+"'")
                        + (" ON DUPLICATE KEY UPDATE ")
                        + ("Weight="+pkgWeight+", ")
                      //+ ("Size='"+size+"',")
                      //+ ("Class='"+class+"',")
                        + ("Service='"+postalService+"', ")
                        + ("TrackingNumber='"+trackNo+"';");
        }
        else {
            postageQuery = "DELETE FROM Postage WHERE ReferenceNum='" + reference + "';";
        }


        ObservableList<OrderItemDataModel> orderItems = table_OrderItems.getItems();
        //Delete all with reference, insert all
        String orderItemDeleteQuery = ("DELETE FROM OrderItems WHERE ReferenceNum = '" + reference + "';");
        String orderItemInsertQuery = null;
        if(orderItems.size() > 0){
            orderItemInsertQuery = ("INSERT INTO OrderItems(ReferenceNum, SKU, Quantity) VALUES ");
            for (int i = 0; i < orderItems.size(); i++) {
                OrderItemDataModel item = orderItems.get(i);
                orderItemInsertQuery += ("('" + reference + "', '" + item.SKU + "', " + item.Quantity + ")");
                if(i == (orderItems.size()-1)) { orderItemInsertQuery += ";"; }
                else { orderItemInsertQuery += ", "; }
            }
        }


        String ordersQuery =   "UPDATE Orders SET "
                            + ("ReceivedDate = '" + receivedDate + "', ")
                            + ("Source = '" + source + "', ")
                            + ("ExternalReference = '" + extRef + "', ")
                            + ("ChannelReference = '" + chnlRef + "', ")
                            + ("PaymentMethod = '" + paymentMethod + "', ")
                            + ("Stage = '" + status + "', ")
                            + ("PurchasePrice = '" + purchasePrice + "', ")
                            + ("CustomerID = %s,")
                            + ("AddressID = %s,")
                            + ("ProcessedDate = " + processedDate + " ")
                            + ("WHERE ReferenceNum = '" + reference + "';");

        Connection connection = null;
        try {
            connection = DatabaseManager.connect();
            connection.setAutoCommit(false); //so can rollback

            PreparedStatement addrStmt = connection.prepareStatement(addrQuery, Statement.RETURN_GENERATED_KEYS);
            addrStmt.executeUpdate();
            ResultSet addrResults = addrStmt.getGeneratedKeys();
            int autoAddrID = 0;
            if(addrResults.next()){
                autoAddrID = addrResults.getInt(1);
            }

            PreparedStatement custStmt = connection.prepareStatement(custQuery, Statement.RETURN_GENERATED_KEYS);
            custStmt.executeUpdate();
            ResultSet custResults = custStmt.getGeneratedKeys();
            int autoCustID = 0;
            if(custResults.next()) {
                autoCustID = addrResults.getInt(1);
            }

            connection.prepareStatement(postageQuery).execute();
            connection.prepareStatement(orderItemDeleteQuery).execute();

            ordersQuery = String.format(ordersQuery, autoCustID, autoAddrID);
            connection.prepareStatement(ordersQuery).execute();

            if(orderItemInsertQuery != null) { connection.prepareStatement(orderItemInsertQuery).execute(); }

            connection.commit();
            return true;
        } catch (SQLException error1) {
            new Alert(Alert.AlertType.ERROR, "Error editing record.").showAndWait();
            try {
                connection.rollback();
            } catch (SQLException error2) {
                new Alert(Alert.AlertType.ERROR, "Error rolling back changes.").showAndWait();
            }
            return false;
        } finally {
            DatabaseManager.closeConnection(connection);
        }
    }

    private void populatePostageFields(String status) {
        if(status.equals("Sent") || status.equals("Approved")) {
            Connection connection = DatabaseManager.connect();
            String query = ("SELECT * FROM Postage WHERE ReferenceNum = '" + reference + "';");

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        selectCombo(resultSet.getString("Service"),combo_PostalService);
                        text_PkgWeight.setText(resultSet.getString("Weight"));
                        text_TrackNo.setText(resultSet.getString("TrackingNumber"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DatabaseManager.closeConnection(connection);
            }
            combo_PostalService.setDisable(false);
            text_PkgWeight.setEditable(true);
            text_TrackNo.setEditable(true);

            if(status.equals("Sent")){
                date_Processed.setValue(LocalDate.now());
                date_Processed.setDisable(false);
            }
            else {
                date_Processed.setValue(null);
                date_Processed.setDisable(true);
            }
        }
        else { //print/process/packed
            date_Processed.setValue(null);
            date_Processed.setEditable(false);

            combo_PostalService.getSelectionModel().clearSelection();
            combo_PostalService.setDisable(true);
            text_PkgWeight.clear();
            text_PkgWeight.setEditable(false);
            text_TrackNo.clear();
            text_TrackNo.setEditable(false);
        }

    }

    private void populateAddressFields(Integer addressID) {
        Connection connection = DatabaseManager.connect();
        String query = ("SELECT * FROM Addresses WHERE AddressID = '" + addressID + "';");

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    selectCombo(resultSet.getString("CountryName"),combo_Country);
                    text_Postcode.setText(resultSet.getString("PostCode"));
                    text_AddrLine1.setText(resultSet.getString("Line1"));
                    text_AddrLine2.setText(resultSet.getString("Line2"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closeConnection(connection);
        }
    }

    private void populateCustomerFields(Integer customerID) {
        Connection connection = DatabaseManager.connect();
        String query = ("SELECT * FROM Customers WHERE CustomerID = '" + customerID + "';");

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    text_FirstName.setText(resultSet.getString("FirstName"));
                    text_Surname.setText(resultSet.getString("Surname"));
                    text_Email.setText(resultSet.getString("EmailAddress"));
                    text_PhoneNo.setText(resultSet.getString("PhoneNumber"));
                    text_Company.setText(resultSet.getString("Company"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closeConnection(connection);
        }
    }

    private void populateOrderItems() {
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
                table_OrderItems.setItems(items);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closeConnection(connection);
        }
    }

    private void selectCombo(Object selectValue, ComboBox combo) {
        for ( int i = 0; i < combo.getItems().size(); i++){
            if(combo.getItems().get(i).equals(selectValue)) {
                combo.getSelectionModel().select(i);
            }
        }
    }
    private void populateCombo(String fieldName, String tableName, ComboBox combo, Object[] args ) {
        Connection connection = DatabaseManager.connect();
        String query = ("SELECT "+fieldName+" FROM "+tableName+";");
        ObservableList<Object> items = FXCollections.observableArrayList();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(resultSet.getObject(fieldName));
                }
                if(args != null){
                    for (Object object: args) {
                        items.add(object);
                    }
                }
                combo.setItems(items);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closeConnection(connection);
        }
    }
    private void populateCombo(String fieldName, String tableName, ComboBox combo) {
        populateCombo(fieldName, tableName, combo, null);
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