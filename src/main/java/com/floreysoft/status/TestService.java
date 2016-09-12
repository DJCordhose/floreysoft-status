package com.floreysoft.status;

import com.floreysoft.status.dto.Report;
import com.floreysoft.status.dto.Test;
import com.floreysoft.status.entity.Status;
import com.floreysoft.status.entity.TestEntity;
import com.floreysoft.status.entity.TestRunEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestService {
    private static final Logger logger = Logger.getLogger(TestService.class.getName());
    private TestDao testDao = new TestDao();

    public void sampleTests() {
        final List<TestEntity> testEntities = testDao.listTests();
        for (TestEntity test : testEntities) {
            if (test.isEnabled()) {
                Status status = executeSampleRun(test);
                testDao.addTestRun(test, status);
            }
        }
    }

    public Status executeSampleRun(TestEntity test) {
        final String url = test.getUrl();
        logger.log(Level.INFO, "Running test against: " + url);
        final String goldenMaster = test.getGoldenMaster();
        if (goldenMaster == null || goldenMaster.length() == 0) {
            logger.log(Level.WARNING, "Test not initialized for: " + url);
            // FIXME: commented out for testing only
//            return Status.UNINITIALIZED;
        }
        // FIXME: should call url and compare to golden master to create status
        return Status.OK;
    }

    public List<Test> listTests() {
        List<Test> tests = new ArrayList<Test>();
        final List<TestEntity> testEntities = testDao.listTests();
        for (TestEntity entity : testEntities) {
            tests.add(entity.toTest());
        }
        return tests;
    }

    public List<Report> listCurrentReports() {
        List<Report> reports = new ArrayList<Report>();
        final List<TestEntity> testEntities = testDao.listTests();
        for (TestEntity test : testEntities) {
            TestRunEntity latestTestRunForTest = testDao.getLatestTestRunForTest(test);
            if (latestTestRunForTest != null) {
                Report report = new Report(KeyFactory.keyToString(latestTestRunForTest.getEntity().getKey()), test.getName(), test.getDescription(), latestTestRunForTest.getTimestamp(), latestTestRunForTest.getStatus());
                reports.add(report);
            }
        }
        return reports;
    }
}