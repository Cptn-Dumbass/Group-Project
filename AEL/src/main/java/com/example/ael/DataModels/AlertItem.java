package com.example.ael.DataModels;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class AlertItem {


    private IntegerProperty id;
    private StringProperty alert;

    private IntegerProperty priority;

    private StringProperty received;

    public AlertItem(int id, String alert, int priority, String received){

        setIdProperty().set(id);
        setAlertProperty().set(alert);
        setPriorityProperty().set(priority);
        setReceivedProperty().set(received);

    }

    public IntegerProperty setIdProperty(){
        if(id == null) id = new SimpleIntegerProperty(this, "id");
        return id;
    }

    public StringProperty setAlertProperty(){
        if(alert == null) alert = new SimpleStringProperty(this, "alert");
        return alert;
    }

    public IntegerProperty setPriorityProperty(){
        if(priority == null) priority = new SimpleIntegerProperty(this, "priority");
        return priority;
    }

    public StringProperty setReceivedProperty(){
        if(received == null) received = new SimpleStringProperty(this, "received");
        return received;
    }

    public int getId(){
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getAlert() {
        return alert.get();
    }

    public void setAlert(String alert) {
        this.alert.set(alert);
    }

    public int getPriority() {
        return priority.get();
    }

    public void setPriority(int priority) {
        this.priority.set(priority);
    }


    public void setReceived(String received) {
        this.received.set(received);
    }

    public String getReceived() {
        return received.get();
    }
}
