package com.floreysoft.status;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import com.floreysoft.status.entity.AbstractEntity;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;

public class Datastore {
	protected static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	private final static Logger logger = Logger.getLogger(Datastore.class.getName());
	private final static int DATASTORE_RETRY_COUNT = 5;

	public static interface RunnableWithResult<T> {
		public T run() throws Exception;
	}

	public static interface Runnable {
		public void run() throws Exception;
	}

	public static interface TransactionableWithResult<T> {
		public T run(Transaction tx) throws Exception;
	}

	public static interface Transactionable {
		public void run(Transaction tx) throws Exception;
	}

	public static void retry(Runnable r, int retries) throws ServletException {
		for (int i = 0; i < retries; i++) {
			try {
				r.run();
				return;
			} catch (ConcurrentModificationException e) {
				logger.log(Level.WARNING, "Operation failed due to optimistic locking on datastore, retry in " + i + " seconds", e);
				try {
					Thread.currentThread().sleep(i * 1000);
				} catch (InterruptedException ie) {
					logger.log(Level.WARNING, "Failed to delay retry", ie);
				}
			} catch (Exception e) {
				logger.log(Level.WARNING, "Operation failed due to internal error, aborting", e);
				throw new ServletException(e);
			}
		}
	}

	public static <T> T retry(RunnableWithResult<T> r, int retries) throws ServletException {
		for (int i = 0; i < retries; i++) {
			try {
				return r.run();
			} catch (ConcurrentModificationException e) {
				logger.log(Level.WARNING, "Operation failed due to optimistic locking on datastore, retry in " + i + " seconds", e);
				try {
					Thread.currentThread().sleep(i * 1000);
				} catch (InterruptedException ie) {
					logger.log(Level.WARNING, "Failed to delay retry", ie);
				}
			} catch (Exception e) {
				logger.log(Level.WARNING, "Operation failed due to internal error, aborting", e);
				throw new ServletException(e);
			}
		}
		return null;
	}

	public static void retry(Transactionable t, int retries) throws ServletException {
		for (int i = 0; i < retries; i++) {
			Transaction tx = datastore.beginTransaction();
			try {
				t.run(tx);
				tx.commit();
			} catch (ConcurrentModificationException e) {
				logger.log(Level.WARNING, "Operation failed due to optimistic locking on datastore, retry in " + i + " seconds", e);
				try {
					Thread.currentThread().sleep(i * 1000);
				} catch (InterruptedException ie) {
					logger.log(Level.WARNING, "Failed to delay retry", ie);
				}
			} catch (Exception e) {
				logger.log(Level.WARNING, "Operation failed due to internal error, aborting", e);
				throw new ServletException(e);
			} finally {
				if (tx.isActive()) {
					tx.rollback();
				}
			}
		}
	}

	public static <T> T retry(TransactionableWithResult<T> t, int retries) throws ServletException {
		T result = null;
		for (int i = 0; i < retries; i++) {
			Transaction tx = datastore.beginTransaction();
			try {
				result = t.run(tx);
				tx.commit();
			} catch (ConcurrentModificationException e) {
				logger.log(Level.WARNING, "Operation failed due to optimistic locking on datastore, retry in " + i + " seconds", e);
				try {
					Thread.currentThread().sleep(i * 1000);
				} catch (InterruptedException ie) {
					logger.log(Level.WARNING, "Failed to delay retry", ie);
				}
			} catch (Exception e) {
				logger.log(Level.WARNING, "Operation failed due to internal error, aborting", e);
				throw new ServletException(e);
			} finally {
				if (tx.isActive()) {
					tx.rollback();
				}
			}
		}
		return result;
	}

	public DatastoreService getDatastore() {
		return datastore;
	}

	public Entity get(final Key key) throws Exception {
		return retry(new RunnableWithResult<Entity>() {
			@Override
			public Entity run() throws Exception {
				return datastore.get(key);
			}
		}, DATASTORE_RETRY_COUNT);
	}

	public void put(AbstractEntity abstractEntity) throws ServletException {
		put(null, abstractEntity);
	}

	public void put(final Transaction tx, final AbstractEntity abstractEntity) throws ServletException {
		retry(new Runnable() {
			@Override
			public void run() throws Exception {
				abstractEntity.save(datastore, tx);
			}
		}, DATASTORE_RETRY_COUNT);
	}

	public void delete(AbstractEntity abstractEntity) throws ServletException {
		try {
			abstractEntity.delete(datastore, null);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to delete entity with key=" + abstractEntity.getEntity().getKey().getName());
			throw new ServletException(e.getMessage());
		}
	}

	public void delete(Transaction tx, AbstractEntity abstractEntity) throws ServletException {
		try {
			abstractEntity.delete(datastore, tx);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to delete entity with key=" + abstractEntity.getEntity().getKey().getName());
			throw new ServletException(e.getMessage());
		}
	}

	public void delete(Key key) throws ServletException {
		delete(null, key);
	}

	public void delete(final Transaction tx, final Key key) throws ServletException {
		retry(new Runnable() {
			@Override
			public void run() throws Exception {
				if (tx != null) {
					datastore.delete(tx, key);
				} else {
					datastore.delete(key);
				}
			}
		}, DATASTORE_RETRY_COUNT);
	}

	public List<Entity> query(final Query query, final FetchOptions options) throws Exception {
		return retry(new RunnableWithResult<List<Entity>>() {
			@Override
			public List<Entity> run() throws Exception {
				return datastore.prepare(query).asList(options);
			}
		}, DATASTORE_RETRY_COUNT);
	}

	public List<Entity> query(final Query query) throws Exception {
		return query(query, FetchOptions.Builder.withDefaults());
	}

}