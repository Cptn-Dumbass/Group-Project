package com.example.ael.DataModels;

public class NotificationDataModel {
    private String description;
    private String priority;
    private String dateTimeReceived;

    public NotificationDataModel(String description, String priority, String dateTimeReceived) {
        this.description = description;
        this.priority = priority;
        this.dateTimeReceived = dateTimeReceived;
    }

    public String getDescription() {
        return description;
    }

    public String getPriority() {
        return priority;
    }

    public String getDateTimeReceived() {
        return dateTimeReceived;
    }
}
