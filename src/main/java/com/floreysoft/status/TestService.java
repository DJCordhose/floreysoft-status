package com.floreysoft.status;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.floreysoft.status.dto.Report;
import com.floreysoft.status.dto.Test;
import com.floreysoft.status.entity.Status;
import com.floreysoft.status.entity.TestEntity;
import com.floreysoft.status.entity.TestRunEntity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.appengine.repackaged.org.joda.time.IllegalFieldValueException;

public class TestService {
    private static final Logger logger = Logger.getLogger(TestService.class.getName());
    private static final URLFetchService urlFetchService = URLFetchServiceFactory.getURLFetchService();
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

    public void setGoldenMaster(List<Test> tests) throws IOException {
        for (Test test : tests) {
            final TestEntity testEntity = new TestEntity(test);
            if (testEntity.isEnabled()) {
                final String url = testEntity.getUrl();
                HTTPResponse response = urlFetchService.fetch(new URL(url));
                byte[] content = response.getContent();
                testEntity.setGoldenMaster(content);
            }
        }
    }

    // no side effects
    private Status executeSampleRun(TestEntity test) {
        final String url = test.getUrl();
        logger.log(Level.INFO, "Running test against: " + url);
        final byte[] goldenMaster = test.getGoldenMaster();
        if (goldenMaster == null || goldenMaster.length == 0) {
            logger.log(Level.WARNING, "Test not initialized for: " + url);
            return Status.UNINITIALIZED;
        }
        long startTime = System.currentTimeMillis();
        try {
            HTTPResponse response = urlFetchService.fetch(new URL(url));
            byte[] content = response.getContent();
            if (!Arrays.equals(content, goldenMaster)) {
                return Status.FAIL;
            }
        } catch (SocketTimeoutException ste) {
            return Status.TIMED_OUT;
        } catch (MalformedURLException e) {
            return Status.INVALID;
        } catch (IOException e) {
            return Status.INVALID;
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        if (isSlow(test, duration)) {
            return Status.SLOW;
        }
        return Status.OK;
    }

    private boolean isSlow(TestEntity test, long durationinMs) {
        // FIXME: should check against median of all samples
        return false;
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