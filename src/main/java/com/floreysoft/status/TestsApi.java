package com.floreysoft.status;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Named;
import javax.servlet.ServletException;

import com.floreysoft.status.dto.Report;
import com.floreysoft.status.dto.Test;
import com.floreysoft.status.entity.TestRunEntity;
import com.floreysoft.status.entity.TestEntity;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;

@Api(name = "status", version = "v1", scopes = { Constants.EMAIL_SCOPE }, clientIds = { Constants.WEB_CLIENT_ID }, audiences = { Constants.ANDROID_AUDIENCE })
public class TestsApi {
	private static final Logger logger = Logger.getLogger(TestsApi.class.getName());
	private Datastore datastore = new Datastore();
	private TestService testService = new TestService();

	@ApiMethod(name = "test", httpMethod = "get")
	public Test getTest(@Named("id") String id) throws NotFoundException {
		try {
			return new TestEntity(datastore.get(KeyFactory.stringToKey(id))).toTest();
		} catch (Exception e) {
			throw new NotFoundException("Test not found with id: " + id);
		}
	}

	@ApiMethod(name = "tests", httpMethod = "get")
	public List<Test> listTests() {
		return testService.listTests();
	}

	@ApiMethod(name = "reports", httpMethod = "get")
	public List<Report> listCurrentReports() {
		return testService.listCurrentReports();
	}

	@ApiMethod(name = "test.add", httpMethod = "post")
	public Test addTest(User user, Test test) throws UnauthorizedException {
		checkUser(user);
		TestEntity testEntity = new TestEntity(test);
		try {
			datastore.put(testEntity);
		} catch (ServletException e) {
			logger.log(Level.SEVERE, "Failed to add test!", e);
		}
		return testEntity.toTest();
	}

	private void checkUser(User user) throws UnauthorizedException {
		if (user == null) {
			throw new UnauthorizedException("Method needs authenticated user");
		}
		if (!user.getEmail().equals("oliver.zeigermann@floreysoft.net") && !user.getEmail().equals("daniel.florey@floreysoft.net")
				&& !user.getEmail().equals("example@example.com")) {
			throw new UnauthorizedException("You do not have admin permissions");
		}
	}

	@ApiMethod(name = "test.save", httpMethod = "put")
	public Test saveTest(User user, Test test) throws UnauthorizedException {
		checkUser(user);
		try {
			TestEntity testEntity = new TestEntity(datastore.get(KeyFactory.stringToKey(test.getId())));
			testEntity.update(test);
			datastore.put(testEntity);
			return testEntity.toTest();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to save test!", e);
			return null;
		}
	}

	@ApiMethod(name = "test.delete", httpMethod = "delete")
	public Test deleteTest(User user, @Named("id") String id) throws UnauthorizedException {
		checkUser(user);
		try {
			datastore.delete(KeyFactory.stringToKey(id));
		} catch (ServletException e) {
			logger.log(Level.SEVERE, "Failed to delete test with id="+id, e);
		}
		return null;
	}
}