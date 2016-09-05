package com.floreysoft.status.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.client.util.Base64;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Transaction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

public abstract class AbstractEntity {
	public static final Logger logger = Logger.getLogger(AbstractEntity.class.getName());
	protected static final String LARGE_PROPERTY = "LargeProperty";
	protected static final String LARGE_VALUE = "v";
	protected static final int DEFAULT_THRESHOLD = 250000;
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	protected Map<String, Object> largeProperties = new HashMap<String, Object>();
	protected Set<String> smallProperties = new HashSet<String>();
	protected Entity entity;

	private Gson gson;

	class ByteArrayToBase64TypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
		public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			return Base64.decodeBase64(json.getAsString());
		}

		public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(Base64.encodeBase64String(src));
		}
	}

	public AbstractEntity(Entity entity) {
		this.entity = entity;
		gson = new GsonBuilder().registerTypeHierarchyAdapter(byte[].class, new ByteArrayToBase64TypeAdapter()).create();
	}

	public final Entity getEntity() {
		return entity;
	}

	public String[] getLargePropertyNames() {
		return null;
	}

	public Set<String> getSmallPropertyNames() {
		return smallProperties;
	}

	protected void setLargeProperty(String propertyName, Object object, int size, int threshold) {
		if (size > threshold) {
			largeProperties.put(propertyName, object);
			entity.removeProperty(propertyName);
			logger.log(Level.INFO, "Large property=" + propertyName + " with size=" + size
					+ " detected, removing inner property and create a subentity on next put()");
		} else {
			largeProperties.remove(propertyName);
			entity.setUnindexedProperty(propertyName, object);
			smallProperties.add(propertyName);
			logger.log(Level.INFO, "Large property=" + propertyName + " with size=" + size
					+ " is within threshold, removing subproperty and created an inner  property on next put()");
		}
	}

	protected Object getLargeProperty(String propertyName) {
		Object value = entity.getProperty(propertyName);
		if (value == null) {
			try {
				Entity largeProperty = datastore.get(KeyFactory.createKey(entity.getKey(), LARGE_PROPERTY, propertyName));
				value = largeProperty.getProperty(LARGE_VALUE);
			} catch (EntityNotFoundException e) {
				return null;
			}
		}
		return value;
	}

	protected void setObjectAsJson(String propertyName, Object object) {
		setObjectAsJson(propertyName, object, DEFAULT_THRESHOLD);
	}

	protected void setObjectAsJson(String propertyName, Object object, int threshold) {
		String json = gson.toJson(object);
		setLargeProperty(propertyName, new Text(json), json.length(), DEFAULT_THRESHOLD);
	}

	protected Object getObjectFromJson(String propertyName, Class clazz) {
		Text json = (Text) getLargeProperty(propertyName);
		return json == null ? null : gson.fromJson(json.getValue(), clazz);
	}

	protected Object getObjectFromJson(String propertyName, TypeToken typeToken) {
		Text json = (Text) getLargeProperty(propertyName);
		return json == null ? null : gson.fromJson(json.getValue(), typeToken.getType());
	}

	protected void setObject(String propertyName, Object object) {
		setObject(propertyName, object, DEFAULT_THRESHOLD);
	}

	protected void setObject(String propertyName, Object object, int threshold) {
		try {
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			ObjectOutputStream outputStream = new ObjectOutputStream(byteStream);
			outputStream.writeObject(object);
			outputStream.close();
			setLargeProperty(propertyName, new Blob(byteStream.toByteArray()), byteStream.size(), threshold);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to serialize object to datastore", e);
		}
	}

	protected Object getObject(String propertyName) {
		try {
			Blob serializedObject = (Blob) getLargeProperty(propertyName);
			if (serializedObject != null) {
				ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(serializedObject.getBytes()));
				return inputStream.readObject();
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to deserialize object from datastore", e);
		}
		return null;
	}

	public void save(DatastoreService datastore, Transaction tx) throws Exception {
		datastore.put(tx, entity);
		for (Entry<String, Object> largeProperty : largeProperties.entrySet()) {
			Entity property = new Entity(AbstractEntity.LARGE_PROPERTY, largeProperty.getKey(), entity.getKey());
			property.setUnindexedProperty(AbstractEntity.LARGE_VALUE, largeProperty.getValue());
			datastore.put(tx, property);
		}
		Set<String> smallPropertyNames = getSmallPropertyNames();
		if (smallPropertyNames != null) {
			for (String smallPropertyName : smallPropertyNames) {
				datastore.delete(tx, KeyFactory.createKey(entity.getKey(), AbstractEntity.LARGE_PROPERTY, smallPropertyName));
			}
		}
	}

	public void delete(DatastoreService datastore, Transaction tx) throws Exception {
		if (tx != null) {
			datastore.delete(tx, entity.getKey());
		} else {
			datastore.delete(entity.getKey());
		}
	}
}