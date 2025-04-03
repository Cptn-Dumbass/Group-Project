package com.example.ael.DataModels;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class NotificationItem {

    private StringProperty subject;

    private StringProperty sender;

    private IntegerProperty priority;

    private StringProperty timeReceived;

    public NotificationItem(String subject, String sender, int priority, String timeReceived){
        setSubjectProperty().set(subject);
        setSenderProperty().set(sender);
        setPriorityProperty().set(priority);
        setTimeReceivedProperty().set(timeReceived);
    }

    public StringProperty setSubjectProperty(){
        if(subject == null) subject = new SimpleStringProperty(this, "subject");
        return subject;
    }


    public IntegerProperty setPriorityProperty(){
        if(priority == null) priority = new SimpleIntegerProperty(this, "priority");
        return priority;
    }

    public StringProperty setSenderProperty(){
        if(sender == null) sender = new SimpleStringProperty(this, "sender");
        return sender;
    }

    public StringProperty setTimeReceivedProperty(){
        if(timeReceived == null) timeReceived = new SimpleStringProperty(this, "timeReceived");
        return timeReceived;
    }




    public String getSubject() {
        return subject.get();
    }

    public StringProperty subjectProperty() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject.set(subject);
    }

    public String getSender() {
        return sender.get();
    }

    public StringProperty senderProperty() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender.set(sender);
    }

    public int getPriority() {
        return priority.get();
    }

    public IntegerProperty priorityProperty() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority.set(priority);
    }

    public String getTimeReceived() {
        return timeReceived.get();
    }

    public StringProperty timeReceivedProperty() {
        return timeReceived;
    }

    public void setTimeReceived(String timeReceived) {
        this.timeReceived.set(timeReceived);
    }
}
