package com.floreysoft.status.entity;

import com.floreysoft.status.dto.Report;
import com.floreysoft.status.dto.Test;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import java.util.Date;

public class TestRunEntity extends TransientEntity {
    public static final String KIND = "TestRun";
    public static final String TEST_KEY = "test";
    public static final String STATUS = "status";

    public TestRunEntity(Entity entity) {
        super(entity);
    }

    public TestRunEntity(TestEntity test, Status status) {
        this(new Entity(KIND));
        setTestKey(test);
        setTimestamp(new Date().getTime());
        setStatus(status);
    }

    public Status getStatus() {
        String statusString = (String)entity.getProperty(STATUS);
        return Status.valueOf(statusString);
    }

    public void setStatus(Status status) {
        entity.setProperty(STATUS, status.name());
    }

    public Key getTestKey() {
        return (Key)entity.getProperty(TEST_KEY);
    }

    public void setTestKey(TestEntity test) {
        entity.setProperty(TEST_KEY, test.getKey());
    }

}
