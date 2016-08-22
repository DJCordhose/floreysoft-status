package com.floreysoft.status;

public class Test {
    public final int id;
    public final String name;
    public final String description;
    public final String url;
    public final int interval;
    public final boolean disabled;

    public Test(int id, String name, String description, String url, int interval, boolean disabled) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.url = url;
        this.interval = interval;
        this.disabled = disabled;
    }
}
