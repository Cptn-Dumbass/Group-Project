package com.example.ael.Controllers.Inventory.Product;

import com.example.ael.Utility.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import org.w3c.dom.Text;

import java.sql.*;
import java.util.Optional;

public class ProductAdd {

    @FXML
    private ComboBox combo_category;

    @FXML
    private TextField text_title;

    @FXML
    private TextField text_weight;

    @FXML
    private TextField text_unitCost;

    @FXML
    private TextField text_minLevel;

    @FXML
    private AnchorPane mainPane;

    @FXML
    public void initialize() {
        refreshCombo();
        //todo remember to disable/enable category buttons as appropriate
    }

    @FXML
    public void onCategoryAddClick(){
        TextInputDialog addDialog = new TextInputDialog();
        addDialog.setHeaderText("Enter the name of the new category:");
        String newCategory = addDialog.showAndWait().get();
        if (!newCategory.trim().isEmpty()) {
            categoryAdd(newCategory);
            refreshCombo();
        }
    }

    @FXML
    public void onCategoryEditClick(){
        Object selection = combo_category.getSelectionModel().getSelectedItem();
        if (selection == null){
            new Alert(Alert.AlertType.ERROR, "Error: No category selected.").showAndWait();
        } else {
            TextInputDialog addDialog = new TextInputDialog();
            addDialog.setHeaderText("Enter the new name of the category:");
            String newCategory = addDialog.showAndWait().get();
            if (!newCategory.trim().isEmpty()) {
                categoryEdit(selection.toString(), newCategory);
                refreshCombo();
            }
        }
    }

    @FXML
    public void onCategoryDeleteClick(){
        SingleSelectionModel selectionModel = combo_category.getSelectionModel();
        if (selectionModel.isEmpty()){
            new Alert(Alert.AlertType.ERROR, "Error: No category selected.").showAndWait();
        } else {
            categoryDelete(selectionModel.getSelectedItem().toString());
            refreshCombo();
        }
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

    private void categoryAdd(String newCategory) {
        Connection connection = DatabaseManager.connect();
        String query = ("INSERT INTO Categories(CategoryName) VALUES ('"+newCategory+"');");
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.executeUpdate();
        }
        catch (SQLIntegrityConstraintViolationException e) {
            new Alert(Alert.AlertType.WARNING, "Error: Category already exists.").showAndWait();
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closeConnection(connection);
        }
    }

    public void categoryEdit(String oldCategory, String newCategory) {
        Connection connection = DatabaseManager.connect();
        String updateQuery = ("UPDATE Categories SET CategoryName='"+newCategory+"' WHERE CategoryID LIKE '"+getCategoryID(oldCategory)+"';");

        try  {
            connection.prepareStatement(updateQuery).executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closeConnection(connection);
        }
    }

    private void categoryDelete(String inputCategory) {
        Connection connection = DatabaseManager.connect();
        String query = ("DELETE FROM Categories WHERE CategoryName = '"+inputCategory+"';");
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.executeUpdate();
        }
        catch (SQLIntegrityConstraintViolationException e) {
            new Alert(Alert.AlertType.ERROR, "Error: Category not empty. Please edit any products of this category before deletion.").showAndWait();
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closeConnection(connection);
        }
    }

    private void refreshCombo() {
        combo_category.getSelectionModel().clearSelection();
        populateCombo("CategoryName", "Categories",combo_category);
    }

    private boolean saveRecord() {
        String category = combo_category.getSelectionModel().getSelectedItem().toString();
        String title = text_title.getText();
        String weight = text_weight.getText();
        String unitCost = text_unitCost.getText();
        String minLevel = text_minLevel.getText();

        //validating fields that can't be empty
        for (String item: new String[] {category, title, weight, unitCost, minLevel}) {
            if(item.trim().isEmpty()) { new Alert(Alert.AlertType.ERROR, "Please ensure all required fields are populated").showAndWait(); return false; }
        }

        try { Double.parseDouble(unitCost); } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Please enter a valid decimal for Unit Cost").showAndWait(); return false;
        }
        try { Integer.parseInt(weight); } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Please enter a valid integer for Weight").showAndWait(); return false;
        }
        try { Integer.parseInt(minLevel); } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Please enter a valid integer for Min Level").showAndWait(); return false;
        }

        String addQuery = "INSERT INTO Products(CategoryID, Title, UnitCost, Weight, MinLevel) VALUES ("
                       + (getCategoryID(category) + ", ")
                       + ("'" + title + "', ")
                       + (unitCost + ", ")
                       + (weight + ", ")
                       + (minLevel)
                       + (");");

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

    private Integer getCategoryID(String categoryName) {
        Connection connection = DatabaseManager.connect();
        String IDquery = ("SELECT CategoryID FROM Categories WHERE CategoryName LIKE '"+categoryName+"';");
        try  {
            ResultSet rs = connection.prepareStatement(IDquery).executeQuery();
            rs.next();
            Integer catID = rs.getInt("CategoryID");

            return catID;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            DatabaseManager.closeConnection(connection);
        }
    }


    private void closeWindow() {
        Stage currentStage = (Stage) mainPane.getScene().getWindow();
        currentStage.close();
    }

    private void populateCombo(String fieldName, String tableName, ComboBox combo) {
        Connection connection = DatabaseManager.connect();
        String query = ("SELECT "+fieldName+" FROM "+tableName+";");
        ObservableList<Object> items = FXCollections.observableArrayList();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(resultSet.getObject(fieldName));
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
}
