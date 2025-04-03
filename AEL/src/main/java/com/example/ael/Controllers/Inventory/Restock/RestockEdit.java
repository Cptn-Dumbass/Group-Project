package com.example.ael.Controllers.Inventory.Restock;

import com.example.ael.Main;
import com.example.ael.Utility.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class RestockEdit {

    @FXML
    private ComboBox combo_prodID;

    @FXML
    private DatePicker date_purchaseDate;

    @FXML
    private TextField text_restockID;

    @FXML
    private TextField text_quantity;

    @FXML
    private TextField text_totalCost;

    @FXML
    private TextField text_supplier;

    @FXML
    private AnchorPane mainPane;

    private Integer restockID;

    @FXML
    public void initialize() {
        restockID = (Integer) Main.getPersistentData();
        populateFields();
    }

    @FXML
    public void onSaveClick() {
        Boolean success = saveRecord();
        if(success) {
            new Alert(Alert.AlertType.INFORMATION,"Record Saved").showAndWait();
            closeWindow();
        }
    }

    @FXML
    public void onCancelClick() {
        closeWindow();
    }

    private void refreshCombo() {
        combo_prodID.getSelectionModel().clearSelection();

        Connection connection = DatabaseManager.connect();
        String query = ("SELECT ProductID, Title FROM Products;");
        ObservableList<ComboProduct> items = FXCollections.observableArrayList();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(new ComboProduct(resultSet.getInt("ProductID"), resultSet.getString("Title")));
                }
                combo_prodID.setItems(items);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closeConnection(connection);
        }
    }

    private boolean saveRecord() {
        Object selectedID = combo_prodID.getSelectionModel().getSelectedItem();
        if (selectedID == null) { new Alert(Alert.AlertType.ERROR, "Please select a product ID.").showAndWait(); return false; }

        Integer prodID = ((ComboProduct) selectedID).getProductID();
        String quantity = text_quantity.getText();
        String totalCost = text_totalCost.getText();
        String purchaseDate = (date_purchaseDate.getValue()).toString(); //dont think this can be null?
        String supplier = text_supplier.getText();

        //validating fields that can't be empty
        for (String item: new String[] {quantity, totalCost, purchaseDate}) {
            if(item.trim().isEmpty()) { new Alert(Alert.AlertType.ERROR, "Please ensure all required fields are populated").showAndWait(); return false; }
        }

        try { Double.parseDouble(totalCost); } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Please enter a valid decimal for Total Cost").showAndWait(); return false;
        }
        try { Integer.parseInt(quantity); } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Please enter a valid integer for Quantity").showAndWait(); return false;
        }

        String addQuery = "UPDATE Restocks SET "
                + ("ProductID=" + prodID + ", ")
                + ("Quantity=" + Integer.parseInt(quantity) + ", ")
                + ("TotalCost=" + Double.parseDouble(totalCost) + ", ")
                + ("PurchaseDate='" + purchaseDate + "', ");
        if(supplier.equals("")) { addQuery+= ("Supplier=null"); } else { addQuery+= ("Supplier='" + supplier + "' "); }
        addQuery += (" WHERE RestockID LIKE "+restockID+" ;");

        Connection connection = DatabaseManager.connect();
        try (PreparedStatement preparedStatement = connection.prepareStatement(addQuery)) {
            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseManager.closeConnection(connection);
        }

        return true;
    }

    private void populateFields() {
        text_restockID.setText(restockID.toString());
        refreshCombo();

        String query = ("SELECT * FROM Restocks WHERE RestockID LIKE " + restockID + ";");
        Connection connection = DatabaseManager.connect();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()){
                for ( int i = 0; i < combo_prodID.getItems().size(); i++){
                    if(((ComboProduct) combo_prodID.getItems().get(i)).getProductID().equals(rs.getInt("ProductID"))) {
                        combo_prodID.getSelectionModel().select(i);
                    }
                }
                date_purchaseDate.setValue(rs.getDate("PurchaseDate").toLocalDate());
                text_quantity.setText(""+rs.getInt("Quantity"));
                text_totalCost.setText(""+rs.getDouble("TotalCost"));
                text_supplier.setText(rs.getString("Supplier"));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closeConnection(connection);
        }

    }

    private void closeWindow() {
        Stage currentStage = (Stage) mainPane.getScene().getWindow();
        currentStage.close();
    }

    private class ComboProduct{
        private final Integer productID;
        private  final String productTitle;

        public ComboProduct(Integer productID, String productTitle) {
            this.productID = productID;
            this.productTitle = productTitle;
        }

        public Integer getProductID() {
            return productID;
        }

        public String getProductTitle() {
            return productTitle;
        }

        @Override
        public String toString(){
            return (productID + " - " + productTitle);
        }
    }
}
