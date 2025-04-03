package com.example.ael.Controllers.Orders;

import com.example.ael.DataModels.OrderDataModel;
import com.example.ael.Main;
import com.example.ael.Utility.DatabaseManager;
import com.example.ael.Utility.OrdersHyperlinkViewCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.util.Optional;

public class OrdersAdmin extends OrdersCommon {
    @FXML
    private Button deleteButton;
    @FXML
    private Button editButton;
    @FXML
    private CheckBox packedCheck;

    @FXML
    private Button approveButton;

    @FXML
    private Button sentButton;

    @FXML
    private Button printShippingButton;

    @FXML
    public void initialize() {
        refreshTable();
        orderTable.getSelectionModel().clearSelection();
        orderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                editButton.setDisable(false);
                deleteButton.setDisable(false);
                if (newSelection.getStatus().equals("Packed")) {
                    approveButton.setDisable(false);
                }
                else {
                    approveButton.setDisable(true);
                }
                if (newSelection.getStatus().equals("Approved")) {
                    sentButton.setDisable(false);
                    printShippingButton.setDisable(false);
                }
                else {
                    sentButton.setDisable(true);
                    printShippingButton.setDisable(true);
                }
            }
            else {
                editButton.setDisable(true);
                deleteButton.setDisable(true);
                approveButton.setDisable(true);
                sentButton.setDisable(true);
                printShippingButton.setDisable(true);
            }
        });
    }

    @FXML
    protected void onPackedCheckChanged() {
        if (packedCheck.isSelected()) {
            refreshTable("Packed");
        }
        else {
            refreshTable();
        }
        orderTable.getSelectionModel().clearSelection();
    }

    @FXML
    protected void onAddButtonClick() {
        Main.popUpModalScreen("orders-add.fxml");
        initialize();
    }

    @FXML
    protected void onEditButtonClick() {
        Integer index = orderTable.getSelectionModel().getFocusedIndex();
        String reference = ((OrderDataModel) orderTable.getItems().get(index)).getReference();
        Main.setPersistentData(reference);
        Main.popUpModalScreen("orders-edit.fxml");
        initialize();
    }

    @FXML
    protected void onApproveButtonClick() {
        Integer index = orderTable.getSelectionModel().getFocusedIndex();
        String reference = ((OrderDataModel) orderTable.getItems().get(index)).getReference();
        Main.setPersistentData(reference);
        Main.popUpModalScreen("orders-approve.fxml");
        initialize();
    }

    @FXML
    protected void onSentButtonClick() {
        //todo this should do a confirmation popup before marking the selected order as sent
    }

    @FXML
    protected void onPrintShippingButtonClick() {
        //todo this should open a pdf of the shipping label matching the selected order
    }


    @FXML
    protected void onTraineeButtonClick() {
        Main.changeScene("orders-view-trainee.fxml");
    }

    @FXML
    protected void onDeleteButtonClick() {
        String reference = orderTable.getSelectionModel().getSelectedItem().getReference();
        Optional<ButtonType> decision = new Alert(Alert.AlertType.CONFIRMATION, ("Are you sure you want to delete Order #" + reference + "?")).showAndWait();
        if (decision.get().equals(ButtonType.OK)) {
            Connection connection = DatabaseManager.connect();
            try {
                connection.setAutoCommit(false);
                String orderItemsDeleteQuery = ("DELETE FROM OrderItems WHERE ReferenceNum='"+reference+"';");
                String postageDeleteQuery = ("DELETE FROM Postage WHERE ReferenceNum='"+reference+"';");
                String ordersDeleteQuery = ("DELETE FROM Orders WHERE ReferenceNum='"+reference+"';");

                connection.prepareStatement(orderItemsDeleteQuery).execute();
                connection.prepareStatement(postageDeleteQuery).execute();
                connection.prepareStatement(ordersDeleteQuery).execute();

                connection.commit();
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Error editing record.").showAndWait();
                try {
                    connection.rollback();
                } catch (SQLException error2) {
                    new Alert(Alert.AlertType.ERROR, "Error rolling back changes.").showAndWait();
                }
            } finally {
                DatabaseManager.closeConnection(connection);
                initialize();
            }
        }
    }
}
