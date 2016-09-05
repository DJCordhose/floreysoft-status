package com.floreysoft.status.entity;

import com.floreysoft.status.Test;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;

public class TestEntity extends AbstractEntity {
	public static final String KIND = "Test";
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	public static final String URL = "url";
	public static final String INTERVAL = "interval";
	public static final String ENABLED = "enabled";
	
	public TestEntity(Entity entity) {
		super(entity);
	}

	public TestEntity(Test test) {
		this(new Entity(KIND));
		update(test);
	}

	public void setName(String name) {
		entity.setProperty(NAME, name);
	}

	public String getName() {
		return (String)entity.getProperty(NAME);
	}

	public void setDescription(String description) {
		entity.setUnindexedProperty(DESCRIPTION, description);
	}

	public String getDescription() {
		return (String)entity.getProperty(DESCRIPTION);
	}

	public void setUrl(String url) {
		entity.setProperty(URL, url);
	}

	public String getUrl() {
		return (String)entity.getProperty(URL);
	}

	public void setInterval(int interval) {
		entity.setProperty(INTERVAL, interval);
	}

	public int getInterval() {
		Long interval = (Long)entity.getProperty(INTERVAL);
		return interval == null ? 0 : interval.intValue();
	}

	public void setEnabled(boolean enabled) {
		entity.setProperty(ENABLED, enabled);
	}

	public boolean isEnabled() {
		Boolean enabled = (Boolean)entity.getProperty(ENABLED);
		return enabled == null ? false: enabled;
	}
	
	public void update(Test test) {
		setName(test.getName());
		setDescription(test.getDescription());
		setUrl(test.getUrl());
		setInterval(test.getInterval());
		setEnabled(test.isEnabled());
		setName(test.getName());
		setName(test.getName());
	}
	
	public Test toTest() {
		return new Test(KeyFactory.keyToString(getEntity().getKey()), getName(), getDescription(), getUrl(), getInterval(), !isEnabled());
	}
}