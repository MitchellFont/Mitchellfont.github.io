package com.example.eventtrackerfinalproject;

public class Reminder {
    private String id;
    private String title;
    private String date;
    private String description;
    private String time;

    public Reminder(String id, String title, String date, String description, String time) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.description = description;
        this.time = time;
    }

    public Reminder(String title, String date, String description, String time) {
        this("", title, date, description, time);
    }

    // Getters and setters
    public String getId() {
        return id;
    }
    
    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getTime() {
        return time;
    }

    public void setId(String id) {
        this.id = id;
    }
}
