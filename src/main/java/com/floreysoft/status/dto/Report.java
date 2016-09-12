package com.floreysoft.status.dto;

import com.floreysoft.status.entity.Status;

public class Report {
    public String id;
    public String name;
    public String description;
    public long timestamp;
    public Status status;

    public Report() {
    }

    public Report(String id, String name, String description, long timestamp, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.timestamp = timestamp;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
