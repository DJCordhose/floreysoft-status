package com.floreysoft.status;

public class Report {
    public final String name;
    public final String description;
    public final long timestamp;
    public final Status status;

    public Report(String name, String description, long timestamp, Status status) {
        this.name = name;
        this.description = description;
        this.timestamp = timestamp;
        this.status = status;
    }
}
