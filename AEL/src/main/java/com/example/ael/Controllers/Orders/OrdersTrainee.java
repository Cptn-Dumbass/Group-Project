package com.example.ael.Controllers.Orders;

import com.example.ael.DataModels.OrderDataModel;
import com.example.ael.Main;
import com.example.ael.Utility.DatabaseManager;
import com.example.ael.Utility.ListManager;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;
import org.w3c.dom.ls.LSException;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class OrdersTrainee extends OrdersCommon{
    @FXML
    protected Button markButton;
    @FXML
    protected Button printButton;
    @FXML
    protected Button pickPackButton;
    @FXML
    protected CheckBox printCheck;
    @FXML
    protected TableColumn<OrderDataModel, Boolean> selectColumn;

    @FXML
    public void initialize() {
        Callback<TableColumn<OrderDataModel, Boolean>, TableCell<OrderDataModel, Boolean>> ct = new Callback<TableColumn<OrderDataModel, Boolean>, TableCell<OrderDataModel, Boolean>>() {
            @Override
            public TableCell<OrderDataModel, Boolean> call(TableColumn<OrderDataModel, Boolean> p) {
                final CheckBoxTableCell<OrderDataModel, Boolean> ctCell = new CheckBoxTableCell<>();
                ctCell.setSelectedStateCallback(new Callback<Integer, ObservableValue<Boolean>>() {
                    @Override
                    public ObservableValue<Boolean> call(Integer index) {
                        refreshButtons();
                        return orderTable.getItems().get(index).isSelected();
                    }
                });
                return ctCell;
            }
        };
        selectColumn.setCellFactory(ct);
        selectColumn.setCellValueFactory(cd -> cd.getValue().isSelected());

        refreshTable();
        orderTable.setSelectionModel(null);
    }

    @FXML
    protected void onPrintCheckChanged() {
        if (printCheck.isSelected()) {
            refreshTable("Print");
        }
        else {
            refreshTable();
        }
    }

    @FXML
    protected void onMarkButtonClick() {
        List<String> selectedRefNo = new ArrayList<String>();
        for (OrderDataModel item : getSelected()) {
            selectedRefNo.add(item.getReference());
        }

        if (selectedRefNo.size() > 0) {
            String query =  "UPDATE Orders SET Stage='Packed' WHERE ReferenceNum IN (";
            for (int i = 0; i < selectedRefNo.size(); i++) {
                query += "'"+selectedRefNo.get(i)+"'";
                if (i == selectedRefNo.size()-1) { query += ");"; }
                else { query += ","; }
            }

            Connection connection = DatabaseManager.connect();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DatabaseManager.closeConnection(connection);
            }

            new Alert(Alert.AlertType.INFORMATION, "Orders marked as Packed").showAndWait();
        } else {
            new Alert(Alert.AlertType.INFORMATION, "No orders selected").showAndWait();
        }
        initialize();
    }

    @FXML
    protected void onPickPackButtonClick() {
        List<String> selectedRefNo = new ArrayList<String>();
        for (OrderDataModel item : getSelected()) {
            selectedRefNo.add(item.getReference());
        }
        String createdRef = ListManager.createLists(selectedRefNo.toArray(String[]::new));

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,("Pick/Pack Lists #"+createdRef+" created. \n\nWould you like to print these lists now?"));
        Optional<ButtonType> decision = alert.showAndWait();
        if(decision.get().equals(ButtonType.OK)) {
            String pickPath = "./orderLists/"+createdRef+"_Pick.pdf";
            String packPath = "./orderLists/"+createdRef+"_Pack.pdf";
            Main.openDoc(pickPath);
            Main.openDoc(packPath);
        }

        if (selectedRefNo.size() > 0) {
            String query =  "UPDATE Orders SET Stage='Process' WHERE ReferenceNum IN (";
            for (int i = 0; i < selectedRefNo.size(); i++) {
                query += "'"+selectedRefNo.get(i)+"'";
                if (i == selectedRefNo.size()-1) { query += ");"; }
                else { query += ","; }
            }

            Connection connection = DatabaseManager.connect();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DatabaseManager.closeConnection(connection);
            }

        }
        initialize();
    }

    @FXML
    protected void onPrintButtonClick() {
        File listDir = new File("./orderLists");
        listDir.mkdirs();
        File[] existingLists = listDir.listFiles();

        if (existingLists.length > 0) {
            ChoiceDialog choiceDialog = new ChoiceDialog(existingLists[0], existingLists);
            choiceDialog.setTitle("List Selection");
            choiceDialog.setHeaderText("Which list do you want to reprint?");
            choiceDialog.setContentText("List Path:");
            Optional<File> response = choiceDialog.showAndWait();
            if(response.isPresent()) {
                Main.openDoc(response.get().toString());
            }
        } else {
            new Alert(Alert.AlertType.ERROR, "No lists are available to reprint.").showAndWait();
        }
    }

    @FXML
    protected void onAdminButtonClick() {
        Main.changeScene("orders-view-admin.fxml");
    }

    private void refreshButtons() {
        List<OrderDataModel> selectedItems = getSelected();
        if ( selectedItems.size() > 0 ){
            boolean markDisable = false;
            boolean pickDisable = false;
            for (OrderDataModel item: selectedItems) {
                if(!item.getStatus().equals("Process")) {
                    markDisable = true;
                }
                if(!item.getStatus().equals("Print")) {
                    pickDisable = true;
                }
            }
            markButton.setDisable(markDisable);
            pickPackButton.setDisable(pickDisable);
        }
        else {
            markButton.setDisable(true);
            pickPackButton.setDisable(true);
        }
    }

    private List<OrderDataModel> getSelected() {
        List<OrderDataModel> selectedItems = new ArrayList<>();
        for (int i = 0; i < orderTable.getItems().size(); i++) {
            OrderDataModel item = orderTable.getItems().get(i);
            if (item.isSelected().get() == true) {
                selectedItems.add(item);
            }
        }
        return selectedItems;
    }
}
