package com.floreysoft.status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.floreysoft.status.entity.TransientEntity;
import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

/**
 * @author Daniel Florey
 * 
 */
@SuppressWarnings("serial")
public class CleanupEntitiesServlet extends HttpServlet {
	private static final long EXECUTION_TIME_THRESHOLD = 25000;

	public static final String KIND_PARAMETER = "kind";
	public static final String TIMEOUT_IN_HOURS_PARAMETER = "tohrs";
	private static final Logger logger = Logger.getLogger(CleanupEntitiesServlet.class.getName());
	private final static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private final static AsyncDatastoreService asyncDatastore = DatastoreServiceFactory.getAsyncDatastoreService();
	private final static String CLEANUP_ENTITIES_TASK = "/cleanup";
	private final static String CLEANUP_QUEUE = "cleanup";

	@Override
	public void init(ServletConfig config) throws ServletException {
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		long timestampStart = System.currentTimeMillis();
		String kind = req.getParameter(KIND_PARAMETER);
		Query query = new Query(kind);
		String timeoutInHours = req.getParameter(TIMEOUT_IN_HOURS_PARAMETER);
		logger.log(Level.INFO, "Removing entites of kind=" + kind);
		if (timeoutInHours != null) {
			logger.log(Level.INFO, "Removing entites of kind=" + kind + " older than " + timeoutInHours + " hours");
			Long timeout = Long.valueOf(timeoutInHours) * 3600000;
			query.setFilter(FilterOperator.LESS_THAN.of(TransientEntity.TIMESTAMP, System.currentTimeMillis() - timeout));
		}
		query.setKeysOnly();
		ArrayList<Key> keysToDelete = new ArrayList<Key>();
		for (Entity entity : datastore.prepare(query).asIterable(FetchOptions.Builder.withChunkSize(500))) {
			keysToDelete.add(entity.getKey());
			if (keysToDelete.size() > 500) {
				asyncDatastore.delete(keysToDelete);
				keysToDelete = new ArrayList<Key>();
			}
			if ((System.currentTimeMillis() - timestampStart) > EXECUTION_TIME_THRESHOLD) {
				asyncDatastore.delete(keysToDelete);
				logger.log(Level.INFO, "Tasks execution took " + (System.currentTimeMillis() - timestampStart) + " ms so far, deferring execution");
				Queue collectGarbageQueue = QueueFactory.getQueue(CLEANUP_QUEUE);
				TaskOptions task = TaskOptions.Builder.withUrl(CLEANUP_ENTITIES_TASK).method(TaskOptions.Method.GET).param(KIND_PARAMETER, kind);
				if (timeoutInHours != null) {
					task.param(TIMEOUT_IN_HOURS_PARAMETER, timeoutInHours);
				}
				collectGarbageQueue.add(task);
				return;
			}
		}
		asyncDatastore.delete(keysToDelete);
	}
}