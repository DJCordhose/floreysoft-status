package com.floreysoft.status;

import com.floreysoft.status.dto.Report;
import com.floreysoft.status.dto.Test;
import com.floreysoft.status.entity.Status;
import com.floreysoft.status.entity.TestEntity;
import com.floreysoft.status.entity.TestRunEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;

import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestDao {
    private static final Logger logger = Logger.getLogger(TestDao.class.getName());
    private Datastore datastore = new Datastore();

    public List<TestEntity> listTests() {
        List<TestEntity> tests = new ArrayList<TestEntity>();
        try {
            List<Entity> entities = datastore.query(new Query(TestEntity.KIND));
            for (Entity entity : entities) {
                tests.add(new TestEntity(entity));
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load tests!", e);
        }
        return tests;
    }

    public TestRunEntity getLatestTestRunForTest(TestEntity test) {
        try {
            final Query query = new Query(TestRunEntity.KIND);
            query.addSort(TestRunEntity.STATUS); // for
            query.addSort(TestRunEntity.TIMESTAMP, Query.SortDirection.DESCENDING);
            query.setFilter(Query.CompositeFilterOperator.and(
                            new Query.FilterPredicate(TestRunEntity.TEST_KEY, Query.FilterOperator.EQUAL, test.getKey()),
                            new Query.FilterPredicate(TestRunEntity.STATUS, Query.FilterOperator.NOT_EQUAL, Status.UNINITIALIZED.name())));
            List<Entity> entities = datastore.query(query, FetchOptions.Builder.withLimit(1));
            if (entities.size() != 0) {
                final Entity entity = entities.get(0);
                return new TestRunEntity(entity);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load reports!", e);
        }
        return null;
    }

    public TestRunEntity addTestRun(TestEntity test, Status status) {
        TestRunEntity testRunEntity = new TestRunEntity(test, status);
        try {
            datastore.put(testRunEntity);
        } catch (ServletException e) {
            logger.log(Level.SEVERE, "Failed to add test!", e);
        }
        return testRunEntity;
    }
}
