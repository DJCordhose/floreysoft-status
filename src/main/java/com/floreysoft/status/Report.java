package com.floreysoft.status;

public class Report {
    public int id;
    public String name;
    public String description;
    public long timestamp;
    public Status status;

    public Report() {
    }

    public Report(int id, String name, String description, long timestamp, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.timestamp = timestamp;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
