package com.floreysoft.status;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.FetchOptions.Builder;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.mail.MailService.Message;
import com.google.appengine.api.mail.MailServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class Datastore {
	protected static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	private final static Logger logger = Logger.getLogger(Datastore.class.getName());

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

	public LicenseEntity getLicense(final String licensee) throws ServletException {
		return retry(new RunnableWithResult<LicenseEntity>() {
			@Override
			public LicenseEntity run() throws Exception {
				Key key = KeyFactory.createKey(LicenseEntity.KIND, licensee);
				try {
					return new LicenseEntity(datastore.get(key));
				} catch (EntityNotFoundException e) {
					return null;
				}
			}
		}, DATASTORE_RETRY_COUNT);
	}

	public LicenseEntity createTrialLicense(final String licensee) throws ServletException {
		return retry(new RunnableWithResult<LicenseEntity>() {
			@Override
			public LicenseEntity run() throws Exception {
				LicenseEntity trialLicense = createTrialLicense(new Entity(KeyFactory.createKey(LicenseEntity.KIND, licensee)));
				put(trialLicense);
				return trialLicense;
			}
		}, DATASTORE_RETRY_COUNT);
	}

	public LicenseEntity createEnterpriseLicense(final String licensee) throws ServletException {
		return retry(new RunnableWithResult<LicenseEntity>() {
			@Override
			public LicenseEntity run() throws Exception {
				LicenseEntity trialLicense = createEnterpriseLicense(new Entity(KeyFactory.createKey(LicenseEntity.KIND, licensee)));
				put(trialLicense);
				return trialLicense;
			}
		}, DATASTORE_RETRY_COUNT);
	}

	public LicenseEntity createFreeLicense(LicenseEntity licenseEntity) {
		licenseEntity.setTrial(false);
		licenseEntity.setEdition(FREE);
		licenseEntity.setExpires(0);
		return licenseEntity;
	}

	public void removeAllLicenses() throws ServletException {
		retry(new Runnable() {
			@Override
			public void run() throws Exception {
				logger.log(Level.INFO, "Removing all licenses from datastore");
				Query query = new Query(LicenseEntity.KIND);
				query.setKeysOnly();
				Iterable<Entity> entities = datastore.prepare(query).asIterable(FetchOptions.Builder.withDefaults());
				if (entities != null) {
					for (Entity entity : entities) {
						logger.log(Level.INFO, "Removing licenses with key=" + entity.getKey().getName());
						datastore.delete(entity.getKey());
					}
				}
			}
		}, DATASTORE_RETRY_COUNT);
	}

	public Application getApplication(final String name) throws ServletException {
		return retry(new TransactionableWithResult<Application>() {
			@Override
			public Application run(Transaction tx) throws Exception {
				String version = AppCoreServlet.getParameterStringValue(VERSION);
				float versionValue = Float.valueOf(version);
				try {
					Entity entity = tx != null ? datastore.get(tx, KeyFactory.createKey(Application.KIND, name)) : datastore.get(KeyFactory.createKey(
							Application.KIND, name));
					Application application = createApplication(entity);
					String datastoreVersion = application.getVersion();
					float datastoreVersionValue = 0;
					try {
						datastoreVersionValue = Float.valueOf(datastoreVersion);
					} catch (NumberFormatException e) {
						logger.log(Level.WARNING, "Version in datastore is corrupt. Value=" + datastoreVersion);
					}
					if (datastoreVersionValue < versionValue) {
						migrateDatastore(datastoreVersionValue, versionValue);
					}
					if (datastoreVersionValue != versionValue) {
						application.setVersion(version);
						put(tx, application);
					}
					return application;
				} catch (EntityNotFoundException e) {
					migrateDatastore(0, versionValue);
					Entity entity = new Entity(Application.KIND, name);
					Application application = createApplication(entity);
					initApplication(application);
					put(tx, application);
					return application;
				}
			}
		}, DATASTORE_RETRY_COUNT);
	}

	public Account createAccount(final String email, final HttpServletRequest req) throws ServletException {
		return retry(new RunnableWithResult<Account>() {
			@Override
			public Account run() throws Exception {
				Entity entity = new Entity(KeyFactory.createKey(Account.KIND, email));
				Account account = createAccount(entity);
				initAccount(account);
				account.setRegistrationDate(new Date());
				account.setDisabled(false);
				put(account);
				return account;
			}
		}, DATASTORE_RETRY_COUNT);
	}

	public Account getAccount(String email) throws ServletException {
		return getAccount(null, email);
	}

	public Account getAccount(final Transaction tx, final String email) throws ServletException {
		return retry(new RunnableWithResult<Account>() {
			@Override
			public Account run() throws Exception {
				try {
					if (tx == null) {
						return createAccount(datastore.get(KeyFactory.createKey(Account.KIND, email)));
					} else {
						return createAccount(datastore.get(tx, KeyFactory.createKey(Account.KIND, email)));
					}
				} catch (EntityNotFoundException e) {
					return null;
				}
			}
		}, DATASTORE_RETRY_COUNT);
	}

	public boolean isAccountsLimitExceeded(final long maxAccounts) throws ServletException {
		if (maxAccounts == 0) {
			return false;
		}
		return retry(new RunnableWithResult<Boolean>() {
			@Override
			public Boolean run() throws Exception {
				Query query = new Query(Account.KIND);
				query.setKeysOnly();
				List<Entity> listOfKeys = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(Integer.MAX_VALUE).chunkSize(1000));
				int numberOfAccounts = listOfKeys.size();
				if (numberOfAccounts > maxAccounts) {
					return true;
				} else {
					return false;
				}
			}
		}, DATASTORE_RETRY_COUNT);
	}

	public Domain createDomain(final String name) throws ServletException {
		return retry(new RunnableWithResult<Domain>() {
			@Override
			public Domain run() throws Exception {
				Entity entity = new Entity(KeyFactory.createKey(Domain.KIND, name));
				Domain domain = createDomain(entity);
				initDomain(domain);
				put(domain);
				return domain;
			}
		}, DATASTORE_RETRY_COUNT);
	}

	public Domain getDomain(final String name) throws ServletException {
		return retry(new RunnableWithResult<Domain>() {
			@Override
			public Domain run() throws Exception {
				try {
					return createDomain(datastore.get(KeyFactory.createKey(Domain.KIND, name)));
				} catch (EntityNotFoundException e) {
					return null;
				}
			}
		}, DATASTORE_RETRY_COUNT);
	}

	public Settings createSettings(final String email) throws ServletException {
		return retry(new RunnableWithResult<Settings>() {
			@Override
			public Settings run() throws Exception {
				Entity entity = new Entity(KeyFactory.createKey(Settings.KIND, email));
				Settings settings = new Settings(entity);
				put(settings);
				return settings;
			}
		}, DATASTORE_RETRY_COUNT);
	}

	public Settings getSettings(final String email) throws ServletException {
		return retry(new RunnableWithResult<Settings>() {
			@Override
			public Settings run() throws Exception {
				if (email == null || email.length() == 0) {
					logger.log(Level.WARNING, "Settings for null or empty email requested!");
					return null;
				}
				try {
					return new Settings(datastore.get(KeyFactory.createKey(Settings.KIND, email)));
				} catch (EntityNotFoundException e) {
					return createSettings(email);
				}
			}
		}, DATASTORE_RETRY_COUNT);
	}

	public String getLicensee(Account account) throws ServletException {
		String licensee = null;
		LicenseEntity license = null;
		if (account.getDomain() != null && !account.getDomain().equals("gmail.com")) {
			// Check if domain license is available
			String domain = account.getDomain();
			license = getLicense(domain);
			if (license != null) {
				logger.log(Level.INFO, "Licensee for account with email=" + account.getEmail() + " is domain=" + licensee);
				licensee = domain;
			}
		}
		if (licensee == null) {
			licensee = account.getEmail();
			logger.log(Level.INFO, "Licensee for account with email=" + account.getEmail() + " is account");
		}
		return licensee;
	}

	public LicenseEntity getLicense(Account account) throws ServletException {
		String email = account.getEmail();
		String domain = account.getDomain();
		String deployment = AppCoreServlet.getParameterStringValue(DEPLOYMENT);
		logger.log(Level.INFO, "Fetching license for user=" + email + ", domain=" + domain + ", deployment=" + deployment);
		if (deployment.equals(DEDICATED_DEPLOYMENT)) {
			// Get the only license that should be stored in the datastore
			Query query = new Query(LicenseEntity.KIND);
			Iterable<Entity> entities = datastore.prepare(query).asIterable(FetchOptions.Builder.withDefaults());
			if (entities != null) {
				for (Entity entity : entities) {
					return new LicenseEntity(entity);
				}
			}
			// No license found
			if (domain == null) {
				logger.log(Level.SEVERE, "Dedicated deployment detected, but no domain set on account");
				return null;
			} else {
				return createEnterpriseLicense(domain);
			}
		} else {
			LicenseEntity license = getLicense(email);
			if (domain != null) {
				logger.log(Level.INFO, "Checking license for domain=" + domain);
				LicenseEntity domainLicense = getLicense(domain);
				if (domainLicense != null) {
					// If user has both personal and enterprise license, prefer
					// enterprise license
					license = domainLicense;
					logger.log(Level.INFO, "Domain license found for domain=" + domain + ", expires=" + new Date(license.getExpires()));
				}
			}
			if (license == null) {
				// Create personal trial if user has no license at all
				license = createTrialLicense(email);
			}
			if (license.getExpires() > 0 && license.getExpires() < System.currentTimeMillis()) {
				logger.log(Level.INFO, "Trial expired, converting to free plan if available");
				license = createFreeLicense(license);
				put(license);
			}
			return license;
		}
	}

	public long getLicenseState(String licensee, String property) throws ServletException {
		return nextSequence(KeyFactory.createKey(SequenceEntity.KIND, LICENSE_PREFIX + licensee + ":" + property), SequenceEntity.MONTHLY_RESET, 0);
	}

	public LicenseState getLicenseState(License license) throws ServletException {
		return modifyLicenseState(license, null, 0);
	}

	public LicenseState modifyLicenseState(License license, String changedProperty, int increase) throws ServletException {
		String licensee = license.getLicensee();
		try {
			Map<String, Integer> state = new HashMap<String, Integer>();
			Map<String, String> properties = license.getProperties();
			if (properties != null) {
				for (String property : properties.keySet()) {
					if (property.equals(changedProperty)) {
						state.put(
								property,
								(int) nextSequence(KeyFactory.createKey(SequenceEntity.KIND, LICENSE_PREFIX + licensee + ":" + property),
										SequenceEntity.MONTHLY_RESET, increase));
					} else {
						state.put(
								property,
								(int) nextSequence(KeyFactory.createKey(SequenceEntity.KIND, LICENSE_PREFIX + licensee + ":" + property),
										SequenceEntity.MONTHLY_RESET, 0));
					}
				}
			}
			return new LicenseState(state, license);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to get license state for licensee=" + licensee, e);
			throw e;
		}
	}

	public void updateLicenseState(License license, String property, int increase) throws Exception {
		String licensee = license.getLicensee();
		long value = nextSequence(KeyFactory.createKey(SequenceEntity.KIND, LICENSE_PREFIX + licensee + ":" + property), SequenceEntity.MONTHLY_RESET, increase);
		onLicenseStatePropertyChanged(license.getLicensee(), property, (int) value);
	}

	public void resetLicenseState(License license, String property) {
		String licensee = license.getLicensee();
		try {
			Entity entity = datastore.get(KeyFactory.createKey(SequenceEntity.KIND, LICENSE_PREFIX + licensee + ":" + property));
			resetSequence(new SequenceEntity(entity));
			onLicenseStatePropertyChanged(licensee, property, 0);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Failed to reset license state for licensee=" + license + ", property=" + property);
		}
	}

	public void resetSequence(SequenceEntity sequenceEntity) {
		List<Key> counters = sequenceEntity.getCounters();
		if (counters != null && counters.size() > 0) {
			datastore.delete(counters);
			logger.log(Level.INFO, "Deleted " + counters.size() + " counters and sequence with name=" + sequenceEntity.getEntity().getKey().getName());
		}
		datastore.delete(sequenceEntity.getEntity().getKey());
	}

	public long nextSequence(Key key, long frequency, long increase) throws ServletException {
		SequenceEntity sequence = null;
		try {
			sequence = new SequenceEntity(datastore.get(key));
			logger.log(Level.INFO, "Sequence found with key=" + key.getName() + ", frequency=" + frequency);
			List<Key> counters = sequence.getCounters();
			if (counters == null) {
				counters = createSequence(key, frequency, sequence.getValue());
				logger.log(Level.INFO, "Migrating sequence with key=" + key + " to new counters");
			}
			if (increase > 0) {
				increaseCounter(counters, increase);
			}
			return sumCounters(counters);
		} catch (EntityNotFoundException e) {
			createSequence(key, frequency, increase);
			return increase;
		}
	}

	public void setSequence(Key key, long value) throws ServletException {
		SequenceEntity sequence = null;
		try {
			sequence = new SequenceEntity(datastore.get(key));
			logger.log(Level.INFO, "Setting sequence with key=" + key + " to value=" + value);
			List<Key> counters = sequence.getCounters();
			Map<Key, Entity> map = datastore.get(counters);
			int index = 0;
			for (Entity entities : map.values()) {
				entities.setUnindexedProperty(CounterEntity.VALUE, index == 0 ? value : 0L);
				datastore.put(entities);
				index++;
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to set sequence", e);
			throw new ServletException("Failed to set sequence! Reason: " + e.getMessage());
		}
	}

	public List<Key> createSequence(Key key, long frequency, long value) throws ServletException {
		// Create new counters to avoid datastore contention
		List<Key> counters = new ArrayList<Key>();
		for (int i = 0; i < SHAREDED_COUNTERS; i++) {
			CounterEntity counterEntity = new CounterEntity(new Entity(CounterEntity.KIND));
			counterEntity.setValue(i == 0 ? value : 0L);
			put(counterEntity);
			counters.add(counterEntity.getEntity().getKey());
		}
		SequenceEntity sequenceEntity = new SequenceEntity(new Entity(key));
		sequenceEntity.setResetFrequency(frequency);
		sequenceEntity.setCounters(counters);
		put(sequenceEntity);
		logger.log(Level.INFO, "New sequence created with key=" + key + ", value=" + value + ", sharded counters=" + counters.size());
		return counters;
	}

	private void increaseCounter(List<Key> counters, long increase) {
		int index = generator.nextInt(SHAREDED_COUNTERS);
		Key counterKey = counters.get(index);
		Transaction tx = datastore.beginTransaction();
		CounterEntity counterEntity;
		try {
			counterEntity = new CounterEntity(datastore.get(tx, counterKey));
			counterEntity.setValue(counterEntity.getValue() + increase);
			datastore.put(tx, counterEntity.getEntity());
			tx.commit();
		} catch (ConcurrentModificationException e) {
			logger.log(Level.WARNING, "Failed to increase shareded counter, try again", e);
			increaseCounter(counters, increase);
		} catch (EntityNotFoundException e) {
			logger.log(Level.SEVERE, "Failed to get shareded counter, try again", e);
			increaseCounter(counters, increase);
		}
	}

	public long sumCounters(List<Key> counters) {
		Map<Key, Entity> counterEntities = datastore.get(counters);
		long sum = 0;
		for (Entity entity : counterEntities.values()) {
			sum += (long) entity.getProperty(CounterEntity.VALUE);
		}
		return sum;
	}

	public void deleteAll(String kind) {
		Query query = new Query(kind);
		query.setKeysOnly();
		Collection<Key> keysToDelete = new ArrayList<Key>();
		while (true) {
			keysToDelete.clear();
			List<Entity> entities = datastore.prepare(query).asList(Builder.withChunkSize(100).limit(100));
			int count = 0;
			for (Entity entity : entities) {
				keysToDelete.add(entity.getKey());
				count++;
			}
			datastore.delete(keysToDelete);
			if (count < 100) {
				break;
			}
		}
	}

	public void licenseChanged(LicenseEntity licenseEntity) throws ServletException {
		License license = licenseEntity.toLicense();
		onLicenseStateChanged(license.getLicensee(), new LicenseState(license));
		put(licenseEntity);
	}

	public void resetDatastore() {
		logger.log(Level.INFO, "Resetting datastore!");
		Queue collectContactsQueue = QueueFactory.getQueue(DELETE_DATASTORE_QUEUE);
		collectContactsQueue.add(TaskOptions.Builder.withUrl(DELETE_DATASTORE_TASK).param("kind", Account.KIND).method(TaskOptions.Method.POST));
		collectContactsQueue.add(TaskOptions.Builder.withUrl(DELETE_DATASTORE_TASK).param("kind", Application.KIND).method(TaskOptions.Method.POST));
	}

	public long getLongProperty(final String name, final long defaultValue) throws ServletException {
		return retry(new RunnableWithResult<Long>() {
			@Override
			public Long run() throws Exception {
				try {
					Key key = KeyFactory.createKey(Property.KIND, name);
					Entity entity = datastore.get(key);
					Long value = (Long) entity.getProperty(Property.VALUE);
					if (value == null) {
						return defaultValue;
					}
					return value;
				} catch (EntityNotFoundException e) {
					return defaultValue;
				}
			}
		}, DATASTORE_RETRY_COUNT);
	}

	public void setLongProperty(final String name, final long value) throws ServletException {
		retry(new Runnable() {
			@Override
			public void run() throws Exception {
				Key key = KeyFactory.createKey(Property.KIND, name);
				Entity entity = null;
				try {
					entity = datastore.get(key);
				} catch (EntityNotFoundException e) {
					entity = new Entity(Property.KIND, name);
				}
				entity.setProperty(Property.VALUE, value);
				datastore.put(entity);
			}
		}, DATASTORE_RETRY_COUNT);
	}

	public long increaseProperty(final String name, final int delta) throws ServletException {
		return retry(new RunnableWithResult<Long>() {
			@Override
			public Long run() throws Exception {
				Transaction tx = datastore.beginTransaction();
				Entity entity;
				Key key = KeyFactory.createKey(Property.KIND, name);
				try {
					entity = datastore.get(tx, key);
				} catch (EntityNotFoundException e) {
					entity = new Entity(key);
				}
				Long number = (Long) entity.getProperty(Property.VALUE);
				if (number == null || number == 0) {
					number = 0L;
				}
				number += delta;
				entity.setProperty(Property.VALUE, number);
				put(tx, new Property(entity));
				tx.commit();
				return number;
			}
		}, DATASTORE_RETRY_COUNT);
	}

	public String getStringProperty(final String name, final String defaultValue) throws ServletException {
		return retry(new RunnableWithResult<String>() {
			@Override
			public String run() throws Exception {
				try {
					Key key = KeyFactory.createKey(Property.KIND, name);
					Entity entity = datastore.get(key);
					String value = (String) entity.getProperty(Property.VALUE);
					if (value == null) {
						return defaultValue;
					}
					return value;
				} catch (EntityNotFoundException e) {
					return defaultValue;
				}
			}
		}, DATASTORE_RETRY_COUNT);
	}

	public void setStringProperty(final String name, final String value) throws ServletException {
		retry(new Runnable() {
			@Override
			public void run() throws Exception {
				Key key = KeyFactory.createKey(Property.KIND, name);
				Entity entity = null;
				try {
					entity = datastore.get(key);
				} catch (EntityNotFoundException e) {
					entity = new Entity(Property.KIND, name);
				}
				entity.setProperty(Property.VALUE, value);
				datastore.put(entity);
			}
		}, DATASTORE_RETRY_COUNT);
	}

	public boolean getBooleanProperty(final String name, final boolean defaultValue) throws ServletException {
		return retry(new RunnableWithResult<Boolean>() {
			@Override
			public Boolean run() throws Exception {
				try {
					Key key = KeyFactory.createKey(Property.KIND, name);
					Entity entity = datastore.get(key);
					Boolean value = (Boolean) entity.getProperty(Property.VALUE);
					if (value == null) {
						return false;
					}
					return value;
				} catch (EntityNotFoundException e) {
					return defaultValue;
				}
			}
		}, DATASTORE_RETRY_COUNT);
	}

	public void setBooleanProperty(final String name, final boolean value) throws ServletException {
		retry(new Runnable() {
			@Override
			public void run() throws Exception {
				Key key = KeyFactory.createKey(Property.KIND, name);
				Entity entity = null;
				try {
					entity = datastore.get(key);
				} catch (EntityNotFoundException e) {
					entity = new Entity(Property.KIND, name);
				}
				entity.setProperty(Property.VALUE, value);
				datastore.put(entity);
			}
		}, DATASTORE_RETRY_COUNT);
	}

	public void sendUltradoc(Locale locale, Account account, String recipient, String key, Map<String,String> parameters, boolean sendOnlyOnce) throws Exception {
		boolean send = !sendOnlyOnce;
		if ( recipient == null ) {
			recipient = account.getEmail();				
		}
		List<String> notifications = account.getNotifications();
		if (!notifications.contains(key)) {
			account.addNotification(key);
			put(account);
			send = true;
		}
		if ( send ) {
			logger.log(Level.INFO, "Sending Ultraodc with key=" + key + " to " + recipient);
			Queue queue = QueueFactory.getQueue(SEND_NEWSLETTER_QUEUE);
			TaskOptions taskOptions = TaskOptions.Builder.withUrl(SEND_ULTRADOC_TASK).method(TaskOptions.Method.GET).param(PARAMETER_TO, recipient).param("key", key);
			if ( parameters != null ) {
				for (Entry<String, String> parameter : parameters.entrySet()) {
					logger.log(Level.INFO, "Adding parameter to task, key=" + parameter.getKey() + ", value= " + parameter.getValue());
					taskOptions.param(parameter.getKey(), parameter.getValue());
				}
			}
			queue.add(taskOptions);
		} else {
			logger.log(Level.INFO, "Notification to " + recipient + " has already been sent, skipping");
		}
	}

	public void sendNotification(Locale locale, Account account, String from, String cc, String bcc, String messageKey, String[] parameters) throws Exception {
		List<String> notifications = account.getNotifications();
		if (!notifications.contains(messageKey)) {
			account.addNotification(messageKey);
			logger.log(Level.INFO, "Sending notification to " + account.getEmail() + " as it has not been sent before");
			sendEmail(locale, from, account.getEmail(), cc, bcc, messageKey, parameters);
		} else {
			logger.log(Level.INFO, "Notification to " + account.getEmail() + " has already been sent, skipping");
		}
	}

	public Gmail getGmail(Account account) throws Exception {
		Credential credentials = AppCoreServlet.getCredential(account);
		return new Gmail.Builder(TRANSPORT, JSON_FACTORY, credentials).setApplicationName(getApplicationName()).build();
	}

	public GoogleAppsEmailSettings getGoogleAppsEmailSettings(Account account) throws Exception {
		Credential credentials = AppCoreServlet.getCredential(account);
		return new GoogleAppsEmailSettings(TRANSPORT, JSON_FACTORY, credentials);
	}

	public Directory getDirectory(Account account) throws Exception {
		Credential credentials = AppCoreServlet.getCredential(account);
		return new Directory.Builder(TRANSPORT, JSON_FACTORY, credentials).setApplicationName(getApplicationName()).build();
	}

	public void sendEmail(Locale locale, String from, String to, String cc, String bcc, String messageKey, String[] parameters) throws Exception {
		logger.log(Level.INFO, "Sending email from=" + from + " with message=" + messageKey + " to=" + to+", cc="+cc+", bcc="+bcc);
		String subject = MessageManager.getText(messageKey, "subject", parameters, locale);
		String body = MessageManager.getText(messageKey, "body", parameters, locale);
		body = body.replace("\\n", "\n");
		Message mail = new Message(from, to, subject, body);
		if ( cc != null ) {
			mail.setCc(cc);
		}
		if ( bcc != null ) {
			mail.setBcc(bcc);
		}
		MailServiceFactory.getMailService().send(mail);
	}

	protected Application createApplication(Entity entity) {
		return new Application(entity);
	}

	protected void initApplication(Application application) {
		String version = AppCoreServlet.getParameterStringValue(VERSION);
		application.setVersion(version);
	}

	protected Account createAccount(Entity entity) {
		return new Account(entity);
	}

	protected void initAccount(Account account) {
		account.setRegistrationDate(new Date());
		account.setDisabled(false);
	}

	protected Domain createDomain(Entity entity) {
		return new Domain(entity);
	}

	protected void initDomain(Domain domain) {
		domain.setRegistrationDate(new Date());
	}

	protected LicenseEntity createTrialLicense(Entity entity) {
		LicenseEntity licenseEntity = new LicenseEntity(entity);
		licenseEntity.setTrial(true);
		licenseEntity.setEdition(TRIAL);
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(GregorianCalendar.MONTH, 1);
		licenseEntity.setExpires(calendar.getTime().getTime());
		return licenseEntity;
	}

	protected LicenseEntity createEnterpriseLicense(Entity entity) {
		LicenseEntity licenseEntity = new LicenseEntity(entity);
		licenseEntity.setTrial(true);
		licenseEntity.setExpires(System.currentTimeMillis());
		return licenseEntity;
	}

	protected String[] getLicenseMonthlyProperties() {
		return new String[0];
	}

	protected void onLicenseStateChanged(String licensee, LicenseState licenseState) {
		AppCoreServlet.getChannelManager().fireEvent(new LicenseStateChangedEvent(licenseState), licensee);
	}

	protected void onLicenseStatePropertyChanged(String licensee, String property, int value) {
		AppCoreServlet.getChannelManager().fireEvent(new LicenseStatePropertyChangedEvent(property, value), licensee);
	}

	protected void migrateDatastore(float installedVersion, float currentVersion) {
		logger.log(Level.INFO, "Migrating from version=" + installedVersion + " to " + currentVersion);
	}

	protected String getAccessDeniedPage(Locale locale) {
		return "Access denied!";
	}

	protected String getApplicationName() {
		return "floreysoft";
	}
}