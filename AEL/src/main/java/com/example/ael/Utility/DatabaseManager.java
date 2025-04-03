package com.example.ael.Utility;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;


public class DatabaseManager {
    private static Connection connection;

    public static Connection connect() {
        String configFilePath = "src/DBconfig.properties";
        FileInputStream configInput = null;
        try {
            configInput = new FileInputStream(configFilePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Properties prop = new Properties();
        try {
            prop.load(configInput);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String dbName = prop.getProperty("DB_NAME");
        String dbPort = prop.getProperty("DB_PORT");
        String dbIP = prop.getProperty("DB_IP");

        final String URL = ("jdbc:mysql://" + dbIP + ":" + dbPort + "/" + dbName);
        final String USER = prop.getProperty("DB_USER");
        final String PASSWORD = prop.getProperty("DB_PASSWORD");

        try { //this should be handled properly, give  a lil error message and probably point to wherever we're putting troubleshooting
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }
    public static void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }
}

