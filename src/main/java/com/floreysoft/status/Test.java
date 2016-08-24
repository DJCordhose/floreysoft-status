package com.floreysoft.status;

public class Test {
    public int id;
    public String name;
    public String description;
    public String url;
    public int interval;
    public boolean disabled;

    public Test() {
    }

    public Test(int id, String name, String description, String url, int interval, boolean disabled) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.url = url;
        this.interval = interval;
        this.disabled = disabled;
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

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
