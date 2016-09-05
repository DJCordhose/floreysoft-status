package com.floreysoft.status.entity;

import com.google.appengine.api.datastore.Entity;

public abstract class TransientEntity extends AbstractEntity {
	public final static String TIMESTAMP = "ts";

	public TransientEntity(Entity entity) {
		super(entity);
	}
	
	public long getTimestamp() {
		return (Long) entity.getProperty(TIMESTAMP);
	}

	public void setTimestamp(long timestamp) {
		entity.setProperty(TIMESTAMP, timestamp);
	}
}