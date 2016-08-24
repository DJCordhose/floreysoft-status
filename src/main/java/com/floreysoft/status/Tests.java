package com.floreysoft.status;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.users.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Named;

@Api(name = "status",
        version = "v1",
        scopes = {Constants.EMAIL_SCOPE},
        clientIds = {Constants.WEB_CLIENT_ID},
        audiences = {Constants.ANDROID_AUDIENCE}
)
public class Tests {
    public static List<Test> mockTests = new ArrayList<Test>();
    public static List<Report> mockReports = new ArrayList<Report>();

    static {
        mockTests.add(new Test(1, "Test1", "Ein Test", "url1", 5, false));
        mockTests.add(new Test(2, "Test2", "Noch ein Test", "url2", 10, true));
        mockReports.add(new Report("Test1", "Ein Test", new Date().getTime(), Status.OK));
        mockReports.add(new Report("Test2", "Zweiter Test", new Date().getTime(), Status.SLOW));
        mockReports.add(new Report("Test3", "Dritter Test", new Date().getTime(), Status.FAIL));
    }

    public Test getTest(@Named("id") Integer id) throws NotFoundException {
        for (Test test : mockTests) {
            if (test.id == id) {
                return test;
            }
        }
        throw new NotFoundException("Test not found with id: " + id);
    }

    public List<Test> listTests() {
        return mockTests;
    }

    public List<Report> listCurrentReports() {
        return mockReports;
    }

    @ApiMethod(name = "test.add", httpMethod = "post")
    public Test addTest(User user, Test test) throws UnauthorizedException {
        checkUser(user);
        mockTests.add(test);
        return test;
    }

    private void checkUser(User user) throws UnauthorizedException {
        if (user == null) {
            throw new UnauthorizedException("Method needs authenticated user");
        }
        if (!user.getEmail().equals("oliver.zeigermann@floreysoft.net")
                && !user.getEmail().equals("daniel.florey@floreysoft.net")
                && !user.getEmail().equals("example@example.com")) {
            throw new UnauthorizedException("You do not have admin permissions");
        }
    }

    @ApiMethod(name = "test.save", httpMethod = "put")
    public Test saveTest(User user, Test test) throws UnauthorizedException {
        checkUser(user);
        this.deleteTest(user, test.id);
        return this.addTest(user, test);
    }

    @ApiMethod(name = "test.delete", httpMethod = "delete")
    public Test deleteTest(User user, @Named("id") Integer id) throws UnauthorizedException {
        checkUser(user);
        int index = 0;
        for (Test test : mockTests) {
            if (test.id == id) {
                mockTests.remove(index);
                return test;
            }
            index++;
        }
        return null;
    }
}
