package com.floreysoft.status.dto;

public class Test {
    public String id;
    public String name;
    public String description;
    public String url;
    public int interval;
    public boolean enabled;

    public Test() {
    }

    public Test(String id, String name, String description, String url, int interval, boolean enabled) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.url = url;
        this.interval = interval;
        this.enabled = enabled;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}